package com.pine.template.face.model;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.matcher.FaceMatcher;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.db.repository.PersonRepository;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.util.LogUtils;

import java.util.List;

/**
 * 用于本地人脸识别，但是数据是在远端的情况
 */
public class PersonDataWorker {
    private final String TAG = this.getClass().getSimpleName();

    private static volatile PersonDataWorker instance;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private PersonDataWorker() {
        mWorkThread = new HandlerThread("FaceMatchWorker");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    public synchronized static PersonDataWorker getInstance() {
        if (instance == null) {
            instance = new PersonDataWorker();
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// identityCheck //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    private volatile int pageNo = 1;
    private volatile int pageSize = 1000;
    private double mMaxDegree = -1.0f;
    private float mFaceConfidence = 80f;
    private PersonEntity mMatchEntity;
    private boolean mMatchAll;

    public synchronized void identityCheck(@NonNull final IModelAsyncResponse<PersonEntity> callback) {
        identityCheck(FaceUrlConstants.IDENTITY_FACE_PATH(), callback);
    }

    public synchronized void identityCheck(String faceFilePath,
                                           @NonNull final IModelAsyncResponse<PersonEntity> callback) {
        Handler handler = new Handler();
        pageNo = 1;
        mMaxDegree = -1.0f;
        mMatchEntity = null;
        mMatchAll = true;

        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                processCheck(handler, faceFilePath, callback);
            }
        });
    }

    private void processCheck(Handler handler, String faceFilePath,
                              @NonNull final IModelAsyncResponse<PersonEntity> callback) {
        if (!FaceMatcher.getInstance().prepareCheckCompare(faceFilePath)) {
            LogUtils.d(TAG, "prepareCheckCompare fail");
            onMatchFailToHandler(callback, handler, new Exception());
            return;
        }
        faceMatch(faceFilePath, PersonRepository.getInstance().queryByPage(pageNo++, pageSize), handler, callback);
    }

    private void faceMatch(String faceFilePath,
                           List<PersonEntity> entities, Handler handler,
                           @NonNull final IModelAsyncResponse<PersonEntity> callback) {
        if (entities != null) {
            for (PersonEntity entity : entities) {
                double degree = FaceMatcher.getInstance().doCheckCompare(entity.getFaceFeatureBytes());
                LogUtils.d(TAG, "faceMatched degree:" + degree + ", matched entity:" + entity);
                if (!mMatchAll && FaceMatcher.getInstance().passConfidence(degree, mFaceConfidence)) {
                    LogUtils.d(TAG, "faceMatched for not match all mode degree:" + degree
                            + ", matched entity:" + entity);
                    mMaxDegree = degree;
                    mMatchEntity = entity;
                    onFaceMatched(callback, handler, mMatchEntity, mMaxDegree);
                    return;
                }
                if (FaceMatcher.getInstance().maxSimilarChange(mMaxDegree, degree)) {
                    mMaxDegree = degree;
                    mMatchEntity = entity;
                }
            }
        }
        if (entities == null || entities.size() < pageSize) {
            onFaceMatched(callback, handler, mMatchEntity, mMaxDegree);
        } else {
            processCheck(handler, faceFilePath, callback);
        }
    }

    private void onFaceMatched(IModelAsyncResponse<PersonEntity> callback,
                               Handler handler, PersonEntity personEntity, double degree) {
        LogUtils.d(TAG, "onFaceMatched maxDegree:" + degree + ", go judging pass confidence next flow");
        if (personEntity == null || !FaceMatcher.getInstance().passConfidence(degree, mFaceConfidence)) {
            onMatchFailToHandler(callback, handler, new Exception());
            return;
        }
        onMatchResponseToHandler(callback, handler, personEntity);
    }

    private <T> void onMatchResponseToHandler(IModelAsyncResponse<T> callback, Handler handler, T t) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FaceMatcher.getInstance().completeCheckCompare();
                callback.onResponse(t);
            }
        });
    }

    private <T> void onMatchFailToHandler(IModelAsyncResponse<T> callback, Handler handler, Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FaceMatcher.getInstance().completeCheckCompare();
                callback.onFail(e);
            }
        });
    }
}
