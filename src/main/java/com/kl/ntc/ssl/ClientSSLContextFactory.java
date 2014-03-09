package com.kl.ntc.ssl;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

public class ClientSSLContextFactory {
    private static final String PROTOCOL = "TLS";
    private static final SSLContext CLIENT_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX";
        }

        SSLContext clientContext;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("ccert.jks"), "sc_password".toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, "kc_password".toCharArray());

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
            TrustManager tm = new ClientTrustManager(sunJSSEX509TrustManager);

            // Initialize the SSLContext to work with our key managers.
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }
        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    private ClientSSLContextFactory() {
        // Unused
    }
}
