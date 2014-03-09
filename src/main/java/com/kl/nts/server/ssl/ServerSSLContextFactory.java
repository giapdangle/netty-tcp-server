package com.kl.nts.server.ssl;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

public class ServerSSLContextFactory {
    private static final String PROTOCOL = "TLS";
    private static final SSLContext SERVER_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("scert.jks"), "ss_password".toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, "ks_password".toCharArray());

            // Set up trusted manager factory to use our key store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(ks);

            TrustManager[] tms = tmf.getTrustManagers();
            // The default X509TrustManager returned by SunX509
            // We'll delegate decisions to it, and fall back to the logic in this class if the default X509TrustManager doesn't trust it.
            X509TrustManager sunJSSEX509TrustManager = null;
            // Iterate over the returned trustmanagers, look for an instance of X509TrustManager.
            // If found, use that as our "default" trust manager.
            for (TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    sunJSSEX509TrustManager = (X509TrustManager) tm;
                }
            }
            TrustManager tm = new ServerTrustManager(sunJSSEX509TrustManager);

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    private ServerSSLContextFactory() {
        //Unused
    }
}
