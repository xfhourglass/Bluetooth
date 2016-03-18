package com.example.mylibrary.newblue;

/**
 * package:com.example.mylibrary.newblue
 * <br/>
 * project:Bluetooth
 * <br/>
 * user:leojay
 * <br/>
 * time:下午1:55
 */
public interface ServiceBlue {
    void startService();
    void shutdownService();
    void readMessage();
    void setOnMessageListener(BlueOperation.onMessageListener listener);
}
