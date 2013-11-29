package com.abs.telecam.helpers.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Spinner;

import com.abs.telecam.BlueMessage;
import com.abs.telecam.DeviceData;
import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.absctract.PhotoHandlerActivity;
import com.abs.telecam.btxfr.ClientThread;
import com.abs.telecam.btxfr.MessageType;
import com.abs.telecam.btxfr.ProgressData;
import com.abs.telecam.btxfr.ServerThread;
import com.abs.telecam.gui.CameraViewer;
import com.abs.telecam.gui.Dashboard;
import com.abs.telecam.helpers.ImageHelper;
import com.abs.telecam.helpers.ObjectSerializer;
import com.abs.telecam.helpers.gui.ToastHelper;

import java.io.IOException;
import java.util.Set;

public class BluetoothHelper {
    public static final String SAVE_PHOTO = "save_photo";
    public static final String SHOOT = "shoot";
    public final PhotoHandlerActivity activity;
    private String currentMethod;

    private ProgressDialog progressDialog;
    private String TAG = "BluetoothHelper";
    private ImageHelper imageHelper;
    private BluetoothAdapter adapter;


    public BluetoothHelper(PhotoHandlerActivity activity){
        this.activity = activity;
        this.imageHelper = new ImageHelper(activity);
        this.adapter = TeleCam.getAdapter(activity);
    }

    public void initializeServer(){
        if (TeleCam.pairedDevices != null) {
            if (TeleCam.serverThread == null) {
                TeleCam.serverThread = new ServerThread(adapter, TeleCam.serverHandler);
                TeleCam.serverThread.start();
            }
        }
    }

