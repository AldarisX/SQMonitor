package com.crocoro.monitor;

import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Network {
    static int span = 999;
    String[] cardList;
    long rxBytesP = 0;
    long txBytesP = 0;
    long rxBytesN;
    long txBytesN;
    Sigar sigar;

    public Network(Sigar sigar) {
        this.sigar = sigar;
        try {
            cardList = sigar.getNetInterfaceList();
            start();
        } catch (SigarException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getRx() {
        double rx = (rxBytesN - rxBytesP) / 1024D;
        rx *= 1000 / span;
//        return decimalFormat.format(rx);
        return rx;
    }

    public double getTx() {
        double tx = (txBytesN - txBytesP) / 1024D;
        tx *= 1000 / span;
//        return decimalFormat.format(tx);
        return tx;
    }

    public void start() throws SigarException, InterruptedException {
        ArrayList<String> lisCardList = new ArrayList<>();
        for (int i = 0; i < cardList.length; i++) {
            NetInterfaceConfig config = sigar.getNetInterfaceConfig(cardList[i]);
            String address = config.getAddress();
            if (!address.equals("0.0.0.0") && !address.equals("127.0.0.1")) {
                NetInterfaceStat stat = sigar.getNetInterfaceStat(cardList[i]);
                if (stat.getSpeed() != 0) {
                    lisCardList.add(cardList[i]);
                    rxBytesP = stat.getRxBytes();
                    txBytesP = stat.getTxBytes();
                }
            }
        }

        Thread mo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long rx = 0;
                    long tx = 0;
                    for (String card : lisCardList) {
                        try {
                            NetInterfaceStat stat = sigar.getNetInterfaceStat(card);
                            rx += stat.getRxBytes();
                            tx += stat.getTxBytes();
                        } catch (SigarException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        sleep(span);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rxBytesP = rxBytesN;
                    txBytesP = txBytesN;
                    rxBytesN = rx;
                    txBytesN = tx;
                }
            }
        });
        mo.start();
    }
}
