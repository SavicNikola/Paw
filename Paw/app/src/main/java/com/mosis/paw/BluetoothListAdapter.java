package com.mosis.paw;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.MyViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> bluetoothList;

    // za select
    private BluetoothAdapterListener listener;

    // View holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, adress;

        public MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.bluetooth_name);
            adress = v.findViewById(R.id.bluetooth_address);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // selektovan prijatelj
                    listener.onItemSelected(bluetoothList.get(getAdapterPosition()));
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongSelected(bluetoothList.get(getAdapterPosition()));
                    return false;
                }
            });
        }
    }

    public BluetoothListAdapter(Context mContext, List<BluetoothDevice> bluetoothList, BluetoothAdapterListener listener) {
        this.mContext = mContext;
        this.bluetoothList = bluetoothList;

        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BluetoothDevice item = bluetoothList.get(position);

        holder.name.setText(item.getName());
        holder.adress.setText(item.getAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothList.size();
    }

    public interface BluetoothAdapterListener {
        void onItemSelected(BluetoothDevice item);
        void onItemLongSelected(BluetoothDevice item);
    }
}
