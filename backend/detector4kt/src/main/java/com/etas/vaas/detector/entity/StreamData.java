package com.etas.vaas.detector.entity;

import java.util.List;

public class StreamData {
    private String sn;
    private String date;
    private List<FramePackage.RequestData.StreamItem> stream;
    
    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<FramePackage.RequestData.StreamItem> getStream() { return stream; }
    public void setStream(List<FramePackage.RequestData.StreamItem> stream) { this.stream = stream; }
}
