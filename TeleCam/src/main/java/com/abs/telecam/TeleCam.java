package com.abs.telecam;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.abs.telecam.btxfr.ClientThread;
import com.abs.telecam.btxfr.ProgressData;
import com.abs.telecam.btxfr.ServerThread;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;
import com.abs.telecam.helpers.gui.ToastHelper;

import java.io.File;
import java.util.Set;

public class TeleCam extends Application {
    private static BluetoothAdapter adapter;
    public static BluetoothHelper bluetoothHelper;
    public static Set<BluetoothDevice> pairedDevices;
    public static Handler clientHandler;
    public static Handler serverHandler;
    public static ClientThread clientThread;
    public static ServerThread serverThread;
    public static ProgressData progressData = new ProgressData();
    public static byte[] currentPreview;
    public static File lastPhotoFile;
    public static final int IMAGE_QUALITY = 100;
    public static String peer;

    @Override
    public void onCreate() {
        super.onCreate();
        setBluetoothAdapter();
    }

    public static void setBluetoothAdapter(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (adapter.isEnabled()) {
                pairedDevices = adapter.getBondedDevices();
            }
        }
    }

    public static BluetoothAdapter getAdapter(Activity activity){
        if (adapter == null) {
            ToastHelper.showLong(activity, R.string.bluetoothNotAvailable);
            return null;
        } else {
            if (!adapter.isEnabled()) {
                ToastHelper.showLong(activity, R.string.bluetoothNotEnabled);
                return null;
            }
            return adapter;
        }
    }
}


