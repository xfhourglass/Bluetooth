package com.example.mybluetooth;

import android.content.Intent;
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
import com.example.mylibrary.newblue.ClientBlue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OperationActivity extends AppCompatActivity implements View.OnClickListener {

    private android.widget.Button buttonup;
    private android.widget.Button buttondown;
    private android.widget.Button buttonbefore;
    private android.widget.Button buttonleft;
    private android.widget.Button buttonright;
    private android.widget.Button buttonlater;
    private android.widget.Button buttonreturn;
    private Button buttonconnect;
    private android.widget.ListView listViewstatemessage;
    private List<HashMap<String, String>> data = new ArrayList<>();
    private HashMap<String, String> map;
    private ClientBlue clientBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        this.listViewstatemessage = (ListView) findViewById(R.id.listView_state_message);
        this.buttonconnect = (Button) findViewById(R.id.button_connect);
        this.buttonreturn = (Button) findViewById(R.id.button_return);
        this.buttonlater = (Button) findViewById(R.id.button_later);
        this.buttonright = (Button) findViewById(R.id.button_right);
        this.buttonleft = (Button) findViewById(R.id.button_left);
        this.buttonbefore = (Button) findViewById(R.id.button_before);
        this.buttondown = (Button) findViewById(R.id.button_down);
        this.buttonup = (Button) findViewById(R.id.button_up);
        buttonconnect.setOnClickListener(this);
        buttonreturn.setOnClickListener(this);
        buttonbefore.setOnClickListener(this);
        buttonlater.setOnClickListener(this);
        buttonleft.setOnClickListener(this);
        buttonright.setOnClickListener(this);
        buttonup.setOnClickListener(this);
        buttondown.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String address = bundle.getString("address");
        clientBlue = new BlueOperation(this, name, address);
        clientBlue.setOnMessageListener(new BlueOperation.onMessageListener() {
            @Override
            public void readMessage(Message message) {
                handler.sendMessage(message);
            }
        });

    }

    @Override
    public void onClick(View view) {
        String msg = null;
        switch (view.getId()) {
            case R.id.button_connect:
                setConnect();
                break;
            case R.id.button_before:
                msg = "before";
                break;
            case R.id.button_later:
                msg = "later";
                break;
            case R.id.button_left:
                msg = "left";
                break;
            case R.id.button_right:
                msg = "right";
                break;
            case R.id.button_up:
                msg = "up";
                break;
            case R.id.button_down:
                msg = "down";
                break;
            case R.id.button_return:
                clientBlue.shutdownClient();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        if (msg != null && !msg.equals("")) {
            clientBlue.sendMessage(msg);
        }
    }

    private void setConnect() {
        clientBlue.startClient();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            map = new HashMap<>();
            map.put("obj", msg.obj.toString());
            data.add(map);
            SimpleAdapter adapter = new SimpleAdapter(OperationActivity.this, data, R.layout.layout_list_item,
                    new String[]{"obj"}, new int[]{R.id.nameTextView});
            listViewstatemessage.setAdapter(adapter);
            Toast.makeText(OperationActivity.this, "数据：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

}
