package com.abs.telecam.helpers.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;


public class Dialog {

    private Context context;

    public Dialog(Context context){
        this.context = context;
    }

    public void showAlert(String title, String message){
         AlertDialog alertDialog = new AlertDialog.Builder(context).create();
         alertDialog.setTitle(title);
         alertDialog.setMessage(message);
//         alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//               dialog.cancel();
//            }
//         });
        // alertDialog.setIcon(R.drawable.ic_menu_simec);
         alertDialog.show();
    }

    public void showToast(String message){
         Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showShortToast(String simecMonitorServiceDestroyed) {
        Toast.makeText(context, simecMonitorServiceDestroyed, Toast.LENGTH_SHORT).show();
    }
}
