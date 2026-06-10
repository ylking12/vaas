package rk.netDevice.sdk.p2;
import rk.netDevice.sdk.p3.NodeData;
import rk.netDevice.sdk.p3.DataTypes;
import java.util.List;
public class StoreDataCount {
    private int deviceAddr;
    private int seqNum;
    private List<NodeData> nodeDataList;
    public int getDeviceAddr() { return deviceAddr; }
    public void setDeviceAddr(int deviceAddr) { this.deviceAddr = deviceAddr; }
    public int getSeqNum() { return seqNum; }
    public void setSeqNum(int seqNum) { this.seqNum = seqNum; }
    public List<NodeData> getNodeDataList() { return nodeDataList; }
    public void setNodeDataList(List<NodeData> nodeDataList) { this.nodeDataList = nodeDataList; }
}
