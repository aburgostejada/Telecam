package com.abs.telecam.gui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.abs.telecam.DeviceData;
import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;
import com.abs.telecam.helpers.gui.ToastHelper;

import java.util.ArrayList;

class ControllerBindings{


    private final Activity activity;
    private int photoAngleRotation = 0;

    public ControllerBindings(Activity activity){
        this.activity = activity;
    }

    public void setUpEventsForController(View view){
        if (TeleCam.pairedDevices != null) {
            ArrayList<DeviceData> deviceDataList = new ArrayList<DeviceData>();
            for (BluetoothDevice device : TeleCam.pairedDevices) {
                deviceDataList.add(new DeviceData(device.getName(), device.getAddress()));
            }
            setUpSpinner(view, deviceDataList);
            setUpShooterButton(view);
        } else {
            ToastHelper.showLong(this.activity, R.string.bluetoothNotAvailableOrNotSupported);
        }
        setUpRotateButtons(view);
        setUpSavePhotoButton(view);
    }

    private void vibrate(){
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(TeleCam.vibratePattern, TeleCam.vibrateRepeat);
    }

    private void setUpShooterButton(View view) {
        final Button clientButton = (Button) view.findViewById(R.id.shutterButton);
        final TextView timerLabel = (TextView) view.findViewById(R.id.timerLabel);

        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpTimerForShooterButton(timerLabel);
            }
        });
    }

    private int getPhotoCountdownInterval(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
        String photo_countdown_interval = settings.getString(TeleCam.PHOTO_COUNTDOWN_INTERVAL, "5");
        return Integer.parseInt(photo_countdown_interval) * TeleCam.second;
    }

    private void setUpTimerForShooterButton(final TextView timerLabel){
        new CountDownTimer(getPhotoCountdownInterval(), TeleCam.second) {
            public void onTick(long millisUntilFinished) {
                timerLabel.setText("( "+millisUntilFinished / TeleCam.second +" )");
            }

            public void onFinish() {
                sendTakePictureRequest();
                vibrate();
                timerLabel.setText("");
            }
        }.start();
    }

    private void setUpSavePhotoButton(View view) {
        Button saveButton = (Button) view.findViewById(R.id.savePhoto);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSavePhotoRequest();
            }
        });
        saveButton.setEnabled(false);
    }

    private void setUpRotateButtons(View view){
        Button rotatePhotoRight = (Button) view.findViewById(R.id.rotateRight);
        Button rotatePhotoLeft = (Button) view.findViewById(R.id.rotateLeft);
        rotatePhotoRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotatePhoto(view.getRootView(), -1);
            }
        });
        rotatePhotoLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotatePhoto(view.getRootView(), 1);
            }
        });
    }

    private void setUpSpinner(View view, ArrayList<DeviceData> deviceDataList){
        Spinner deviceSpinner = (Spinner) view.findViewById(R.id.deviceSpinner);
        ArrayAdapter<DeviceData> deviceArrayAdapter = new ArrayAdapter<DeviceData>(activity, android.R.layout.simple_spinner_item, deviceDataList);
        deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(deviceArrayAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Dashboard.PreferredPeer, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
        int preferredPeer = settings.getInt(Dashboard.PreferredPeer, -1);
        if(preferredPeer > -1){
            deviceSpinner.setSelection(preferredPeer);
        }
    }



    private void sendTakePictureRequest(){
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SHOOT, Dashboard.TAG);
    }

    private void sendSavePhotoRequest(){
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SAVE_PHOTO, Dashboard.TAG);
    }

    private void rotatePhoto(View view, int direction){
        ImageView imageView = (ImageView) view.findViewById(R.id.pic_preview);
        photoAngleRotation+=(90*direction);
        imageView.setRotation(photoAngleRotation);
    }
}

