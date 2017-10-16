package com.crocoro.monitor;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CPU {
    Sigar sigar;

    public CPU(Sigar sigar) {
        this.sigar = sigar;
    }

    public double getFree() throws SigarException {
        return sigar.getCpuPerc().getIdle();
    }

    public double getUsage() throws SigarException {
        double user = sigar.getCpuPerc().getUser();
        double sys = sigar.getCpuPerc().getSys();
        return user + sys;
    }
}
