package com.example.ma.sslSession;

import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

/**
 * Created by ma on 15/1/13.
 */


public class SSLTrustManager {

    private static TrustManager[] trustManagers;

    public static class _FakeX509TrustManager implements javax.net.ssl.X509TrustManager {
        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            Log.d("Max",chain+" Client");
            return (true);
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            Log.d("Max",chain+" Server");
            return (true);
        }

        public X509Certificate[] getAcceptedIssuers() {
            return (_AcceptedIssuers);
        }
    }

    public static void allowAllSSL() {

        javax.net.ssl.HttpsURLConnection
                .setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        Log.d("Max",hostname+" "+session+" DefaultHostnameVerifier");
                        return true;
                    }
                });

        javax.net.ssl.SSLContext context = null;

        if (trustManagers == null) {
            trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
        }

        try {
            context = javax.net.ssl.SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            Log.e("allowAllSSL", e.toString());
        } catch (KeyManagementException e) {
            Log.e("allowAllSSL", e.toString());
        }
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context
                .getSocketFactory());
    }
}
