package com.crocoro.monitor;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.LinkedHashMap;
import java.util.Set;

import static java.lang.Thread.sleep;

public class Disk {
    static int span = 999;
    Sigar sigar;

    LinkedHashMap<String, DiskRW> diskList = new LinkedHashMap<>();

    public Disk(Sigar sigar) {
        this.sigar = sigar;

        try {
            FileSystem[] fsList = sigar.getFileSystemList();
            for (FileSystem fs : fsList) {
                if (fs.getType() == FileSystem.TYPE_LOCAL_DISK) {
                    String name = fs.getDevName();
                    diskList.put(name, new DiskRW());
                }
            }

            start();
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getDiskList() {
        return diskList.keySet();
    }

    public double getRead(String name) {
        DiskRW diskRW = diskList.get(name);
        double read = (diskRW.getReadN() - diskRW.getReadP()) / 1048576D;
        read *= 1000 / span;
        return read;
    }

    public double getWrite(String name) {
        DiskRW diskRW = diskList.get(name);
        double write = (diskRW.getWriteN() - diskRW.getWriteP()) / 1048576D;
        write *= 1000 / span;
        return write;
    }

    public double getUsage(String name) {
        double usage = 0;
        try {
            usage = sigar.getFileSystemUsage(name).getUsePercent();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return usage;
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (String disk : diskList.keySet()) {
                        DiskRW diskRW = diskList.get(disk);
                        try {
                            FileSystemUsage usage = sigar.getFileSystemUsage(disk);
                            diskRW.setReadN(usage.getDiskReadBytes());
                            diskRW.setWriteN(usage.getDiskWriteBytes());
                            diskList.put(disk, diskRW);
                        } catch (SigarException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        sleep(span);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
