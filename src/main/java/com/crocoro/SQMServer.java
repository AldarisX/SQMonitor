package com.crocoro;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SQMServer {
    SunHttpServer httpServer = new SunHttpServer();
    SunHttpsServer httpsServer = new SunHttpsServer();

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
            httpsServer.setPasswd(uInArgs.get("passwd"));
        }
        if (uInArgs.containsKey("httpport")) {
            httpServer.setPort(Integer.parseInt(uInArgs.get("httpport")));
        }
        if (uInArgs.containsKey("httpsport")) {
            httpsServer.setPort(Integer.parseInt(uInArgs.get("httpsport")));
        }
        if (uInArgs.containsKey("defaultInterface")) {
            httpServer.setDefaultInterface(uInArgs.get("defaultInterface"));
            httpServer.setDefaultInterfaceFile(uInArgs.get("defaultInterfaceFile"));

            httpsServer.setDefaultInterface(uInArgs.get("defaultInterface"));
            httpsServer.setDefaultInterfaceFile(uInArgs.get("defaultInterfaceFile"));
        }

        try {
            httpServer.start();
            httpsServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
