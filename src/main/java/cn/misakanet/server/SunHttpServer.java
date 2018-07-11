package cn.misakanet.server;

import cn.misakanet.server.handler.DefaultPageHandler;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SunHttpServer extends CommonHttpServer {
    HttpServer server;

    @Override
    public void config() throws IOException {
        server = HttpServer.create(new InetSocketAddress(getPort()), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", new DefaultPageHandler());
    }

    @Override
    public void start() {
        server.start();
        System.out.println("启动完成 可以访问 http://IP:" + getPort() + "/ 查看");
    }

    public void createContext(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }
}