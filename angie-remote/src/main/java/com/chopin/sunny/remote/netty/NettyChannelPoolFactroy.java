package com.chopin.sunny.remote.netty;

import com.chopin.sunny.model.RpcResponse;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.serializer.PropertyConfigeHelper;
import com.chopin.sunny.serializer.enums.SerializeType;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-18 20:53
 **/
@Log4j2
public class NettyChannelPoolFactroy {

    private static final NettyChannelPoolFactroy channelPoolFactroy = new NettyChannelPoolFactroy();
    private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();
    private static final int CONNECT_SIZE = 10;
    private static final SerializeType serializeType =  PropertyConfigeHelper.getSerializerType();

    private static final String registryType =  (String)PropertyConfigeHelper.getProperty("angie.registry.type");

    private NettyChannelPoolFactroy(){}

    public static NettyChannelPoolFactroy getInstance(){
       return channelPoolFactroy;
    }

    public void init(){

        Registry registry = RegistryFactory.getRegistry(RegistryFactory.RegistType.valueOf(registryType));

        Map<String, URL> providers = registry.getLocalRegisterCaches();

        Set<InetSocketAddress> addresses = providers.values().stream().map(x->new InetSocketAddress(x.getHost(),x.getPort())).collect(Collectors.toSet());
        addresses.stream().forEach(x->addConnectToPool(x));
    }


    public ArrayBlockingQueue<Channel> getChannels(InetSocketAddress address){
        return channelPoolMap.get(address);
    }

    public void release(Channel channel, InetSocketAddress inetSocketAddress){
        //如果channel不可用，则直接增加一个新的channel到队列中
        if(channel==null){
            addConnectToPool(inetSocketAddress);
            return;
        }else if(!channel.isActive() || !channel.isOpen() || !channel.isWritable()){//回收掉，放个新的进去
            channel.deregister().syncUninterruptibly().awaitUninterruptibly();
            channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            addConnectToPool(inetSocketAddress);
        }
    }

    private void addConnectToPool(InetSocketAddress socketAddress){
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup(CONNECT_SIZE))
                    // 指定Channel
                    .channel(NioSocketChannel.class)
                    //使用指定的端口设置套接字地址
                    .remoteAddress(socketAddress)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new NettyDecoderHandler(RpcResponse.class,serializeType))
                                    .addLast("encoder", new NettyEncodeHandler(serializeType))
                                    .addLast("handler", new NettyClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ArrayBlockingQueue<Channel> inner = channelPoolMap.get(socketAddress);
                    if(inner == null){
                        inner = new ArrayBlockingQueue<>(CONNECT_SIZE);
                        channelPoolMap.put(socketAddress,inner);
                    }
                    inner.offer(future.channel());
                } else {
                    log.error(future.cause());
                }
            });
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

}
