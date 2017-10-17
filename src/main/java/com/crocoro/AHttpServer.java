package com.crocoro;

import com.crocoro.handler.FileHandler;
import com.crocoro.handler.StatusHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class AHttpServer {
    private static String passwd = "";
    private static int port = 8000;
    private static String defaultInterface = "status";
    private static String defaultInterfaceFile = "index.html";
    public File base = new File("res");

    public static void main(String[] args) throws IOException {
        //处理参数
        HashMap<String, String> uInArgs = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            uInArgs.put(args[i].replace("-", ""), args[i + 1]);
        }
        if (uInArgs.containsKey("passwd")) {
            passwd = uInArgs.get("passwd");
        }
        if (uInArgs.containsKey("port")) {
            port = Integer.parseInt(uInArgs.get("port"));
        }
        if (uInArgs.containsKey("defaultInterface")) {
            defaultInterface = uInArgs.get("defaultInterface");
            defaultInterfaceFile = uInArgs.get("defaultInterfaceFile");
        }
        //启动服务器
        new AHttpServer().init();
    }

    private void init() throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.library.path", new File("lib/").getAbsolutePath());

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        addContext(server, "res");
        server.createContext("/" + defaultInterface, new FileHandler("res/" + defaultInterfaceFile));
        server.createContext("/api", new StatusHandler(passwd));
        server.start();
        System.out.println("启动完成 可以访问http://IP:" + port + "/status");
    }

    //添加文件上下文
    private void addContext(HttpServer server, String dirName) throws IOException {
        File[] webFiles = new File(dirName).listFiles();
        for (File file : webFiles) {
            if (file.isFile()) {
                String fileContext = file.getAbsolutePath().toString().replace(base.getAbsolutePath().toString(), "");
                server.createContext(fileContext.replaceAll("\\\\", "/"), new FileHandler(file.getAbsolutePath()));
            } else {
                addContext(server, file.getAbsolutePath());
            }
        }
    }
}