package com.abs.telecam.factory;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.abs.telecam.gui.Dashboard;

import java.io.IOException;
import java.util.List;


public class SurfaceHolderCallBack implements SurfaceHolder.Callback   {


    private Camera camera;
    private boolean isPreviewRunning;


    private Camera.Size getHighestResolution(List<Camera.Size> availableResolutions){
        int w = 0;
        int h = 0;
        int index = 0;
        int sel = 0;
        for(Camera.Size s : availableResolutions){
            index++;
            if(w < s.width && h < s.height){
                w =  s.width;
                h =  s.height;
                sel = index;
            }
        }
        return availableResolutions.get(sel);
    }

    public void takePicture(Camera.PictureCallback imageCallback) {
        camera.takePicture(null, null, imageCallback);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters params;
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//        List<Camera.Size> size = params.getSupportedPreviewSizes();
//        params.setPreviewSize(size.get(0).width, size.get(0).height);
        Camera.Size maxRex = getHighestResolution(params.getSupportedPictureSizes());
        params.setPictureSize(maxRex.width, maxRex.height);
        camera.setParameters(params);
        camera.startPreview();

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();

        if (isPreviewRunning) {
            camera.stopPreview();
        }
        try {
            camera.startPreview();
        } catch(Exception e) {
            Log.e(Dashboard.TAG, e.getMessage());
        }
        isPreviewRunning = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if(isPreviewRunning && camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                isPreviewRunning = false;
            }
        }
        catch (Exception e) {
            Log.e(Dashboard.TAG, e.getMessage());
        }
    }
}