    public void ensureDiscoverable() {
        if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivity(discoverableIntent);
        }
    }

    public void openBlueToothSettings() {
        activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }


    public void sendMessageToLink(byte[] MessageBytes){
        try {
            Message message = new Message();
            message.obj = MessageBytes;
            TeleCam.clientThread.incomingHandler.sendMessage(message);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void sendMessageToPair(String command, final String tag){
        setCurrentMethod(command);
        if(tag.equals(Dashboard.TAG)){
            Spinner deviceSpinner = (Spinner) activity.findViewById(R.id.deviceSpinner);
            DeviceData deviceData = (DeviceData) deviceSpinner.getSelectedItem();
            for (BluetoothDevice device : adapter.getBondedDevices()) {
                if (device.getAddress().contains(deviceData.getValue())) {
                    if (TeleCam.clientThread != null) {
                        TeleCam.clientThread.cancel();
                    }
                    TeleCam.clientThread = new ClientThread(device, TeleCam.clientHandler);
                    TeleCam.clientThread.start();
                }
            }
        }else if(tag.equals(CameraViewer.TAG)){
            Set<BluetoothDevice> deviceList = adapter.getBondedDevices();
            for (BluetoothDevice device : deviceList) {
                if (device.getAddress().equals(TeleCam.peer)) {
                    if (TeleCam.clientThread != null) {
                        TeleCam.clientThread.cancel();
                    }
                    TeleCam.clientThread = new ClientThread(device, TeleCam.clientHandler);
                    TeleCam.clientThread.start();
                }
            }
        }

    }


    public Handler getClientHandlerFor(final String tag) {
        return new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MessageType.READY_FOR_DATA: {
                        if(tag.equals(CameraViewer.TAG)){
                            new Thread(new Runnable() {
                                public void run() {
                                    if(getCurrentMethod().equals(BluetoothHelper.SHOOT)){
                                        sendMessageToLink(BlueMessage.newFrom(BluetoothHelper.SHOOT, null, TeleCam.currentPreview));
                                        TeleCam.currentPreview = null;
                                    }else if(getCurrentMethod().equals(BluetoothHelper.SAVE_PHOTO)){
                                        if(TeleCam.lastPhotoFile != null){
                                            sendMessageToLink(BlueMessage.newFrom(BluetoothHelper.SAVE_PHOTO, null, imageHelper.loadImageFromFile(TeleCam.lastPhotoFile)));
                                            TeleCam.lastPhotoFile = null;
                                        }
                                    }
                                }
                            }).start();
                        }else if(tag.equals(Dashboard.TAG)){
                            new Thread(new Runnable() {
                                public void run() {
                                    BlueMessage blueMessage = BlueMessage.newFrom(getCurrentMethod(), adapter.getAddress(), new byte[0]);
                                    try {
                                        sendMessageToLink(ObjectSerializer.objToByte(blueMessage));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e(tag,e.getMessage());
                                    }
                                }
                            }).start();
                        }
                        break;
                    }

                    case MessageType.COULD_NOT_CONNECT: {
                        ToastHelper.showShort(activity, R.string.couldNotConnectToPeer);
                        break;
                    }

                    case MessageType.SENDING_DATA: {
                        progressDialog = new ProgressDialog(activity);
                        if(tag.equals(CameraViewer.TAG)){
                            progressDialog.setMessage(activity.getString(R.string.sendingPhoto));
                        }else if(tag.equals(Dashboard.TAG)){
                            progressDialog.setMessage(activity.getString(R.string.takingPicture));
                        }
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        break;
                    }
                    case MessageType.DATA_SENT_OK: {
                        resetProgressDialog();
                        if(tag.equals(Dashboard.TAG)){
                            ToastHelper.showShort(activity, R.string.takePhotoRequestSentSuccessfully);
                        }else if(tag.equals(CameraViewer.TAG)){
                            ToastHelper.showShort(activity, R.string.photoSentSuccessfully);
                        }
                        break;
                    }

                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        ToastHelper.showShort(activity, R.string.errorInTransmission);
                        break;
                    }
                }
            }
        };
    }

    private void sendMessageToLink(BlueMessage blueMessage) {
        try {
            sendMessageToLink(ObjectSerializer.objToByte(blueMessage));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String getCurrentMethod(){
        return this.currentMethod;
    }

    public void setCurrentMethod(String currentMethod){
        this.currentMethod = currentMethod;
    }

    public void onDestroy() {
        if(TeleCam.clientThread != null){
            TeleCam.clientThread.cancel();
            TeleCam.clientThread = null;
        }
        if(TeleCam.serverThread != null){
            TeleCam.serverThread.cancel();
            TeleCam.serverThread = null;
        }
    }

    public void onStart() {
        initializeServer();
    }


    public void onStop(){
        resetProgressDialog();
    }

    private void resetProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public Handler getServerHandler(final String tag) {
        return new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {

                    case MessageType.DATA_RECEIVED: {
                       resetProgressDialog();
                        byte[] messageBytes = (byte[]) message.obj;
                        BlueMessage blueMessage = null;
                        try {
                            blueMessage = (BlueMessage) ObjectSerializer.byteToObj(messageBytes);
                        } catch (IOException e) {
                            Log.e(tag, e.getMessage());
                        }
                       if(tag.equals(CameraViewer.TAG) && blueMessage != null){
                           TeleCam.peer = blueMessage.source;
                           if(blueMessage.command.equals(BluetoothHelper.SHOOT)){
                               activity.takePicture();
                           }else if(blueMessage.command.equals(BluetoothHelper.SAVE_PHOTO)){
                               activity.sendFullResPhoto();
                           }
                       }else if(tag.equals(Dashboard.TAG) && blueMessage != null){
                           if(blueMessage.command.equals(BluetoothHelper.SAVE_PHOTO)){
                               activity.savePhoto(blueMessage.payload);
                           }else if(blueMessage.command.equals(BluetoothHelper.SHOOT)){
                               activity.updatePreview(blueMessage.payload);
                           }
                       }
                       break;
                    }
                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        ToastHelper.showShort(activity, R.string.errorInReception);
                        break;
                    }

                    case MessageType.DATA_PROGRESS_UPDATE: {
                        TeleCam.progressData = (ProgressData) message.obj;
                        double pctRemaining = 100 - (((double) TeleCam.progressData.remainingSize / TeleCam.progressData.totalSize) * 100);
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(activity);
                            progressDialog.setMessage(activity.getString(R.string.ReceivingPhoto));
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setProgress(0);
                            progressDialog.setMax(100);
                            progressDialog.show();
                        }
                        progressDialog.setProgress((int) Math.floor(pctRemaining));
                        break;
                    }

                    case MessageType.INVALID_HEADER: {
                        ToastHelper.showShort(activity, R.string.invalidHeader);
                        break;
                    }
                }
            }
        };
    }
}


