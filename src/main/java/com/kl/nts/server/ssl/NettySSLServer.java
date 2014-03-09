package com.kl.nts.server.ssl;

import com.kl.nts.server.JsonDecoder;
import com.kl.nts.server.JsonEncoder;
import com.kl.nts.server.tcp.NettyTCPServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLEngine;

public class NettySSLServer extends Thread {
    private static final Logger LOG = Logger.getLogger(NettySSLServer.class.getName());

    private final int port;

    public NettySSLServer(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1 /* number of threads */ );
        EventLoopGroup workerGroup = new NioEventLoopGroup(2 /* number of threads */);

        try {
            final SSLEngine engine = ServerSSLContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            engine.setNeedClientAuth(true);

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new SslHandler(engine),
                            new LoggingHandler(LogLevel.INFO),
                            new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                            new JsonEncoder(),
                            new JsonDecoder(),
                            new NettySSLServerHandler());
                }
            });
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

            // Bind and start to accept incoming connections.
            Channel ch = b.bind(port).sync().channel();
            LOG.info("SSL Server started on port [" + port + "]");

            ch.closeFuture().sync();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
