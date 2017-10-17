package com.crocoro.handler;

import com.crocoro.monitor.CPU;
import com.crocoro.monitor.Disk;
import com.crocoro.monitor.Memory;
import com.crocoro.monitor.Network;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.io.OutputStream;

public class StatusHandler implements HttpHandler {
    private String passwd;

    private Sigar sigar;
    private CPU cpu;
    private Memory mem;
    private Network network;
    private Disk disk;

    public StatusHandler(String passwd) {
        this.passwd = passwd;
        sigar = new Sigar();
        cpu = new CPU(sigar);
        mem = new Memory(sigar);
        network = new Network(sigar);
        disk = new Disk(sigar);
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        //先验证密码
        if (("passwd=" + passwd).equals(http.getRequestURI().getQuery())) {
            Headers headers = http.getResponseHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");
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
                //处理磁盘信息
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
            //密码不对就直接关掉
            http.close();
        }
    }
}
