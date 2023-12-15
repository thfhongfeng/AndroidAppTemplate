package com.pine.tool.util;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsbDeviceUtils {
    private final static String TAG = UsbDeviceUtils.class.getSimpleName();

    private static final int USB_CAMERA_TYPE = 14; //可能跟不同系统设备相关，一般是某个固定值，可以打Log验证。

    /**
     * 判断当前Usb设备是否是Camera设备
     */
    public static boolean isUsbCameraDevice(UsbDevice usbDevice) {
        LogUtils.i(TAG, "isUsbCameraDevice usbDevice:" + usbDevice.getProductName() + " "
                + usbDevice.getDeviceClass() + ", subclass = " + usbDevice.getDeviceSubclass());
        if (usbDevice == null) {
            return false;
        }
        boolean isCamera = false;
        int interfaceCount = usbDevice.getInterfaceCount();
        for (int interIndex = 0; interIndex < interfaceCount; interIndex++) {
            UsbInterface usbInterface = usbDevice.getInterface(interIndex);
            //usbInterface.getName()遇到过为null的情况
            if ((usbInterface.getName() == null || usbDevice.getProductName().equals(usbInterface.getName()))
                    && usbInterface.getInterfaceClass() == USB_CAMERA_TYPE) {
                isCamera = true;
                break;
            }
        }
        LogUtils.i(TAG, "usbDevice: " + usbDevice.getProductName() + " isCamera: " + isCamera);
        return isCamera;
    }

    /**
     * 通过 UsbManager获取当前外设摄像头信息
     */
    public static List<UsbDevice> getCameraList(Context context) {
        List<UsbDevice> cameraList = new ArrayList<>();
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE); //这个只适用于Usb设备
        HashMap<String, UsbDevice> mDeviceMap = mUsbManager.getDeviceList();
        if (mDeviceMap != null) {
            for (UsbDevice usbDevice : mDeviceMap.values()) {
                if (isUsbCameraDevice(usbDevice)) {
                    cameraList.add(usbDevice);
                }
            }
        }
        return cameraList;
    }

    /**
     * 通过 UsbManager获取当前外设摄像头名称信息
     */
    public static List<String> getCameraStringList(Context context) {
        List<String> cameraList = new ArrayList<>();
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceMap = mUsbManager.getDeviceList();
        if (mDeviceMap != null) {
            for (UsbDevice usbDevice : mDeviceMap.values()) {
                if (isUsbCameraDevice(usbDevice)) {
                    cameraList.add(usbDevice.getProductName());
                }
            }
        }
        return cameraList;
    }
}
