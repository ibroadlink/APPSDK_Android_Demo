package cn.com.broadlink.blappsdkdemo.utils.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryEx extends SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryEx(KeyStore truststore)

            throws NoSuchAlgorithmException, KeyManagementException,

            KeyStoreException, UnrecoverableKeyException {

        super(truststore);

        TrustManager tm = new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                if (null == chain || 0 == chain.length) {
                    throw new IllegalArgumentException("parameter is not used");
                } else if(null == authType || 0 == authType.length()){
                    throw new IllegalArgumentException("parameter is not used");
                }
                try {
                    //  if the certificate chain is not trusted by this TrustManager.
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

        };

        sslContext.init(null, new TrustManager[] { tm }, null);

    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {

        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);

    }

    @Override
    public Socket createSocket() throws IOException {

        return sslContext.getSocketFactory().createSocket();

    }

}