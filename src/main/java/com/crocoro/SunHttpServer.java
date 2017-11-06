package com.crocoro;

import com.crocoro.handler.FileHandler;
import com.crocoro.handler.StatusHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SunHttpServer {
    private String passwd = "";
    private int port = 8000;
    private String defaultInterface = "status";
    private String defaultInterfaceFile = "index.html";
    private File base = new File("res");

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDefaultInterface(String defaultInterface) {
        this.defaultInterface = defaultInterface;
    }

    public void setDefaultInterfaceFile(String defaultInterfaceFile) {
        this.defaultInterfaceFile = defaultInterfaceFile;
    }

    public void setBase(File base) {
        this.base = base;
    }

    public void start() throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.library.path", new File("lib/").getAbsolutePath());

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        addContext(server, "res");
        server.createContext("/" + defaultInterface, new FileHandler("res/" + defaultInterfaceFile));
        server.createContext("/api", new StatusHandler(passwd));
        server.start();
        System.out.println("启动完成 可以访问 http://IP:" + port + "/status 查看");
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