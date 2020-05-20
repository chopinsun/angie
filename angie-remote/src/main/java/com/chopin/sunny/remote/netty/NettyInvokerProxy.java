package com.chopin.sunny.remote.netty;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.cluster.router.RouterFactory;
import com.chopin.sunny.cluster.router.RouterTypeEnum;
import com.chopin.sunny.model.RpcRequest;
import com.chopin.sunny.model.RpcResponse;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.utils.PropertyConfigeHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @title: angie
 * @description: netty执行代理
 * @author: sunxiaobo
 * @create: 2020-05-18 22:55
 **/
@Log4j2
public class NettyInvokerProxy implements InvocationHandler {

    private Object target;

    private static final String routerType =(String) PropertyConfigeHelper.getProperty("angie.router.type");
    public NettyInvokerProxy(Object target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String key = target.getClass().getName()+"."+method.getName();
        Registry registry = RegistryFactory.getRegistry();
        Set<URL> providers = registry.getLocalRegisterCaches().get(key);
        if(providers==null || providers.isEmpty()){
            throw new RuntimeException("No service available!");
        }
        Router router = RouterFactory.getRouter(RouterTypeEnum.valueOf(routerType));
        URL url = router.one(providers.stream().collect(Collectors.toList()));

        RpcRequest request = RpcRequest.builder()
                .uniqueKey(UUID.randomUUID().toString())
                .provider(url)
                .appName(url.getAppKey())
                .invokedMethodName(method.getName())
                .args(args)
                .invokeTimeout(url.getTimeout())
                .build();
        NettyClientResponseCache.initNewCache(request.getUniqueKey());
        ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactroy.getInstance().getChannels(url.toInetSocketAddress());
        Channel channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MICROSECONDS);
        try {
            //如果chanel不可用，则换一个
            while (!channel.isOpen() || !channel.isActive() || !channel.isWritable()) {
                channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
                if (channel == null) {
                    //若队列中没有可用的channel,则重新生成一个,重新循环直到拿到可用的channel
                    NettyChannelPoolFactroy.getInstance().generateChannel(url.toInetSocketAddress());
                }
            }
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.syncUninterruptibly();
        }catch (Exception e){
            log.error("",e);
        }finally {
            NettyChannelPoolFactroy.getInstance().release(channel,url.toInetSocketAddress());
        }
        RpcResponse response = NettyClientResponseCache.getResponse(request.getUniqueKey(),request.getInvokeTimeout());
        return response;
    }



}
