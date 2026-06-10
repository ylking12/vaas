/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.TimeRange
 */
package com.etas.vaas.backend.dto;


public class TimeRange {
    private long left;
    private long right;

    public TimeRange() {
    }

    public long getLeft() {
        return this.left;
    }

    public long getRight() {
        return this.right;
    }

    public void setLeft(long left) {
        this.left = left;
    }

    public void setRight(long right) {
        this.right = right;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TimeRange)) {
            return false;
        }
        TimeRange other = (TimeRange)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getLeft() != other.getLeft()) {
            return false;
        }
        return this.getRight() == other.getRight();
    }

    protected boolean canEqual(Object other) {
        return other instanceof TimeRange;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $left = this.getLeft();
        result = result * 59 + (int)($left >>> 32 ^ $left);
        long $right = this.getRight();
        result = result * 59 + (int)($right >>> 32 ^ $right);
        return result;
    }

    public String toString() {
        return "TimeRange(left=" + this.getLeft() + ", right=" + this.getRight() + ")";
    }
}

