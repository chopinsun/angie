package com.chopin.sunny.remote.netty;

import com.alibaba.fastjson.JSON;
import com.chopin.sunny.model.ProviderService;
import com.chopin.sunny.model.RpcRequest;
import com.chopin.sunny.registry.api.RegisterCenter;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServerInvokerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    //服务端限流
    private static final Map<String, Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //发生异常,关闭链路
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (ctx.channel().isWritable()) {
            //从服务调用对象里获取服务提供者信息
            ProviderService metaDataModel = request.getProvider();
            long consumeTimeOut = request.getInvokeTimeout();
            final String methodName = request.getInvokedMethodName();

            //根据方法名称定位到具体某一个服务提供者
            String serviceKey = metaDataModel.getServiceItf().getName();
            //获取限流工具类
            int workerThread = metaDataModel.getWorkerThreads();
            Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
            if (semaphore == null) {
                synchronized (serviceKeySemaphoreMap) {
                    semaphore = serviceKeySemaphoreMap.get(serviceKey);
                    if (semaphore == null) {
                        semaphore = new Semaphore(workerThread);
                        serviceKeySemaphoreMap.put(serviceKey, semaphore);
                    }
                }
            }

            //获取注册中心服务
            RegisterCenter registerCenter4Provider = RegisterCenter.singleton();
            List<Provider> localProviderCaches = registerCenter4Provider.getProviderMap().get(serviceKey);

            Object result = null;
            boolean acquire = false;

            try {
                Provider localProviderCache =localProviderCaches.stream().filter((input)->StringUtils.equals(input.getServiceMethod().getName(), methodName)).findAny().get();

                Object serviceObject = localProviderCache.getServiceObject();

                //利用反射发起服务调用
                Method method = localProviderCache.getServiceMethod();
                //利用semaphore实现限流
                acquire = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
                if (acquire) {
                    result = method.invoke(serviceObject, request.getArgs());
                }
            } catch (Exception e) {
                System.out.println(JSON.toJSONString(localProviderCaches) + "  " + methodName+" "+e.getMessage());
                result = e;
            } finally {
                if (acquire) {
                    semaphore.release();
                }
            }

            //根据服务调用结果组装调用返回对象
            RpcResponse response = new RpcResponse();
            response.setInvokeTimeout(consumeTimeOut);
            response.setUniqueKey(request.getUniqueKey());
            response.setResult(result);

            //将服务调用返回对象回写到消费端
            ctx.writeAndFlush(response);


        } else {
            log.error("------------channel closed!---------------");
        }
    }
}
