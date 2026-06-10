package rk.netDevice.sdk.p3;
import java.util.ArrayList;
import java.util.List;
public class RealTimeData {
    private int deviceAddr;
    private int seqNum;
    private List<NodeData> nodeDataList = new ArrayList<>();
    public int getDeviceAddr() { return deviceAddr; }
    public void setDeviceAddr(int deviceAddr) { this.deviceAddr = deviceAddr; }
    public int getSeqNum() { return seqNum; }
    public void setSeqNum(int seqNum) { this.seqNum = seqNum; }
    public List<NodeData> getNodeDataList() { return nodeDataList; }
}
