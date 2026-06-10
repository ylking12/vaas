package rk.netDevice.sdk.p2;

import org.springframework.stereotype.Component;
@Component
public class RSServer extends Thread {
    private boolean running;
    private IDataListener dataListener;
    
    public RSServer() {}
    public void setDataListener(IDataListener listener) { this.dataListener = listener; }
    public IDataListener getDataListener() { return dataListener; }
    public void stopService() { running = false; }
}
