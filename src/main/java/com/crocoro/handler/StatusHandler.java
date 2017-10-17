package com.crocoro.handler;

import com.crocoro.monitor.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class StatusHandler implements HttpHandler {
    private String passwd;

    private Sigar sigar;
    private CPU cpu;
    private Memory mem;
    private Network network;
    private Disk disk;
    private AUpTime upTime;

    public StatusHandler(String passwd) {
        this.passwd = passwd;
        sigar = new Sigar();
        upTime = new AUpTime(sigar);
        cpu = new CPU(sigar);
        mem = new Memory(sigar);
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
        if ((passwd).equals(queryList.get("passwd"))) {
            //判断参数
            String command = queryList.get("command");
            if (command != null || !command.equals("")) {
                Headers headers = http.getResponseHeaders();
                headers.add("Content-Type", "application/json; charset=utf-8");
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
        JSONObject result = new JSONObject();
        result.accumulate("cpuModel", cpu.getModel());

        os.write(result.toString().getBytes());
        os.close();
    }

    private void dynMessage(OutputStream os) throws SigarException, IOException {
        try {
            JSONObject result = new JSONObject();
            result.accumulate("cpuIdle", cpu.getFree() + "");
            result.accumulate("loadAvg", upTime.getLoadAvg() + "");
            result.accumulate("upTime", upTime.getUptime() + "");
            result.accumulate("freeMem", mem.getFreeMem() + "");
            result.accumulate("usedMem", mem.getUsedMem() + "");
            result.accumulate("rx", network.getRx());
            result.accumulate("tx", network.getTx());
            //处理磁盘信息
            JSONArray apiDiskList = new JSONArray();
            for (String name : disk.getDiskList()) {
                JSONObject diskInfo = new JSONObject();
                diskInfo.accumulate("name", name + "");
                diskInfo.accumulate("read", disk.getRead(name) + "");
                diskInfo.accumulate("write", disk.getWrite(name) + "");
                diskInfo.accumulate("usage", disk.getUsage(name) + "");
                apiDiskList.add(diskInfo);
            }
            result.accumulate("diskList", apiDiskList);

            os.write(result.toString().getBytes("UTF-8"));
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
