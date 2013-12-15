package com.abs.telecam;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.CountDownTimer;
import android.os.Handler;

import com.abs.telecam.adapters.DeviceBluetoothAdapter;
import com.abs.telecam.btxfr.ClientThread;
import com.abs.telecam.btxfr.ProgressData;
import com.abs.telecam.btxfr.ServerThread;
import com.abs.telecam.gui.ControllerViewer;
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
    public static ControllerViewer[] controllerViewers = new ControllerViewer[2];
    public static byte[] currentPreview;
    public static File lastPhotoFile;
    public static final int IMAGE_QUALITY = 100;
    public static String peer;
    public static final int second = 1000;
    public static final String PHOTO_COUNTDOWN_INTERVAL = "PHOTO_COUNTDOWN_INTERVAL";
    public static long[] vibratePattern = { 50, 300, 50, 300} ;
    public static int vibrateRepeat = 3;
    public static DeviceBluetoothAdapter newDevicesArrayAdapter;
    public static ProgressDialog loadingDialog;


    @Override
    public void onCreate() {
        super.onCreate();
        setBluetoothAdapter();
    }

    public static void setBluetoothAdapter(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        updatePairedDevicesList();
    }

    public static void updatePairedDevicesList(){
        if (adapter != null) {
            if (adapter.isEnabled()) {
                pairedDevices = adapter.getBondedDevices();
            }
        }
    }

    public static void showProgressDialog(Activity activity){
        loadingDialog = ProgressDialog.show(activity, "", activity.getString(R.string.please_wait), true);
        new CountDownTimer(20*second, second) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                dismissProgressDialog();
            }
        }.start();
    }

    public static void dismissProgressDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
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


