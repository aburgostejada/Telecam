package com.abs.telecam.helpers.gui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DialogHelper {

    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String CANCEL = "Cancel";
    private static final String CLOSE = "Close";
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
        if(TeleCam.newDeviceDialog != null){
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select a device to connect: ");

        if(TeleCam.newDevicesArrayAdapter == null){
            return;
        }
        builderSingle.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
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
                TeleCam.showProgressDialog(activity,R.string.delete_action_dialog_title, R.string.no_devices_available_message);
            }
        });
        TeleCam.newDeviceDialog = builderSingle.show();
    }

    public void showAlert(final int title, int message) {
        if(TeleCam.alerts.get(title) != null){
            return;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(activity.getString(message));
        alertDialog.setCancelable(true);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, CLOSE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                TeleCam.alerts.remove(title);
            }
        });
        alertDialog.show();
        TeleCam.alerts.put(title, alertDialog);
    }
}
