package main.java.com.chopin.sunny.netty;

import com.chopin.sunny.remote.model.RpcRequest;
import com.chopin.sunny.remote.netty.NettyDecoderHandler;
import com.chopin.sunny.remote.netty.NettyEncodeHandler;
import com.chopin.sunny.remote.netty.NettyServerInvokerHandler;
import com.chopin.sunny.serializer.PropertyConfigeHelper;
import com.chopin.sunny.serializer.enums.SerializeType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
    private static NettyServer nettyServer = new NettyServer();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private SerializeType serializerType = PropertyConfigeHelper.getSerializerType();

    public void start(final int port){
        synchronized (NettyServer.class){
            if(bossGroup != null || workerGroup !=null ){
                return;
            }
            bossGroup =  new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyDecoderHandler(RpcRequest.class,serializerType));
                            socketChannel.pipeline().addLast(new NettyEncodeHandler(serializerType));
                            socketChannel.pipeline().addLast(new NettyServerInvokerHandler());
                        }
                    });
            try{
                serverBootstrap.bind(port).sync().channel();
            }catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }
        }
    }

    private NettyServer(){}


    public static NettyServer singleton(){
        return nettyServer;
    }

}
