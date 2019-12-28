package com.pine.base.component.scan.zxing;

import android.content.Context;
import android.graphics.Bitmap;

import com.pine.base.R;
import com.pine.base.component.scan.IScanAnalyzeListener;
import com.pine.base.component.scan.IScanManager;
import com.pine.tool.util.LogUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by tanghongfeng on 2019/11/28.
 */

public class ZXingScanManager implements IScanManager {
    private final static String TAG = LogUtils.makeLogTag(ZXingScanManager.class);
    private HashMap<Integer, CaptureFragment> mSurfaceFragmentMap = new HashMap<>();

    private static ZXingScanManager mInstance;

    public static IScanManager getInstance() {
        if (mInstance == null) {
            synchronized (ZXingScanManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use third scan: zxing");
                    mInstance = new ZXingScanManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void setAnalyzeListener(final IScanAnalyzeListener listener, int surfaceContainerId) {
        if (mSurfaceFragmentMap.containsKey(surfaceContainerId)) {
            setAnalyzeListener(mSurfaceFragmentMap.get(surfaceContainerId), listener);
        }
    }

    @Override
    public void attachScanSurface(FragmentActivity activity, final IScanAnalyzeListener listener, int surfaceContainerId) {
        CaptureFragment captureFragment = new CaptureFragment();
        //为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.base_scan_zxing_surface);
        //替换我们的扫描控件
        activity.getSupportFragmentManager().beginTransaction().replace(surfaceContainerId, captureFragment).commit();
        if (listener != null) {
            setAnalyzeListener(captureFragment, listener);
        }
        mSurfaceFragmentMap.put(surfaceContainerId, captureFragment);
    }

    @Override
    public void detachScanSurface(int surfaceContainerId) {
        mSurfaceFragmentMap.remove(surfaceContainerId);
    }


    private void setAnalyzeListener(@NonNull CaptureFragment captureFragment, final IScanAnalyzeListener listener) {
        if (captureFragment == null) {
            return;
        }
        captureFragment.setAnalyzeCallback(new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap bitmap, String result) {
                if (listener != null) {
                    listener.onAnalyzeSuccess(bitmap, result);
                }
            }

            @Override
            public void onAnalyzeFailed() {
                if (listener != null) {
                    listener.onAnalyzeFailed();
                }
            }
        });
    }
}
