package com.example.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private android.widget.TextView textViewstate;
    private android.widget.ListView listView;
    private android.widget.Button buttonrush;

    private List<HashMap<String, String>> data;
    private HashMap<String, String> map = null;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.buttonrush = (Button) findViewById(R.id.button_rush);
        this.listView = (ListView) findViewById(R.id.listView);
        this.textViewstate = (TextView) findViewById(R.id.textView_state);

        rushList();
        buttonrush.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        rushList();
    }

    private void setListView() {
        if (0 != data.size()) {
            simpleAdapter = new SimpleAdapter(this, data, R.layout.layout_list_item,
                    new String[]{"name"}, new int[]{R.id.nameTextView});
            listView.setAdapter(simpleAdapter);
            if (simpleAdapter != null) {
                simpleAdapter.notifyDataSetChanged();
            }

        }
    }

    private void rushList(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(MainActivity.this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
        } else {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            data = new ArrayList<>();
            for (BluetoothDevice device : devices) {
                map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());
                data.add(map);
            }

            setListView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, OperationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", data.get(i).get("name"));
        bundle.putString("address", data.get(i).get("address"));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
