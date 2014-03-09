package com.kl.nts;

import com.kl.nts.server.ssl.NettySSLServer;
import com.kl.nts.server.tcp.NettyTCPServer;
import com.kl.nts.server.http.NettyHTTPServer;

public class LaunchPad {

    public static void main(String[] args) throws Exception {
        int tcpPort = 8000;
        int sslPort = 8443;
        int httpPort = 8080;

        NettyTCPServer tcpServer = new NettyTCPServer(tcpPort);
        tcpServer.start();

        NettySSLServer sslServer = new NettySSLServer(sslPort);
        sslServer.start();

        NettyHTTPServer httpServer = new NettyHTTPServer(httpPort);
        httpServer.start();

        tcpServer.join();
        sslServer.join();
        httpServer.join();
    }
}
