package com.crocoro.monitor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Memory extends Hardware {
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

    public long getAllUsedMem() throws SigarException {
        return sigar.getMem().getUsed();
    }

    public long getAllFreeMem() throws SigarException {
        return sigar.getMem().getFree();
    }

    public double getUage() throws SigarException {
        return sigar.getMem().getUsedPercent();
    }

    @Override
    public JSONObject getStatus() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("free", getFreeMem());
            result.accumulate("used", getUsedMem());
            result.accumulate("allFree", getAllFreeMem());
            result.accumulate("allUsed", getAllUsedMem());
            result.accumulate("usage", getUage());
        } catch (SigarException | JSONException e) {
//            e.printStackTrace();
        }
        return result;
    }
}
