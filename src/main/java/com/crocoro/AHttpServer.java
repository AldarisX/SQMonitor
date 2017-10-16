package com.crocoro;

import com.crocoro.monitor.CPU;
import com.crocoro.monitor.Disk;
import com.crocoro.monitor.Memory;
import com.crocoro.monitor.Network;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.*;
import java.net.InetSocketAddress;

public class AHttpServer {
    static String passwd;
    static int port = 8080;
    Sigar sigar;
    CPU cpu;
    Memory mem;
    Network network;
    Disk disk;

    public static void main(String[] args) throws IOException {
        if (args.length == 4) {
            if (!args[0].equals("-passwd")) {
                System.out.println("请添加 -passwd 比如 -passwd 1234");
            } else {
                passwd = args[1];

                if (args[2].equals("-port")) {
                    port = Integer.parseInt(args[3]);
                    new AHttpServer().init();
                } else {
                    System.out.println("请添加 -port 比如 -port 8080");
                }
            }
        } else {
            System.out.println("参数不正确");
            System.out.println("需要 -passwd 你的密码 -port 监听的端口");
        }
    }

    public void init() throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        System.out.println(new File("lib/").getAbsolutePath());
        System.setProperty("java.library.path", new File("lib/").getAbsolutePath());

        sigar = new Sigar();
        cpu = new CPU(sigar);
        mem = new Memory(sigar);
        network = new Network(sigar);
        disk = new Disk(sigar);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/status", new FileHandler("res/index.html"));
        server.createContext("/jquery.min.js", new FileHandler("res/jquery.min.js"));
        server.createContext("/api", new ApiHandler());
        server.start();
        System.out.println("启动完成 可以访问http://IP:" + port + "/status");
    }

    class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange http) throws IOException {
            if (("passwd=" + passwd).equals(http.getRequestURI().getQuery())) {
                Headers headers = http.getResponseHeaders();
                headers.add("Content-Type", "application/json;charset=utf-8");
                headers.add("Server", "Sun HttpServer");
                http.sendResponseHeaders(200, 0);
                OutputStream os = http.getResponseBody();
                JSONObject apiResult = new JSONObject();
                try {
                    apiResult.accumulate("cpuIdle", cpu.getFree() + "");
                    apiResult.accumulate("freeMem", mem.getFreeMem() + "");
                    apiResult.accumulate("usedMem", mem.getUsedMem() + "");
                    apiResult.accumulate("rx", network.getRx());
                    apiResult.accumulate("tx", network.getTx());

                    JSONArray apiDiskList = new JSONArray();
                    for (String name : disk.getDiskList()) {
                        JSONObject diskInfo = new JSONObject();
                        diskInfo.accumulate("name", name);
                        diskInfo.accumulate("read", disk.getRead(name) + "");
                        diskInfo.accumulate("write", disk.getWrite(name) + "");
                        diskInfo.accumulate("usage", disk.getUsage(name) + "");
                        apiDiskList.add(diskInfo);
                    }
                    apiResult.accumulate("diskList", apiDiskList);

                    os.write(apiResult.toString().getBytes());
                    os.close();
                } catch (SigarException e) {
                    e.printStackTrace();
                }
            } else {
                http.close();
            }
        }
    }

    class FileHandler implements HttpHandler {
        String fileName;

        public FileHandler(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void handle(HttpExchange http) throws IOException {
            Headers headers = http.getResponseHeaders();
            headers.add("Content-Type", "text/html; charset=utf-8");
            headers.add("Server", "Sun HttpServer");
            http.sendResponseHeaders(200, 0);

            StringBuffer sbIndex = new StringBuffer();

            File file = new File(fileName);
//            BufferedReader br = new BufferedReader(new FileReader(file));
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            while ((str = br.readLine()) != null) {
                sbIndex.append(str);
                sbIndex.append("\n");
            }
            br.close();

            OutputStream os = http.getResponseBody();
            os.write(sbIndex.toString().getBytes("UTF-8"));
            os.close();
        }
    }
}