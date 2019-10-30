package com.pine.tool.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by tanghongfeng on 2019/10/18.
 */

public class TypeConvertUtils {
    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * bitmap 转 byte[]数组
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * byte[]数组 转 bitmap
     */
    public static Bitmap toBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    /**
     * bitmap 转 InputStream
     */
    public static InputStream toInputStream(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * InputStream 转 bitmap
     */
    public static Bitmap toBitmap(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    /**
     * Drawable 转 bitmap
     */
    public static Bitmap toBitmap(Drawable img) {
        BitmapDrawable bd = (BitmapDrawable) img;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    /**
     * bitmap 转 Drawable
     */
    public static Drawable toDrawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable img = bd;
        return img;
    }

    /**
     * String 转 byte[]数组
     */
    public static byte[] toByteArray(String str, String charset) {
        byte[] bytes = null;
        if (charset == null) {
            bytes = str.getBytes();
        } else {
            try {
                // 如charset = "utf-8"
                bytes = str.getBytes(charset);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return bytes;
    }

    /**
     * String 转 byte[]数组
     */
    public static String toString(byte[] bytes, String charset) {
        String string = null;
        try {
            string = new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }
}
