package com.kl.ntc.ssl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

public class NettySSLClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = Logger.getLogger(NettySSLClientHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        LOG.info(o.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("Unexpected exception from channel. " + cause.getMessage());
        ctx.close();
    }
}
