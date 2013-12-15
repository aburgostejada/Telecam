package com.abs.telecam.helpers.gui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;

import java.util.AbstractCollection;

public class DialogHelper {

    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String CANCEL = "Cancel";
    private final Activity activity;

    public DialogHelper(Activity activity){
        this.activity = activity;

    }

    public void CamOrRemoteDialog(){
//        new AlertDialog.Builder(activity)
//                .setTitle(R.string.camOrRemoteDialogTitle)
//                .setMessage(R.string.camOrRemoteDialogMessage)
//                .setCancelable(false)
//                .setPositiveButton(R.string.control, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        startDashBoard(false);
//                    }
//                }).setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        startDashBoard(true);
//                    }
//        }).show();
    }



    public void setYesNoDialog(int dialogTitle, int dialogMessage, final Handler.Callback yesCallback, final Handler.Callback noCallback){
        setYesNoDialog(activity.getString(dialogTitle),activity.getString(dialogMessage), yesCallback, noCallback);
    }

    public void setYesNoDialog(String dialogTitle, String dialogMessage, final Handler.Callback yesCallback, final Handler.Callback noCallback){
        new AlertDialog.Builder(activity)
        .setTitle(dialogTitle)
        .setMessage(dialogMessage)
        .setPositiveButton(YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Message message = new Message();
                yesCallback.handleMessage(message);
                dialog.dismiss();
            }
        }).setNegativeButton(NO, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Message message = new Message();
                noCallback.handleMessage(message);
                dialog.cancel();
            }
        }).show();
    }

    public void setNewDeviceDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select a device to connect: ");



        if(TeleCam.newDevicesArrayAdapter == null){
            return;
        }
        builderSingle.setNegativeButton(CANCEL,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(TeleCam.newDevicesArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TeleCam.bluetoothHelper.pairDevice(TeleCam.newDevicesArrayAdapter.getItem(which));
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }
}
