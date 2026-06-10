package rk.netDevice.sdk.p2;
import java.util.List;
import java.util.ArrayList;

public class RealTimeData {
    private int deviceAddr;
    private int deviceId;
    private int seqNum;
    private int relayStatus;
    private short coordinateType;
    private double lat;
    private double lng;
    private List<NodeData> nodeList = new ArrayList<>();
    
    public int getDeviceAddr() { return deviceAddr; }
    public void setDeviceAddr(int deviceAddr) { this.deviceAddr = deviceAddr; }
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    public int getSeqNum() { return seqNum; }
    public void setSeqNum(int seqNum) { this.seqNum = seqNum; }
    public int getRelayStatus() { return relayStatus; }
    public void setRelayStatus(int relayStatus) { this.relayStatus = relayStatus; }
    public short getCoordinateType() { return coordinateType; }
    public void setCoordinateType(short coordinateType) { this.coordinateType = coordinateType; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public List<NodeData> getNodeList() { return nodeList; }
    public void setNodeList(List<NodeData> nodeList) { this.nodeList = nodeList; }

    public static class NodeData {
        private int nodeId;
        private float tem;
        private float hum;
        private float floatValue;
        private long unSignedInt32Value;
        private Object data;
        private java.util.Date recordTime;
        
        public int getNodeId() { return nodeId; }
        public void setNodeId(int nodeId) { this.nodeId = nodeId; }
        public float getTem() { return tem; }
        public void setTem(float tem) { this.tem = tem; }
        public float getHum() { return hum; }
        public void setHum(float hum) { this.hum = hum; }
        public float getFloatValue() { return floatValue; }
        public void setFloatValue(float floatValue) { this.floatValue = floatValue; }
        public long getUnSignedInt32Value() { return unSignedInt32Value; }
        public void setUnSignedInt32Value(long val) { this.unSignedInt32Value = val; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public java.util.Date getRecordTime() { return recordTime; }
        public void setRecordTime(java.util.Date recordTime) { this.recordTime = recordTime; }
    }
}
