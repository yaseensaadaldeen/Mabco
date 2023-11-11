package com.example.mabco;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsTrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
    private static boolean isVerified;

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if(chain == null || chain.length == 0)throw new IllegalArgumentException("Certificate is null or empty");
        if(authType == null || authType.length() == 0) throw new IllegalArgumentException("Authtype is null or empty");
        if(!authType.equalsIgnoreCase("ECDHE_RSA") &&
                !authType.equalsIgnoreCase("ECDHE_ECDSA") &&
                !authType.equalsIgnoreCase("RSA") &&
                !authType.equalsIgnoreCase("ECDSA")) throw new CertificateException("Certificate is not trust");
        try {
            chain[0].checkValidity();
        } catch (Exception e) {
            throw new CertificateException("Certificate is not valid or trusted");
        }
    }



    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                System.out.print("host" + arg0+ "\n");
                isVerified =  arg0.contains("mabcoonline.com")
                        || arg0.contains("google") || arg0.contains("gstatic");


                return isVerified;
            }

        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager()};
        }

        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context
                .getSocketFactory());
    }

}