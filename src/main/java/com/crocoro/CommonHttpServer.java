package com.crocoro;

import com.crocoro.handler.FileHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;

public abstract class CommonHttpServer {
    protected String passwd = "";
    protected int port = 8000;
    protected String defaultInterface = "status";
    protected String defaultInterfaceFile = "index.html";
    protected File base = new File("res");

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

    //添加文件上下文
    protected void addContext(HttpServer server, String dirName) {
        File[] webFiles = new File(dirName).listFiles();
        for (File file : webFiles) {
            if (file.isFile()) {
                String fileContext = file.getAbsolutePath().replace(base.getAbsolutePath(), "");
                server.createContext(fileContext.replaceAll("\\\\", "/"), new FileHandler(file.getAbsolutePath()));
            } else {
                addContext(server, file.getAbsolutePath());
            }
        }
    }

    public abstract void start() throws IOException;
}
