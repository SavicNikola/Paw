package com.mosis.paw;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AddFriendActivity extends AppCompatActivity implements BluetoothListAdapter.BluetoothAdapterListener {

    BluetoothAdapter mBluetoothAdapter;

    ArrayList<BluetoothDevice> mBTDevices;
    BluetoothListAdapter mAdapter;

    Button bluetoothSearch;
    RecyclerView btRecyclerView;

    List<BluetoothDevice> paired;

    ClientClass clientClass;

    Boolean stateChangeReceiverRegistered = false;
    Boolean stateSearchRegistered = false;

    private static final String APP_NAME = "Paw";
    private static final UUID MY_UUID = UUID.fromString("6f3db676-82d2-11e8-adc0-fa7ae01bbebc");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        EditToolbar();

        initRecycleView();

        initBluetooth();
        initBond();
        initBluetoothSearch();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        paired = new ArrayList<BluetoothDevice>();
        paired.addAll(pairedDevices);

        //??
        ServerClass serverClass = new ServerClass();
        serverClass.start();
    }

    private void initRecycleView() {
        btRecyclerView = findViewById(R.id.bluetooth_recycle_view);
        mBTDevices = new ArrayList<>();
        mAdapter = new BluetoothListAdapter(this, mBTDevices, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        btRecyclerView.setLayoutManager(mLayoutManager);
        btRecyclerView.setItemAnimator(new DefaultItemAnimator());
        btRecyclerView.setAdapter(mAdapter);
    }

    private void initBluetoothSearch() {
        bluetoothSearch = findViewById(R.id.bluetooth_search_btn);

        bluetoothSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBTDevices.clear();
                mAdapter.notifyDataSetChanged();

                int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
                ActivityCompat.requestPermissions(AddFriendActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();

                mBluetoothAdapter.startDiscovery();
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(searchFoundReceiver, intentFilter);

                stateSearchRegistered = true;
            }
        });
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            // enable bt
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);

                // receiver for action
                IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(stateChangeReceiver, intentFilter);
                stateChangeReceiverRegistered = true;
            }
        }
    }

    private final BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "Bluetooth on!", Toast.LENGTH_SHORT).show();
                        initBluetoothDiscoverable();
                }
            }
        }
    };

    private void initBluetoothDiscoverable() {

        //Toast.makeText(this, "Making discoverable for 300 seconds!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(intent);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discoverableChangeReceiver, intentFilter);
    }

    private final BroadcastReceiver discoverableChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(context, "Discoverable on!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private final BroadcastReceiver searchFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initBond() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondChangedReceiver, intentFilter);
    }

    private final BroadcastReceiver bondChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    startClientOnDevice(device);
                }
            }
        }
    };

    private void startClientOnDevice(BluetoothDevice device) {
        Toast.makeText(this, "Connect with " + device.getName(), Toast.LENGTH_SHORT).show();

        clientClass = new ClientClass(device);
        clientClass.start();
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Add friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (stateChangeReceiverRegistered) {
            unregisterReceiver(stateChangeReceiver);
            unregisterReceiver(discoverableChangeReceiver);
        }

        if (stateSearchRegistered)
            unregisterReceiver(searchFoundReceiver);

        unregisterReceiver(bondChangedReceiver);
    }

    @Override
    public void onItemSelected(BluetoothDevice item) {
        Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();

        mBluetoothAdapter.cancelDiscovery();

        if (paired.contains(item))
            startClientOnDevice(item);
        else
            // pair
            item.createBond();
    }

    SendReceive sendReceive;

    @Override
    public void onItemLongSelected(BluetoothDevice item) {
        String poruka = "Cao cao";

        if (sendReceive != null)
            sendReceive.write(poruka.getBytes());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTION_FAILED = 3;
    private static final int STATE_MESSAGE_RECEIVED = 4;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_CONNECTING:
                    Toast.makeText(AddFriendActivity.this, "Server started!", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTION_FAILED:
                    Toast.makeText(AddFriendActivity.this, "Connection failed..", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTED:
                    Toast.makeText(AddFriendActivity.this, "Client started!", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String poruka = new String(readBuff, 0, msg.arg1);
                    Toast.makeText(AddFriendActivity.this, poruka, Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    });

    private class ServerClass extends Thread {

        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            BluetoothSocket socket = null;

            while (socket == null) {

                try {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothSocket socket;
        public BluetoothDevice device;

        public ClientClass(BluetoothDevice device) {
            this.device = device;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            this.socket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;

            // uvek smo spremni za poruku
            while (true) {
                try {
                    bytes = inputStream.read(buffer); //uzimamo pristiglu poruku

                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        // za slanje
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
