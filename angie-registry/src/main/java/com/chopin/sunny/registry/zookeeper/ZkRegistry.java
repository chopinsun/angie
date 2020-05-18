package com.chopin.sunny.registry.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chopin.sunny.model.Concumer;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.AbstractRegistry;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j2
public class ZkRegistry extends AbstractRegistry {

    private String ZK_SERVERS;
    private String ZK_SESSION_TIME_OUT;
    private String ZK_CONNECTION_TIME_OUT;
    private String ZK_ROOT_PATH="/ZK_ROOT/";
    private String PROVIDER_ROOT;
    private String CONCUMER_ROOT;
    private final RetryPolicy retryPolicy= new ExponentialBackoffRetry(1000, 3);

    private CuratorFramework client;
    private ExecutorService exec;
    private ConcurrentHashMap<URL,Set<Map<URL,PathChildrenCache>>> linsteners= new ConcurrentHashMap<>();



    public ZkRegistry(){
        super();
        exec = new ThreadPoolExecutor(1,4,1000, TimeUnit.MICROSECONDS,new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void init() {
        client = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVERS)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(PROVIDER_ROOT)
                .build();
        client.start();
    }

    @Override
    public void destroy() {
        client.close();
    }

    @Override
    protected void doRegister(URL url) {
        client.start();
        if(url.getService()==null){
            log.error("无服务可注册");
            return;
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZK_ROOT_PATH + url.toNodePath() + url.getService().toServicePath(),JSONObject.toJSONBytes(url));
        }catch (Exception e){
            log.error("productor 注册失败",e);
        }

    }

    @Override
    protected void doUnRegister(URL url) {
        try {
            client.delete().forPath(ZK_ROOT_PATH + url.toNodePath());
        } catch (Exception e) {
            log.error("【生产者】注册失败 "+ JSONObject.toJSONString(url),e);
        }
    }

    @Override
    protected void doSubscribe(URL url, Set<URL> producers) {
        Set<Map<URL,PathChildrenCache>> caches = producers.stream()
                .map(x-> Arrays.asList(x).stream().collect(Collectors.toMap(k->k,v->new PathChildrenCache(client,v.toNodePath(),true,false,exec))))
                .collect(Collectors.toSet());

        caches.stream().forEach(cache->
                cache.values().stream().forEach(cac->{
                    try {
                        cac.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                        cac.getListenable().addListener((c, e) -> {
                            notify(url,URL.valueOf(e.getData().getPath(),e.getData().getData()));
                            log.info("触发订阅: {} / {}",e.getType(),cac.getCurrentData());
                        });
                    } catch (Exception e) {
                        log.error("订阅失败",e);
                    }

                }));
        linsteners.put(url, caches);
    }

    @Override
    protected void doUnSubscribe(URL url, Set<URL> producer) {
        Set<Map<URL,PathChildrenCache>> caches = linsteners.get(url);
        caches.stream().forEach(x->
            x.entrySet().stream().forEach(entry->{
                if(producer.contains(entry.getKey())){
                    x.remove(entry.getKey());
                }
            })
        );
    }


}
