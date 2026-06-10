/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.entity.IntermediateResult
 *  com.etas.vaas.detector.event.bumpy.BumpyProcessor$Statistics
 */
package com.etas.vaas.detector.entity;

import com.etas.vaas.detector.event.bumpy.BumpyProcessor;

public class IntermediateResult {
    private long timeStamp;
    private String frameTime;
    private String receivedTime;
    private float mu;
    private BumpyProcessor.Statistics st;

    public IntermediateResult() {
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public String getFrameTime() {
        return this.frameTime;
    }

    public String getReceivedTime() {
        return this.receivedTime;
    }

    public float getMu() {
        return this.mu;
    }

    public BumpyProcessor.Statistics getSt() {
        return this.st;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setFrameTime(String frameTime) {
        this.frameTime = frameTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setMu(float mu) {
        this.mu = mu;
    }

    public void setSt(BumpyProcessor.Statistics st) {
        this.st = st;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntermediateResult)) {
            return false;
        }
        IntermediateResult other = (IntermediateResult)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTimeStamp() != other.getTimeStamp()) {
            return false;
        }
        if (Float.compare(this.getMu(), other.getMu()) != 0) {
            return false;
        }
        String this$frameTime = this.getFrameTime();
        String other$frameTime = other.getFrameTime();
        if (this$frameTime == null ? other$frameTime != null : !this$frameTime.equals(other$frameTime)) {
            return false;
        }
        String this$receivedTime = this.getReceivedTime();
        String other$receivedTime = other.getReceivedTime();
        if (this$receivedTime == null ? other$receivedTime != null : !this$receivedTime.equals(other$receivedTime)) {
            return false;
        }
        BumpyProcessor.Statistics this$st = this.getSt();
        BumpyProcessor.Statistics other$st = other.getSt();
        return !(this$st == null ? other$st != null : !this$st.equals(other$st));
    }

    protected boolean canEqual(Object other) {
        return other instanceof IntermediateResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $timeStamp = this.getTimeStamp();
        result = result * 59 + (int)($timeStamp >>> 32 ^ $timeStamp);
        result = result * 59 + Float.floatToIntBits(this.getMu());
        String $frameTime = this.getFrameTime();
        result = result * 59 + ($frameTime == null ? 43 : $frameTime.hashCode());
        String $receivedTime = this.getReceivedTime();
        result = result * 59 + ($receivedTime == null ? 43 : $receivedTime.hashCode());
        BumpyProcessor.Statistics $st = this.getSt();
        result = result * 59 + ($st == null ? 43 : $st.hashCode());
        return result;
    }

    public String toString() {
        return "IntermediateResult(timeStamp=" + this.getTimeStamp() + ", frameTime=" + this.getFrameTime() + ", receivedTime=" + this.getReceivedTime() + ", mu=" + this.getMu() + ", st=" + this.getSt() + ")";
    }
}

