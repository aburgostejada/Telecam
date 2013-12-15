package com.abs.telecam.gui;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.helpers.gui.DialogHelper;
import com.abs.telecam.helpers.gui.ToastHelper;


public class Welcome extends Activity
{
    private static final int ENABLED_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        getActionBar().setTitle(R.string.camOrRemoteDialogTitle);
        DialogHelper dialogHelper = new DialogHelper(this);


        final Button isCamera = (Button) findViewById(R.id.setCamera);
        isCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Welcome.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    startCamViewer();
                } else {
                    ToastHelper.showLong(Welcome.this,R.string.cameraNotAvailable);
                }
            }
        });

        final Button isController = (Button) findViewById(R.id.setControl);
        isController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDashBoard();
            }
        });

        if(TeleCam.getAdapter(this) == null){
            dialogHelper.setYesNoDialog(R.string.bluetooth_is_required_title, R.string.bluetooth_is_required_message, new Handler.Callback(){
            @Override
                public boolean handleMessage(Message msg) {
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), ENABLED_BLUETOOTH);
                    return false;
                }
            }, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    finish();
                    return false;
                }
            });
        }else{
            TextView deviceName = (TextView) findViewById(R.id.deviceName);
            deviceName.setText(TeleCam.getAdapter(this).getName());
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLED_BLUETOOTH) {
            if(TeleCam.getAdapter(this) == null){
                finish();
            }else{
                TeleCam.setBluetoothAdapter();
            }
        }
    }

    private void startDashBoard(){
        Intent i = new Intent(this, Dashboard.class);
        this.startActivity(i);
    }

    private void startCamViewer(){
        Intent i = new Intent(this, CameraViewer.class);
        this.startActivity(i);
    }



}
