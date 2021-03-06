package cn.misakanet.monitor;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CPU extends Hardware {
    private Sigar sigar;
    private CpuInfo cpuInfo;

    public CPU(Sigar sigar) {
        this.sigar = sigar;
        try {
            cpuInfo = sigar.getCpuInfoList()[0];
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    public double getFree() throws SigarException {
        return sigar.getCpuPerc().getIdle();
    }

    public double getUsage() throws SigarException {
        double user = sigar.getCpuPerc().getUser();
        double sys = sigar.getCpuPerc().getSys();
        return user + sys;
    }

    public String getModel() {
        return cpuInfo.getModel();
    }

    @Override
    public JSONObject getStatus() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("idle", getFree());
            result.accumulate("model", getModel());
        } catch (SigarException | JSONException e) {
//            e.printStackTrace();
        }
        return result;
    }
}
