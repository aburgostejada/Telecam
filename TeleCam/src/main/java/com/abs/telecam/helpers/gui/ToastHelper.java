package com.abs.telecam.helpers.gui;
import android.app.Activity;

public class ToastHelper {

    public static void show(Activity activity, String text){
        android.widget.Toast toast = android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLong(Activity activity, String text){
        android.widget.Toast toast = android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLong(Activity activity, int text){
        android.widget.Toast toast = android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showShort(Activity activity, String text){
        android.widget.Toast toast = android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showShort(Activity activity, int text){
        android.widget.Toast toast = android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastOnUiThread(final Activity activity, final String text) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                showLong(activity, text);
            }
        });
    }

    public static void toastOnUiThread(final Activity activity, final int text) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                showLong(activity, activity.getString(text));
            }
        });
    }


}
