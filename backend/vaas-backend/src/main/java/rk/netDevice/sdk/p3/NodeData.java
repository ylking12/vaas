package rk.netDevice.sdk.p3;
public class NodeData {
    private short nodeId;
    private DataTypes dataType;
    private float floatData;
    private int intData;
    private Object data;
    private double lng;
    private double lat;
    private double doubleData;
    private boolean switchData;
    private short coordinateType;
    
    public short getNodeId() { return nodeId; }
    public void setNodeId(short nodeId) { this.nodeId = nodeId; }
    public DataTypes getDataType() { return dataType; }
    public void setDataType(DataTypes dataType) { this.dataType = dataType; }
    public float getFloatData() { return floatData; }
    public void setFloatData(float floatData) { this.floatData = floatData; }
    public int getIntData() { return intData; }
    public void setIntData(int intData) { this.intData = intData; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getDoubleData() { return doubleData; }
    public void setDoubleData(double doubleData) { this.doubleData = doubleData; }
    public boolean getSwitchData() { return switchData; }
    public void setSwitchData(boolean switchData) { this.switchData = switchData; }
    public short getCoordinateType() { return coordinateType; }
    public void setCoordinateType(short coordinateType) { this.coordinateType = coordinateType; }
}
