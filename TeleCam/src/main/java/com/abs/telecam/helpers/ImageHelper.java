package com.abs.telecam.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.helpers.gui.ToastHelper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHelper{

    private final Activity activity;
    public static String directory = "TeleCam";

    public ImageHelper(Activity activity){
        this.activity = activity;
    }

    public byte[] compress(Bitmap image){
        ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, TeleCam.IMAGE_QUALITY, compressedImageStream);
        return compressedImageStream.toByteArray();
    }

    public byte[] loadImageFromFile(File file){
        return compress(BitmapFactory.decodeFile(file.getAbsolutePath()));
    }

    public Bitmap getImageFromData(byte[] data) {
         return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public Bitmap resizeImage(Bitmap image, int previewWight) {
        final double proportion = (double)image.getHeight() / (double)image.getWidth();
        final int previewHeight = (int) (proportion * previewWight);
        return Bitmap.createScaledBitmap(image, previewWight, previewHeight, true);
    }

    private void updateMediaServiceForFile(String path){
        MediaScannerConnection.scanFile(activity, new String[] { path }, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                ToastHelper.toastOnUiThread(activity, R.string.imageSave);
            }
        });
    }

    public Bitmap rotateImage(int angle, Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }

    public File getFile(){
        String path = Environment.getExternalStorageDirectory() + File.separator + directory + File.separator ;
        String filename = "pic_"+ System.currentTimeMillis() +".jpg";
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        return new File(path+filename);
    }


    public void saveToFile(File file, Bitmap bitmap){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, TeleCam.IMAGE_QUALITY, fos);
            fos.close();
            updateMediaServiceForFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(Bitmap bitmap) {
        File file = getFile();
        saveToFile(file, bitmap);
    }

    public void saveToFile(byte[] imageBytes){
        BufferedOutputStream bos;
        try {
            File file = getFile();
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(imageBytes);
            bos.flush();
            bos.close();
            updateMediaServiceForFile(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
