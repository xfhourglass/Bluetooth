package com.example.mylibrary.newblue;

/**
 * package:com.example.mylibrary.newblue
 * <br/>
 * project:Bluetooth
 * <br/>
 * user:leojay
 * <br/>
 * time:下午1:54
 */
public interface ClientBlue {
    void startClient();
    void shutdownClient();
    void sendMessage(String message);
    void setOnMessageListener(BlueOperation.onMessageListener listener);
}
