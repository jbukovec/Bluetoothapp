package com.example.jurica.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothMainActivity extends AppCompatActivity {
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket btSocket;
    InputStream inputStream;
    OutputStream outputStream;
    ConnectThread connectThread;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_main);
        Intent intent = getIntent();
        String address = intent.getStringExtra("btdevaddr");
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        connectThread = new ConnectThread(this, bluetoothDevice);
        connectThread.start();
        final EditText text = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread sendThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String s = text.getText().toString();
                        if(s != null || s != "") {
                            try {
                                outputStream.write(s.getBytes());
                            } catch (IOException e) {
                                Log.e("OUTPU STREAM WRITE", "Error OutputStream write", e);
                            }
                        }
                    }
                });
                sendThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        connectThread.cancel();
        super.onDestroy();
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private UUID MY_UUID = UUID.fromString( "00001101-0000-1000-8000-00805F9B34FB");
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        Activity activity;

        public ConnectThread(final Activity activity, BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            this.activity = activity;
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.w("SOCKET CREATED", "Socket created successfully");
            } catch (IOException e) {
                Log.e("SOCKET CREATE FAILED", "Socket's create() method failed", e);
            }
            btSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                btSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.e("SOCKET CONNECT ERROR", "Could not connect to client socket", connectException);
                try {
                    btSocket.close();
                } catch (IOException closeException) {
                    Log.e("SOCKET CLOSE", "Could not close the client socket", closeException);
                }
                return;
            }
            try {
                tmpIn = btSocket.getInputStream();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!sendButton.isEnabled()) {
                            sendButton.setEnabled(true);
                        }
                    }
                });

                Log.w("INPUT STREAM", "Input stream success");
            } catch (IOException e) {
                Log.e("INPUT STREAM", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = btSocket.getOutputStream();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!sendButton.isEnabled()) {
                            sendButton.setEnabled(true);
                        }
                    }
                });

                Log.w("OUTPUT STREAM", "Output stream success");
            } catch (IOException e) {
                Log.e("OUTPUT STREAM", "Error occurred when creating output stream", e);
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e("SOCKET CLOSE", "Could not close the client socket", e);
            }
        }
    }

}
