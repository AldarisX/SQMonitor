package com.crocoro.monitor;

import net.sf.json.JSONObject;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.Uptime;
import org.hyperic.sigar.util.PrintfFormat;

public class AUpTime extends Hardware {
    private Sigar sigar;
    private Uptime uptime;
    private double[] loadAvg = new double[3];
    private PrintfFormat formatter = new PrintfFormat("%.2f, %.2f, %.2f");

    public AUpTime(Sigar sigar) {
        this.sigar = sigar;
        try {
            uptime = sigar.getUptime();
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    public String getLoadAvg() {
        try {
            double[] avg = sigar.getLoadAverage();
            loadAvg[0] = avg[0];
            loadAvg[1] = avg[1];
            loadAvg[2] = avg[2];
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < avg.length; i++) {
                sb.append(avg[i]).append(" ");
            }
            return sb.toString();
        } catch (SigarNotImplementedException e) {
            return "(Windows下无法获得负载)";
        } catch (SigarException e) {
            e.printStackTrace();
            return "异常发生!!!";
        }
    }

    public String getUptime() {
        try {
            double uptime = sigar.getUptime().getUptime();
            String retval = "";

            int days = (int) uptime / (60 * 60 * 24);
            int minutes, hours;

            if (days != 0) {
                retval += days + " " + ((days > 1) ? "days" : "天") + ", ";
            }

            minutes = (int) uptime / 60;
            hours = minutes / 60;
            hours %= 24;
            minutes %= 60;

            if (hours != 0) {
                retval += hours + "小时:" + minutes + "分钟";
            } else {
                retval += minutes + " 分钟";
            }
            return retval;
        } catch (SigarException e) {
            e.printStackTrace();
            return "发生异常";
        }
    }

    @Override
    public JSONObject getStatus() {
        JSONObject result = new JSONObject();
        result.accumulate("upTime", getUptime());
        result.accumulate("loadAvg", getLoadAvg());
        return result;
    }
}
