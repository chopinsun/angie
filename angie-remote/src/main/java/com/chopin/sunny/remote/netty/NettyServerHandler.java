package com.chopin.sunny.remote.netty;

import com.alibaba.fastjson.JSON;

import com.chopin.sunny.model.RpcRequest;
import com.chopin.sunny.model.RpcResponse;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import static com.chopin.sunny.registry.RegistryFactory.RegistType;
import com.chopin.sunny.registry.api.Registry;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Log4j2
@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Value("${angie.registry.type}")
    private String registryType;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty handler error",cause);
        //发生异常,关闭链路
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (ctx.channel().isWritable()) {
            //从服务调用对象里获取服务提供者信息
            URL provider = request.getProvider();
            //根据方法名称定位到具体某一个服务提供者
            String serviceKey = provider.getAppKey();
            //获取注册中心服务
            Registry registry = RegistryFactory.getRegistry();
            Object result = null;
            try {
                if(provider.getService() ==null || provider.getService().getClassName() ==null || provider.getService().getMethodName() ==null){
                    RpcResponse response= RpcResponse.fail(request.getUniqueKey()).error(new IllegalArgumentException()).message("Invalid request parameter").build();
                    ctx.writeAndFlush(response);
                    return;
                }
                if(registry.getLocalRegisterCaches().get(provider)==null){
                    RpcResponse response = RpcResponse.fail(request.getUniqueKey()).error(new RuntimeException("the interface you requested does not exists or is not available")).message("the interface you requested does not exists or is not available").build();
                    ctx.writeAndFlush(response);
                    return;
                }
                Object service = registry.getLocalRegisterCaches().get(serviceKey);
                //利用反射发起服务调用
                Class clazz = Class.forName(provider.getService().getClassName());
                Class<?>[] methodTypes = provider.getService().getMethodParams().stream().map(x->x.getClass()).toArray(Class[]::new);
                Method method = clazz.getMethod(provider.getService().getMethodName(),methodTypes);
                if(method!=null){
                    result =method.invoke(service,provider.getService().getMethodParams().stream().toArray());
                }
            } catch (Exception e) {
                log.warn("method invoke error",e);
                RpcResponse response = RpcResponse.fail(request.getUniqueKey()).error(e).message("method invoke error" + e.getMessage()).build();
                ctx.writeAndFlush(response);
                return;
            }
            RpcResponse response = RpcResponse.success(request.getUniqueKey())
                    .result(result)
                    .build();
            //将服务调用返回对象回写到消费端
            ctx.writeAndFlush(response);
        } else {
            log.error("------------channel closed!---------------");
        }
    }
}
