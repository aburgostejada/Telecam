package com.abs.telecam.absctract;


import android.app.Activity;

public abstract class PhotoHandlerActivity extends Activity {
    //Status Update Enabled Activity
    public abstract void takePicture();
    public abstract void updatePreview(byte[] data);
    public abstract void sendFullResPhoto();
    public abstract void savePhoto(byte[] messageBytes);
}
