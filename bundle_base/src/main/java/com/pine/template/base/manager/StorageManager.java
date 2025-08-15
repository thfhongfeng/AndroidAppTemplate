package com.pine.template.base.manager;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;

import com.pine.template.base.bean.DiskInfo;
import com.pine.template.base.bgwork.BgWorkManager;
import com.pine.template.base.business.track.AppTrackManager;
import com.pine.template.base.business.track.TrackDefaultBuilder;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.template.bundle_base.R;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageManager {
    private final String TAG = this.getClass().getSimpleName();

    public static final String TYPE_STORAGE_NOT_ENOUGH = "storageNotEnough";
    public static final String TYPE_EXTERNAL_STORAGE_NOT_ENOUGH = "externalStorageNotEnough";

    private static StorageManager instance;

    public static synchronized StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    private Context mContext;
    private ActivityManager mActivityManager;

    private StorageManager() {
        mContext = AppUtils.getApplicationContext();
        mActivityManager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
    }

    public void checkDisk() {
        checkDefaultDisk(false);
        checkExternalDisk(false);
    }

    public synchronized boolean checkDefaultDisk(boolean onlyCheck) {
        DiskInfo defaultDiskInfo = getDefaultDiskInfo();
        long threshold = 50 * 1024 * 1024L;
        if (defaultDiskInfo != null && defaultDiskInfo.leftSize < threshold) {
            if (!onlyCheck) {
                LogUtils.d(TAG, "checkDefaultDisk TYPE_STORAGE_NOT_ENOUGH");
                BgWorkManager.sendBgAction(TYPE_STORAGE_NOT_ENOUGH, defaultDiskInfo);
                int del = AppTrackManager.getInstance().deleteForStorageOut(1000, 5000);
                if (del > 0) {
                    recordInfoDefaultStorageNotEnough(false, threshold, del);
                }
            }
            return false;
        }
        return true;
    }

    public synchronized boolean checkExternalDisk(boolean onlyCheck) {
        DiskInfo externalDiskInfo = getExternalDiskInfo();
        if (externalDiskInfo == null) {
            return false;
        }
        long threshold = 50 * 1024 * 1024L;
        if (externalDiskInfo != null && externalDiskInfo.leftSize < threshold) {
            if (!onlyCheck) {
                LogUtils.d(TAG, "checkExternalDisk TYPE_EXTERNAL_STORAGE_NOT_ENOUGH");
                BgWorkManager.sendBgAction(TYPE_EXTERNAL_STORAGE_NOT_ENOUGH, externalDiskInfo);
                recordInfoExternalStorageNotEnough(true, threshold);
            }
            return false;
        }
        return true;
    }

    public synchronized ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public synchronized DiskInfo getDefaultDiskInfo() {
        return getDiskInfo(PathUtils.getAppFilePath(null));
    }

    public synchronized DiskInfo getExternalDiskInfo() {
        return getDiskInfo("/mnt/sdcard1");
    }

    public synchronized DiskInfo getDiskInfo(String diskDir) {
        try {
            DiskInfo diskInfo = null;
            File root = new File(diskDir);
            if (!root.exists()) {
                LogUtils.d(TAG, "getDiskInfo root not exist:" + diskDir);
                return null;
            }
            StatFs statfs = new StatFs(root.getPath());
            long blockSize = statfs.getBlockSizeLong();
            long totalBlocks = statfs.getBlockCountLong();
            long leftBlock = statfs.getAvailableBlocksLong();
            diskInfo = new DiskInfo();
            diskInfo.disPath = root.getPath();
            diskInfo.leftSize = leftBlock * blockSize;
            diskInfo.totalSize = totalBlocks * blockSize;
            if (BuildConfig.DEBUG) {
                LogUtils.d(TAG, "getDiskInfo diskInfo:" + diskInfo);
            }
            return diskInfo;
        } catch (Exception e) {
            return null;
        }
    }

    private final String DEFAULT_CUR_CLASS = "TrackRecordHelper";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean checkStorage() {
        boolean enough = checkDefaultDisk(true);
        if (!enough) {
            LogUtils.d(TAG, "storage not enough, ignore track job");
        }
        return enough;
    }

    public void recordInfoDefaultStorageNotEnough(boolean external, long threshold, int trackDelCount) {
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.info_storage_not_enough,
                mSimpleDateFormat.format(recordDate), Formatter.formatFileSize(mContext, threshold), trackDelCount);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_STORAGE_NOT_ENOUGH, actionData,
                recordDate.getTime(), true);
    }

    public void recordInfoExternalStorageNotEnough(boolean external, long threshold) {
        if (!checkStorage()) {
            return;
        }
        Date recordDate = new Date();
        String actionData = mContext.getString(R.string.info_external_storage_not_enough,
                mSimpleDateFormat.format(recordDate), Formatter.formatFileSize(mContext, threshold));
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_STATE_INFO, DEFAULT_CUR_CLASS,
                TrackDefaultBuilder.INFO_STORAGE_NOT_ENOUGH, actionData,
                recordDate.getTime(), true);
    }
}
