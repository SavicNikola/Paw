package com.mosis.paw;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity implements BluetoothListAdapter.BluetoothAdapterListener {

    BluetoothAdapter mBluetoothAdapter;

    ArrayList<BluetoothDevice> mBTDevices;
    BluetoothListAdapter mAdapter;

    Button bluetoothSearch;
    RecyclerView btRecyclerView;

    Boolean stateChangeReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        EditToolbar();

        initRecycleView();

        initBluetooth();
        initBluetoothSearch();

        initBond();
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

                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();

                mBluetoothAdapter.startDiscovery();
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(searchFoundReceiver, intentFilter);
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
                    Toast.makeText(context, "Povezan", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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
    }

    @Override
    public void onItemSelected(BluetoothDevice item) {
        Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();

        // pair
        item.createBond();

        mBluetoothAdapter.cancelDiscovery();
    }
}
