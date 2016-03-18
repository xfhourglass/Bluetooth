package com.example.mybluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.mylibrary.newblue.BlueOperation;
import com.example.mylibrary.newblue.ServiceBlue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private android.widget.Button buttonstart;
    private android.widget.Button buttonstop;
    private android.widget.ListView listViewmsg;

    private ServiceBlue serviceBlue;

    private List<HashMap<String, String>> data = new ArrayList<>();
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.listViewmsg = (ListView) findViewById(R.id.listView_msg);
        this.buttonstop = (Button) findViewById(R.id.button_stop);
        this.buttonstart = (Button) findViewById(R.id.button_start);
        buttonstart.setOnClickListener(this);
        buttonstop.setOnClickListener(this);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        String name = adapter.getName();
        String address = adapter.getAddress();
        serviceBlue = new BlueOperation(this, name, address);
        serviceBlue.setOnMessageListener(new BlueOperation.onMessageListener() {
            @Override
            public void readMessage(Message message) {
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start:
                serviceBlue.startService();
                break;
            case R.id.button_stop:
                serviceBlue.shutdownService();
                listViewmsg.deferNotifyDataSetChanged();
                break;
            default:
                break;
        }

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            map = new HashMap<>();
            map.put("message", msg.obj.toString());
            data.add(map);
            SimpleAdapter adapter = new SimpleAdapter(TestActivity.this, data, R.layout.layout_item,
                    new String[]{"message"}, new int[]{R.id.message_text});
            listViewmsg.setAdapter(adapter);
            Toast.makeText(TestActivity.this, "shuju" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
