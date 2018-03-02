package com.crocoro.handler;

import com.crocoro.monitor.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

public class StatusHandler implements HttpHandler {
    private String passwd;

    private Sigar sigar;
    private CPU cpu;
    private Memory mem;
    private Swap swap;
    private Network network;
    private Disk disk;
    private AUpTime upTime;

    public StatusHandler(String passwd) {
        this.passwd = passwd;
        sigar = new Sigar();
        upTime = new AUpTime(sigar);
        cpu = new CPU(sigar);
        mem = new Memory(sigar);
        swap = new Swap(sigar);
        network = new Network(sigar);
        disk = new Disk(sigar);
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        //转化query
        HashMap<String, String> queryList = new HashMap<>();
        String[] queryListRow = http.getRequestURI().getQuery().split("&");
        for (int i = 0; i < queryListRow.length; i++) {
            String query = queryListRow[i];
            String[] queryKV = query.split("=");
            queryList.put(queryKV[0], queryKV[1]);
        }
        //先验证密码
        if (passwd.equals(queryList.get("passwd"))) {
            //判断参数
            String command = queryList.get("command");
            if (!"".equals(command)) {
                Headers headers = http.getResponseHeaders();
                headers.add("Content-Type", "application/json; charset=utf-8");
                headers.add("Content-Encoding", "gzip");
                headers.add("Server", "Sun HttpServer");
                http.sendResponseHeaders(200, 0);
                OutputStream os = http.getResponseBody();
                try {
                    switch (command) {
                        case "info":
                            infoMessage(os);
                            break;
                        case "dyn":
                            dynMessage(os);
                            break;
                        case "shutdown":
                            shutdown();
                            break;
                        default:
                            os.close();
                            break;
                    }
                } catch (SigarException e) {
                    e.printStackTrace();
                }
            } else {
                //不传参数就直接关掉
                http.close();
            }
        } else {
            //密码不对就直接关掉
            http.close();
        }
    }

    private void infoMessage(OutputStream os) throws IOException {
        try {
            JSONObject result = new JSONObject();
            result.accumulate("cpuModel", cpu.getModel());

            GZIPOutputStream gzip = new GZIPOutputStream(os);
            gzip.write(result.toString().getBytes("UTF-8"));
            gzip.close();
            os.close();
        } catch (ExportException e) {
            e.printStackTrace();
        }
    }

    private void dynMessage(OutputStream os) {
        try {
            JSONObject result = new JSONObject();

            result.accumulate("CPU", cpu.getStatus());
            result.accumulate("Mem", mem.getStatus());
            result.accumulate("Swap", swap.getStatus());
            result.accumulate("Net", network.getStatus());
            result.accumulate("Disk", disk.getStatus());
            result.accumulate("Uptime", upTime.getStatus());

            GZIPOutputStream gzip = new GZIPOutputStream(os);
            gzip.write(result.toString().getBytes("UTF-8"));
            gzip.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        System.exit(0);
    }
}
