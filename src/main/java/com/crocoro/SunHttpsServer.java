package com.crocoro;

import com.crocoro.handler.FileHandler;
import com.crocoro.handler.StatusHandler;
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
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class SunHttpsServer extends CommonHttpServer {
    @Override
    public void start() throws IOException {
        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);

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
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(new File("SQM.jks")), "aldaris".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "sqm_aldaris".toCharArray());

            SSLContext ssl = SSLContext.getInstance("TLSv1.2");
            ssl.init(kmf.getKeyManagers(), tm, new java.security.SecureRandom());
            server.setHttpsConfigurator(new HttpsConfigurator(ssl));

            addContext(server, "res");
            server.createContext("/", new FileHandler("res/" + defaultInterfaceFile));
            server.createContext("/api", new StatusHandler(passwd));
            server.start();
            System.out.println("启动完成 可以访问 https://IP:" + port + "/status 查看");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
