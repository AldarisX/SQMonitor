package com.crocoro;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SQMServer {
    SunHttpServer httpServer = new SunHttpServer();

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.library.path", new File("lib/").getAbsolutePath());
        new SQMServer().init(args);
    }

    public void init(String[] args) {
        //处理参数
        HashMap<String, String> uInArgs = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            uInArgs.put(args[i].replace("-", ""), args[i + 1]);
        }
        if (uInArgs.containsKey("passwd")) {
            httpServer.setPasswd(uInArgs.get("passwd"));
        }
        if (uInArgs.containsKey("port")) {
            httpServer.setPort(Integer.parseInt(uInArgs.get("port")));
        }
        if (uInArgs.containsKey("defaultInterface")) {
            httpServer.setDefaultInterface(uInArgs.get("defaultInterface"));
            httpServer.setDefaultInterfaceFile(uInArgs.get("defaultInterfaceFile"));
        }

        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
