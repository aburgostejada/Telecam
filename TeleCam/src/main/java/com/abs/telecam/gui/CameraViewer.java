package com.abs.telecam.gui;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.absctract.PhotoHandlerActivity;
import com.abs.telecam.factory.SurfaceHolderCallBack;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;
import com.abs.telecam.helpers.ImageHelper;
import com.abs.telecam.helpers.gui.BlueToothActionsMenuHelper;

public class CameraViewer extends PhotoHandlerActivity implements Camera.PictureCallback {
    private SurfaceView surface_view;
    public static final String TAG = "Camera";

    SurfaceHolder surface_holder = null;
    SurfaceHolderCallBack sh_callback  = null;
    Button visibleButton;
    BluetoothAdapter adapter;
    ImageHelper imageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeleCam.bluetoothHelper = new BluetoothHelper(this);
        adapter = TeleCam.getAdapter(this);
        imageHelper = new ImageHelper(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        surface_view = new SurfaceView(getApplicationContext());

        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        visibleButton = new Button(this);
        visibleButton.getBackground().setAlpha(30);
        visibleButton.setText("Visible");
        visibleButton.setTextColor(Color.GREEN);
        RelativeLayout.LayoutParams lButtonParams = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT );

        lButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        visibleButton.setLayoutParams(lButtonParams);
        layout.addView(surface_view);
        layout.addView(visibleButton);
        visibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeleCam.bluetoothHelper.ensureDiscoverable();
            }
        });
        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (surface_holder == null) {
            surface_holder = surface_view.getHolder();
        }
        sh_callback = new SurfaceHolderCallBack();
        surface_holder.addCallback(sh_callback);

        TeleCam.clientHandler = TeleCam.bluetoothHelper.getClientHandlerFor(TAG);
        TeleCam.serverHandler = TeleCam.bluetoothHelper.getServerHandler(TAG);
        TeleCam.bluetoothHelper.initializeServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TeleCam.bluetoothHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onResume();
        TeleCam.bluetoothHelper.onStart();
    }

    @Override
    public void updatePreview(byte[] data) {
        //TODO Remove
    }

    @Override
    public void savePhoto(byte[] messageBytes) {
        //TODO Remove
    }

    public void takePicture() {
        sh_callback.takePicture(this);
    }

    public void sendFullResPhoto() {
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SAVE_PHOTO, TAG);

    }

    public void sendPicturePreview(){
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SHOOT, TAG);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = imageHelper.getImageFromData(data);
        TeleCam.currentPreview = imageHelper.compress(imageHelper.resizeImage(bitmap, 1080));
        sendPicturePreview();
        imageHelper.saveToFile(bitmap);
        camera.startPreview();
    }

}


