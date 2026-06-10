package rk.netDevice.sdk.p3;

public interface IDataListener {
    void receiveLoginData(LoginData loginData);
    void receiveTelecontrolAck(TelecontrolAck telecontrolAck);
    void receiveRealTimeData(RealTimeData realTimeData);
    void receiveStoreData(StoreData storeData);
    void receiveStoreDataCount(StoreDataCount storeDataCount);
    void receiveStoreDataAck(StoreDataAck storeDataAck);
    void receiveTimingAck(TimingAck timingAck);
    void receiveParamIds(ParamIds paramIds);
    void receiveParamItems(ParamItems paramItems);
    void receiveAffectedParamItems(AffectedParamItems affectedParamItems);
    void receiveDataTransAck(DataTransAck dataTransAck);
}
