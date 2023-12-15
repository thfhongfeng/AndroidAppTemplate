package com.pine.tool.camera;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public interface ICameraCallback {
    interface ICameraSetListener {
        boolean onParamSet(@NonNull CameraSurfaceParams params);
    }

    interface ICameraInitListener {
        void onCameraInit(boolean success);

        void onCameraInitProcessing();
    }

    interface PreviewCallback {
        void onPreviewFrame(byte[] data);
    }

    abstract class TakePicListener {
        public boolean restartPreview;

        public abstract void onPictureTaken(Bitmap bitmap);

        public abstract void onFail();
    }

    interface IRecordCallback {
        void onRecordPrepare();

        void onRecordStart();

        void onRecordComplete(String recordFilePath);

        void onRecordFail();

        void onRecordRelease();
    }
}
