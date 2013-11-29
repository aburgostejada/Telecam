package com.abs.telecam.helpers;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class ObjectSerializer {
    private static String TAG = "ObjectSerializer";

    public static byte[] objToByte(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            yourBytes = bos.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            bos.close();
        }
        return yourBytes;
    }

    public static Object byteToObj(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bis.close();
            if (in != null) {
                in.close();
            }
        }
        return o;
    }

}
