package com.kl.nts.server.ssl;

import com.kl.nts.json.AuthMessage;
import com.kl.nts.task.ClientTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;

import java.net.InetAddress;

public class NettySSLServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = Logger.getLogger(NettySSLServerHandler.class.getName());

    private ClientTask task;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOG.info(msg.toString());
        if (msg instanceof AuthMessage) {
            if (ctx.channel().isOpen()) {
                task.acceptRequest((AuthMessage) msg);
            }
        } else if (msg instanceof String) {
            LOG.info("Message is string: [" + msg + "].");
            ctx.writeAndFlush(((String) msg).toUpperCase());
        } else {
            LOG.warn("Message is not valid.");
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        task = new ClientTask(ctx.channel());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        task.unregisteredChannel();
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        ctx.writeAndFlush("{\"info\":\"Session protected by " + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite.\"}\n");
                    }
                }
        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
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
