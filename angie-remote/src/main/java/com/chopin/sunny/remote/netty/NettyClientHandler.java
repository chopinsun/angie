package com.chopin.sunny.remote.netty;

import com.chopin.sunny.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-18 20:41
 **/
@Log4j2
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause);
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        NettyClientResponseCache.putResponse(rpcResponse);
    }
}
