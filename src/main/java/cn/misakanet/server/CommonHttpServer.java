package cn.misakanet.server;

import java.io.File;
import java.io.IOException;

public abstract class CommonHttpServer {
    private String passwd = "";
    private int port;
    private String defaultPage = "status.html";
    private File resource;

    public abstract void config() throws IOException;

    public abstract void start();

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    public File getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = new File(resource);
    }

    public void setResource(File resource) {
        this.resource = resource;
    }
}
