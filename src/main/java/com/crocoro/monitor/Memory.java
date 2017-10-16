package com.crocoro.monitor;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Memory {
    Sigar sigar;

    public Memory(Sigar sigar) {
        this.sigar = sigar;
    }

    public long getUsedMem() throws SigarException {
        return sigar.getMem().getActualUsed();
    }

    public long getFreeMem() throws SigarException {
        return sigar.getMem().getActualFree();
    }
}
