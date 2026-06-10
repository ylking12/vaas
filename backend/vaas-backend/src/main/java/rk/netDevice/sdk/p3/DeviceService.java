package rk.netDevice.sdk.p3;

import org.springframework.stereotype.Component;
@Component
public class DeviceService extends Thread {
    private boolean running;
    private IDataListener dataListener;
    
    public DeviceService() {}
    public void setDataListener(IDataListener listener) { this.dataListener = listener; }
    public IDataListener getDataListener() { return dataListener; }
    public void stopService() { running = false; }
}
