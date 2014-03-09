package com.kl.ntc;

import com.kl.ntc.http.NettyHTTPClient;
import com.kl.ntc.ssl.NettySSLClient;
import com.kl.ntc.tcp.NettyTCPClient;

public class ClientLaunchPad {

    public static void main(String[] args) throws Exception {
        final String host = "localhost";
        final String path = "/";
        final int tcpPort = 8000;
        final int sslPort = 8443;
        final int httpPort = 8080;

        NettyTCPClient tcpClient = new NettyTCPClient(host, tcpPort);
        NettySSLClient sslClient = new NettySSLClient(host, sslPort);
        NettyHTTPClient httpClient = new NettyHTTPClient(host, httpPort, path);

//        tcpClient.start();
//        tcpClient.join();

        sslClient.start();
        sslClient.join();

//        httpClient.start();
//        httpClient.join();
    }
}
