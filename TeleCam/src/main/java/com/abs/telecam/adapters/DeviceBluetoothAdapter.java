package com.abs.telecam.adapters;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abs.telecam.R;


public class DeviceBluetoothAdapter extends ArrayAdapter<BluetoothDevice> {

    private Context context;

    public DeviceBluetoothAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item, null);
        }

        BluetoothDevice item = getItem(position);
        if (item!= null) {
            TextView itemView = (TextView) view.findViewById(R.id.new_device_label);
            if (itemView != null) {
                itemView.setText(item.getName() + " " + item.getAddress());
            }
        }

        return view;
    }


}
