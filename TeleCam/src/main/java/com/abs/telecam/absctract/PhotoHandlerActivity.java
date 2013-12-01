package com.abs.telecam.absctract;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public abstract class PhotoHandlerActivity extends FragmentActivity {
    //Status Update Enabled Activity
    public abstract void takePicture();
    public abstract void updatePreview(byte[] data);
    public abstract void sendFullResPhoto();
    public abstract void savePhoto(byte[] messageBytes);
}
