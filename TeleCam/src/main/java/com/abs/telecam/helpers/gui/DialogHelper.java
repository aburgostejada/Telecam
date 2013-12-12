package com.abs.telecam.helpers.gui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

public class DialogHelper {

    private static final String YES = "Yes";
    private static final String NO = "No";
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
                }
            }).setNegativeButton(NO, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Message message = new Message();
                    yesCallback.handleMessage(message);
                    dialog.cancel();
                }
            }).show();
    }
}
