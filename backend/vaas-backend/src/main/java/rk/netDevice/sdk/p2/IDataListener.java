package rk.netDevice.sdk.p2;

public interface IDataListener {
    void receiveLoginData(LoginData loginData);
    void receiveTelecontrolAck(TelecontrolAck telecontrolAck);
    void receiveRealTimeData(RealTimeData realTimeData);
    void receiveStoreData(StoreData storeData);
    void receiveTimmingAck(TimmingAck timmingAck);
    void receiveHeartbeatData(HeartbeatData heartbeatData);
    void receiveParamData(ParamData paramData);
    void receiveParamIdsData(ParamIdsData paramIdsData);
    void receiveTransDataAck(TransDataAck transDataAck);
    void receiveWriteParamAck(WriteParamAck writeParamAck);
}
