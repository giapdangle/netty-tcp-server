package com.kl.nts.server.ssl;

import org.apache.log4j.Logger;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ServerTrustManager implements X509TrustManager {
    private static final Logger LOG = Logger.getLogger(ServerTrustManager.class.getName());

    private X509TrustManager sunJSSEX509TrustManager;

    public ServerTrustManager(X509TrustManager sunJSSEX509TrustManager) {
        this.sunJSSEX509TrustManager = sunJSSEX509TrustManager;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return sunJSSEX509TrustManager.getAcceptedIssuers();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOG.info("ON SERVER - CLIENT CERTIFICATE: " + chain[0].getSubjectDN());
        try {
            sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException excep) {
            LOG.error(excep);
            throw excep;
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOG.info("ON SERVER - SERVER CERTIFICATE: " + chain[0].getSubjectDN());
        try {
            sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException excep) {
            LOG.error(excep);
            throw excep;
        }
    }
}
