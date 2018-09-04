package com.example.jurica.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public BluetoothAdapter bluetoothAdapter;
    public final int REQUEST_ENABLE_BT = 1;
    public ArrayList<BluetoothDevice> pairedDevices;
    public BluetoothDevicesListAdapter btListAdapter;
    public ListView btDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(this, "Uređaj ne podržava bluetooth", Toast.LENGTH_LONG).show();
        }
        else {
            enableBluetooth();
        }

        pairedDevices = getPairedDevices();
        btListAdapter = new BluetoothDevicesListAdapter(this, pairedDevices);
        btDevicesListView = (ListView) findViewById(R.id.btDevicesListView);
        btDevicesListView.setAdapter(btListAdapter);
        //Gumb za osvježiti listu uparenih uređaja
        Button refresh = (Button) findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
                pairedDevices.removeAll(pairedDevices);
                pairedDevices.addAll(getPairedDevices());
                btListAdapter.notifyDataSetChanged();
            }
        });
        btDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent activityIntent = new Intent(getApplicationContext(), BluetoothMainActivity.class);
                BluetoothDevice btDev = (BluetoothDevice) adapterView.getItemAtPosition(i);
                activityIntent.putExtra("btdevaddr", btDev.getAddress());
                startActivity(activityIntent);
            }
        });
    }



    private void enableBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "Bluetooth uključen", Toast.LENGTH_SHORT).show();
            pairedDevices.removeAll(pairedDevices);
            pairedDevices.addAll(getPairedDevices());
            btListAdapter.notifyDataSetChanged();
        }
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Otkazano", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<BluetoothDevice> getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                devices.add(device);
            }
        }
        return devices;
    }

}
