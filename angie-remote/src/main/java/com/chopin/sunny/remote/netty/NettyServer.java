package com.chopin.sunny.remote.netty;

import com.chopin.sunny.enums.SerializeType;
import com.chopin.sunny.model.RpcRequest;
import com.chopin.sunny.utils.PropertyConfigeHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-15 15:19
 **/
@Component
@Log4j2
public class NettyServer {

    //处理连接的线程
    private EventLoopGroup boss = new EpollEventLoopGroup();
    //处理数据的线程
    private EventLoopGroup work = new EpollEventLoopGroup();

    private final static Integer port = 6666;

    private SerializeType serializeType = PropertyConfigeHelper.getSerializerType();

    private Channel channel;


    @PostConstruct
    public void start() throws Exception{
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work)
                // 指定Channel
                .channel(EpollServerSocketChannel.class)
                //使用指定的端口设置套接字地址
                .localAddress(new InetSocketAddress(port))
                //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                .option(ChannelOption.SO_BACKLOG,1024)
                //将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                .childOption(ChannelOption.TCP_NODELAY, true)
                //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        //注册编码器
                        p.addLast(new NettyEncodeHandler(serializeType));
                        //注册解码器
                        p.addLast(new NettyDecoderHandler(RpcRequest.class,serializeType));
                        //注册业务逻辑处理器
                        p.addLast(new NettyServerHandler());
                    }
                })
                ;
        try {
            channel = bootstrap.bind().sync().channel();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    @PreDestroy
    public void stop(){
        try {
            if(channel!=null) {
                channel.close();
            }
            boss.shutdownGracefully().sync();
            work.shutdownGracefully().sync();
        }catch (InterruptedException e){
            log.error("netty stop error",e);
        }
    }


}
