package com.pine.template.base.component.scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.pine.template.base.R;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/11/28.
 */

public class ScanManager {
    private final static String TAG = LogUtils.makeLogTag(ScanManager.class);
    private static volatile IScanManager mManagerImpl;

    private ScanManager() {

    }

    private static boolean isInit() {
        return mManagerImpl != null;
    }

    public static void init(Context context, IScanManagerFactory factory) {
        mManagerImpl = factory.makeScanManager(context);
        if (mManagerImpl != null) {
            mManagerImpl.init(context);
        }
    }

    public static void attachScanSurface(final FragmentActivity activity, @IdRes int surfaceContainerId) {
        if (!isInit()) {
            return;
        }
        mManagerImpl.attachScanSurface(activity, null, surfaceContainerId);
    }

    public static void setAnalyzeListener(@NonNull final IScanAnalyzeListener listener, int surfaceContainerId) {
        if (!isInit()) {
            return;
        }
        mManagerImpl.setAnalyzeListener(new GateListener(listener), surfaceContainerId);
    }

    public static void attachScanSurface(final FragmentActivity activity,
                                         final IScanAnalyzeListener listener, @IdRes int surfaceContainerId) {
        if (!isInit()) {
            return;
        }
        mManagerImpl.attachScanSurface(activity, new GateListener(listener), surfaceContainerId);
    }

    public static void detachScanSurface(@IdRes int surfaceContainerId) {
        if (!isInit()) {
            return;
        }
        mManagerImpl.detachScanSurface(surfaceContainerId);
    }

    private static boolean checkScanResult(String result) {
        if (!isInit()) {
            return false;
        }
        return true;
    }

    private static class GateListener implements IScanAnalyzeListener {
        private IScanAnalyzeListener listener;

        public GateListener(IScanAnalyzeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAnalyzeSuccess(Bitmap bitmap, String result) {
            if (checkScanResult(result)) {
                if (listener != null) {
                    listener.onAnalyzeSuccess(bitmap, result);
                }
            } else {
                if (listener != null && !listener.onAnalyzeFailed()) {
                    Toast.makeText(AppUtils.getApplicationContext(), R.string.base_scan_forbidden_result, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onAnalyzeFailed() {
            if (listener != null) {
                return listener.onAnalyzeFailed();
            }
            return false;
        }
    }
}
