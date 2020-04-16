package com.chopin.sunny.registry.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chopin.sunny.model.Concumer;
import com.chopin.sunny.model.ProviderService;
import com.chopin.sunny.registry.api.RegisterCenter;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Log4j2
public class ZkRegister implements RegisterCenter {

    private static ZkRegister zkRegister= new ZkRegister();
    private ConcurrentHashMap<String,List<ProviderService>> providerServiceMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,List<ProviderService>> providerServiceMap4Concumer = new ConcurrentHashMap<>();
    private String ZK_SERVERS;
    private String ZK_SESSION_TIME_OUT;
    private String ZK_CONNECTION_TIME_OUT;
    private String ZK_ROOT_PATH;
    private String PROVIDER_ROOT;
    private String CONCUMER_ROOT;
    private final RetryPolicy retryPolicy= new ExponentialBackoffRetry(1000, 3);

    private CuratorFramework providerClient;
    private CuratorFramework consumerClient;

    @Override
    public void init() {
        providerClient = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVERS)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(PROVIDER_ROOT)
                .build();
        consumerClient= CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVERS)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(CONCUMER_ROOT)
                .build();
    }

    @Override
    public void registerProvider(List<ProviderService> providerServices) {
        // 把provider 列表注册到zk，并写入本地map进行缓存
        if(providerServices==null || providerServices.isEmpty()){
            return ;
        }
        providerClient.start();
        try {
            providerServices.forEach(providerService -> {
                List<ProviderService> services = providerServiceMap.get(providerService.getServiceItf().getName());
                if(services==null){
                    services = new ArrayList<>();
                    providerServiceMap.put(providerService.getServiceItf().getName(),services);
                }
                services.add(providerService);
            });

            providerServiceMap.entrySet().forEach(map ->{
                try {
                    ProviderService server = map.getValue().get(0);
                    String node = ZK_ROOT_PATH+"/"+map.getKey()+"/"+PROVIDER_ROOT+"/"+server.getServerIp()+"/"+server.getServerPort()+"/"+server.getWeight();
                    Stat stat=providerClient.checkExists().forPath(node);
                    if(stat == null){
                        providerClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(node);
                        //TODO 设置data
                        providerClient.setData().forPath(node,map.getValue());
                    }
                } catch (Exception e) {
                   log.error("【生产者】注册失败 "+ JSONObject.toJSONString(map),e);
                }
            });

        }catch (Exception e){
            log.error("【生产者】注册失败 "+ JSONArray.toJSONString(providerServices) ,e);
        }finally {
            providerClient.close();
        }

    }

    @Override
    public void registerConcumer(List<Concumer> concumers) {
        //把consumer注册到zk
        if(concumers==null || concumers.isEmpty()){
            return ;
        }
        consumerClient.start();
        try {

            concumers.forEach(map ->{
                try {
                    String node = ZK_ROOT_PATH+"/"+CONCUMER_ROOT+"/"+map.getAppKey()+"/"+map.getServerIp()+"/"+map.getServerPort();
                    Stat stat=consumerClient.checkExists().forPath(node);
                    if(stat == null){
                        consumerClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(node);
                    }
                } catch (Exception e) {
                    log.error("【消费者】注册失败"+map.toString());
                }
            });
            //拉取zk上的provider列表，缓存到本地
            String ProviderNode =  ZK_ROOT_PATH+"/"+ PROVIDER_ROOT;
            List<String> nodes = consumerClient.getChildren().forPath(ProviderNode);
            List<ProviderService> providerList = nodes.stream().map(n->
                    n.split("/").length>5?
                    ProviderService.builder()
                            .appKey(n.split("/")[2])
                            .serverIp(n.split("/")[3])
                            .serverPort(Integer.valueOf(n.split("/")[4]))
                            .build() : null
            ).collect(Collectors.toList());

            providerList.forEach(providerService -> {
                List<ProviderService> services = providerServiceMap4Concumer.get(providerService.getServiceItf().getName());
                if(services==null){
                    services = new ArrayList<>();
                }
                services.add(providerService);
            });

        }catch (Exception e){
            log.error("【消费者】注册失败 "+ JSONArray.toJSONString(concumers) ,e);
        }finally {
            consumerClient.close();
        }

    }

    @Override
    public void subscribe(List<ProviderService> providerServices) {
        // 订阅zk节点
        if(providerServices==null || providerServices.isEmpty()){
            return ;
        }
        try {
            consumerClient.start();
            TreeCache cache = TreeCache.newBuilder(consumerClient, ZK_ROOT_PATH+"/"+CONCUMER_ROOT+"/").setCacheData(false).build();

            providerServices.forEach(x->{
                String node = ZK_ROOT_PATH+"/"+CONCUMER_ROOT+"/"+x.getAppKey()+"/"+x.getServerIp()+"/"+x.getServerPort();
                try {
                    cache.getListenable().addListener((c, event) -> {
                        if ( event.getData() != null )
                        {
                            List<ProviderService> providerList = nodes.stream().map(n->
                                    n.split("/").length>5?
                                            ProviderService.builder()
                                                    .appKey(n.split("/")[2])
                                                    .serverIp(n.split("/")[3])
                                                    .serverPort(Integer.valueOf(n.split("/")[4]))
                                                    .build() : null
                            ).collect(Collectors.toList());

                            providerList.forEach(providerService -> {
                                List<ProviderService> services = providerServiceMap4Concumer.get(providerService.getServiceItf().getName());
                                if(services==null){
                                    services = new ArrayList<>();
                                }
                                services.add(providerService);
                            });

                            //TODO 更新本地列表，执行回调

                            System.out.println("type=" + event.getType() + " path=" + event.getData().getPath());
                        }
                        else
                        {
                            System.out.println("type=" + event.getType());
                        }
                    });
                    cache.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unSubscribe(List<ProviderService> providerServices) {
        // 取消订阅
    }

    private ZkRegister(){
        this.init();
    }

    public static ZkRegister getInstance(){
        return zkRegister;
    }

}
