package cn.misakanet;

import cn.misakanet.handler.StatusHandler;
import cn.misakanet.server.ServerConfig;
import cn.misakanet.server.SunHttpServer;
import cn.misakanet.server.SunHttpsServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SQMServer {
    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.library.path", new File("lib/").getAbsolutePath());
        new SQMServer().init();
    }

    private void init() throws IOException {
        try {
            //加载配置
            ServerConfig config = ServerConfig.getInstance();
            String resource = config.getConfig("resource");
            String passwd = config.getConfig("passwd");
            String defaultPage = config.getConfig("defaultPage");
            //加载http配置
            if (config.getHttpConfig("enable")) {
                SunHttpServer httpServer = new SunHttpServer();
                httpServer.setPasswd(passwd);
                httpServer.setResource(resource);
                httpServer.setPort(config.getHttpConfig("port"));
                httpServer.setDefaultPage(defaultPage);

                httpServer.config();
                httpServer.createContext("/api", new StatusHandler(passwd));
                httpServer.start();
            }
            //加载https配置
            if (config.getHttpsConfig("enable")) {
                SunHttpsServer httpsServer = new SunHttpsServer();
                httpsServer.setPasswd(passwd);
                httpsServer.setResource(resource);
                httpsServer.setPort(config.getHttpsConfig("port"));
                httpsServer.setDefaultPage(defaultPage);

                httpsServer.config();
                httpsServer.createContext("/api", new StatusHandler(passwd));
                httpsServer.start();
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到配置文件");
        }
    }
}
