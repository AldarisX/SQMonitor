package cn.misakanet.server;

import cn.misakanet.tool.FileTool;
import net.sf.json.JSONObject;

import java.io.FileNotFoundException;

public class ServerConfig {
    private static ServerConfig serverConfig;
    private JSONObject config;

    private ServerConfig(String config) {
        this.config = JSONObject.fromObject(config);
    }

    public static ServerConfig getInstance() {
        if (serverConfig == null) {
            try {
                serverConfig = new ServerConfig(FileTool.readFile("config.json"));
            } catch (FileNotFoundException e) {
                System.err.println("找不到配置文件config.json");
                System.exit(0);
            }
        }
        return serverConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key) {
        return (T) config.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHttpConfig(String key) {
        return (T) config.getJSONObject("http").get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHttpsConfig(String key) {
        return (T) config.getJSONObject("https").get(key);
    }
}
