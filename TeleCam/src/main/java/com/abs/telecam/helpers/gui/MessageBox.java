package com.abs.telecam.helpers.gui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


import java.util.concurrent.Callable;

public class MessageBox {
    private Context context;

    MessageBox(Context context) {
        this.context = context;
    }

    public void Show(final Callable<Void> pos, final Callable<Void> neg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Esta seguro?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                           pos.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                           neg.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();

        builder.show();
    }
}
