package com.example.mylibrary.newblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.mylibrary.BluetoothMsg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * package:com.example.mylibrary.newblue
 * <br/>
 * project:Bluetooth
 * <br/>
 * user:leojay
 * <br/>
 * time:下午1:52
 */
public class BlueOperation implements ClientBlue,ServiceBlue {

    private Context context;

    /* 一些常量，代表服务器的名称 */
    public String PROTOCOL_SCHEME_RFCOMM = "btspp";

    private BluetoothServerSocket mserverSocket = null;
    private ServerThread startServerThread = null;
    private clientThread clientConnectThread = null;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private readThread mreadThread = null;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private onMessageListener listener;

    public BlueOperation(Context context, String name, String address) {
        this.context = context;
        if (name != null){
            PROTOCOL_SCHEME_RFCOMM = name;
        }
        BluetoothMsg.BlueToothAddress = address;
    }

    public interface onMessageListener{
        void readMessage(Message String);
    }

    public void setOnMessageListener(onMessageListener listener){
        this.listener = listener;
    }

    @Override
    public void startClient() {
        String address = BluetoothMsg.BlueToothAddress;
        if (!address.equals("null")) {
            device = mBluetoothAdapter.getRemoteDevice(address);
            clientConnectThread = new clientThread();
            clientConnectThread.start();
            BluetoothMsg.isOpen = true;
        } else {
            Toast.makeText(context, "address is null !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void shutdownClient() {
        new Thread() {
            @Override
            public void run() {
                if (clientConnectThread != null) {
                    clientConnectThread.interrupt();
                    clientConnectThread = null;
                }
                if (mreadThread != null) {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            }

            ;
        }.start();
    }

    @Override
    public void sendMessage(String message) {
        if (socket == null) {
            Toast.makeText(context, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startService() {
        startServerThread = new ServerThread();
        startServerThread.start();
        BluetoothMsg.isOpen = true;
    }

    @Override
    public void shutdownService() {
        new Thread() {
            @Override
            public void run() {
                if (startServerThread != null) {
                    startServerThread.interrupt();
                    startServerThread = null;
                }
                if (mreadThread != null) {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                try {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                    if (mserverSocket != null) {
                        mserverSocket.close();/* 关闭服务器 */
                        mserverSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("server", "mserverSocket.close()", e);
                }
            }

            ;
        }.start();
    }

    @Override
    public void readMessage() {
        mreadThread = new readThread();
        mreadThread.start();
    }


    //开启客户端
    private class clientThread extends Thread {
        @Override
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                //连接
                Message msg2 = new Message();
                msg2.obj = "请稍候，正在连接服务器:" + BluetoothMsg.BlueToothAddress;
                msg2.what = 0;
//                LinkDetectedHandler.sendMessage(msg2);
                listener.readMessage(msg2);

                socket.connect();

                Message msg = new Message();
                msg.obj = "已经连接上服务端！可以发送信息。";
                msg.what = 0;
//                LinkDetectedHandler.sendMessage(msg);
                listener.readMessage(msg);
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                Log.e("connect", "", e);
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = 0;
//                LinkDetectedHandler.sendMessage(msg);
                listener.readMessage(msg);
            }
        }
    }

    //开启服务器
    private class ServerThread extends Thread {
        @Override
        public void run() {

            try {
                    /* 创建一个蓝牙服务器
                     * 参数分别：服务器名称、UUID   */
                mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Log.d("server", "wait cilent connect...");

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = 0;
//                LinkDetectedHandler.sendMessage(msg);
                listener.readMessage(msg);
                    /* 接受客户端的连接请求 */
                socket = mserverSocket.accept();
                Log.d("server", "accept success !");

                Message msg2 = new Message();
                String info = "客户端已经连接上！可以发送信息。";
                msg2.obj = info;
                msg.what = 0;
//                LinkDetectedHandler.sendMessage(msg2);
                listener.readMessage(msg2);
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取数据
    private class readThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = 1;
//                        LinkDetectedHandler.sendMessage(msg);
                        listener.readMessage(msg);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
