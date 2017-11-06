package com.crocoro.monitor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Swap extends Hardware {
    Sigar sigar;

    public Swap(Sigar sigar) {
        this.sigar = sigar;
    }

    public long getUsedSwap() throws SigarException {
        return sigar.getSwap().getUsed();
    }

    public long getFreeSwap() throws SigarException {
        return sigar.getSwap().getFree();
    }

    @Override
    public JSONObject getStatus() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("free", getFreeSwap());
            result.accumulate("used", getUsedSwap());
        } catch (SigarException | JSONException e) {
//            e.printStackTrace();
        }
        return result;
    }
}
