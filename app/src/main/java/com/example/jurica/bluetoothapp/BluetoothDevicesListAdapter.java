package com.example.jurica.bluetoothapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothDevicesListAdapter extends BaseAdapter {

    View v;
    ArrayList<BluetoothDevice> pairedDevices;
    public BluetoothDevicesListAdapter(Context context, ArrayList<BluetoothDevice> devices){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.v = layoutInflater.inflate(R.layout.bt_items, null);
        this.pairedDevices = devices;
    }
    @Override
    public int getCount() {
        return pairedDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return pairedDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView deviceName = (TextView) v.findViewById(R.id.deviceName);
        TextView deviceAddress = (TextView) v.findViewById(R.id.deviceAddress);
        deviceName.setText(getItem(i).getName());
        deviceAddress.setText(getItem(i).getAddress());
        return v;
    }
}
