package cn.misakanet.server;

import cn.misakanet.server.handler.DefaultPageHandler;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SunHttpsServer extends CommonHttpServer {
    HttpsServer server;

    @Override
    public void config() throws IOException {
        TrustManager[] tm = {new X509TrustManager() {
            X509Certificate[] x509Certificates;

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                this.x509Certificates = x509Certificates;
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return x509Certificates;
            }
        }};

        try {
            server = HttpsServer.create(new InetSocketAddress(getPort()), 0);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(new File("SQM.jks")), "aldaris".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "sqm_aldaris".toCharArray());

            SSLContext ssl = SSLContext.getInstance("TLSv1.2");
            ssl.init(kmf.getKeyManagers(), tm, new java.security.SecureRandom());
            server.setHttpsConfigurator(new HttpsConfigurator(ssl));

            server.createContext("/", new DefaultPageHandler());
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        server.start();
        System.out.println("启动完成 可以访问 https://IP:" + getPort() + "/ 查看");
    }

    public void createContext(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }
}
