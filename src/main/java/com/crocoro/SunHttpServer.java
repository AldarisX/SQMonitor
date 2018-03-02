package com.crocoro;

import com.crocoro.handler.DefaultPageHandler;
import com.crocoro.handler.StatusHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SunHttpServer extends CommonHttpServer {
    @Override
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        addContext(server, "res");
        server.createContext("/", new DefaultPageHandler("res/" + defaultInterfaceFile));
        server.createContext("/api", new StatusHandler(passwd));
        server.start();
        System.out.println("启动完成 可以访问 http://IP:" + port + "/status 查看");
    }
}