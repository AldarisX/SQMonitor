package com.crocoro.monitor;

public class DiskRW {
    private long readP = 0;
    private long writeP = 0;
    private long readN = 0;
    private long writeN = 0;

    public long getReadP() {
        return readP;
    }

    public void setReadP(long readP) {
        this.readP = readP;
    }

    public long getWriteP() {
        return writeP;
    }

    public void setWriteP(long writeP) {
        this.writeP = writeP;
    }

    public long getReadN() {
        return readN;
    }

    public void setReadN(long readN) {
        this.readP = this.readN;
        this.readN = readN;
    }

    public long getWriteN() {
        return writeN;
    }

    public void setWriteN(long writeN) {
        this.writeP = this.writeN;
        this.writeN = writeN;
    }
}
