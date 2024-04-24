package com.pine.template.face.db;

import android.os.Handler;
import android.os.HandlerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.db.repository.PersonRepository;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;

import java.util.List;

public class LocalWorker {
    private final String TAG = this.getClass().getSimpleName();

    private static volatile LocalWorker instance;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    public LocalWorker() {
        mWorkThread = new HandlerThread("LocalWorker");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    public synchronized static LocalWorker getInstance() {
        if (instance == null) {
            instance = new LocalWorker();
        }
        return instance;
    }

    private Gson sGson = new GsonBuilder().disableHtmlEscaping().create();

    public void requestPersonListData(IModelAsyncResponse<List<PersonEntity>> callback) {
        List<PersonEntity> list = PersonRepository.getInstance().queryAllList();
        if (callback != null) {
            callback.onResponse(list);
        }
    }

    public void requestAddPerson(PersonEntity entity,
                                 IModelAsyncResponse<PersonEntity> callback) {
        if (entity != null && PersonRepository.getInstance().insert(entity)) {
            if (callback != null) {
                callback.onResponse(entity);
            }
        } else {
            if (callback != null) {
                callback.onFail(new MessageException(""));
            }
        }
    }

    public void requestUpdatePerson(PersonEntity entity,
                                    IModelAsyncResponse<PersonEntity> callback) {
        if (entity != null && PersonRepository.getInstance().update(entity)) {
            if (callback != null) {
                callback.onResponse(entity);
            }
        } else {
            if (callback != null) {
                callback.onFail(new MessageException(""));
            }
        }
    }

    public void requestDeletePerson(PersonEntity entity,
                                    IModelAsyncResponse<Boolean> callback) {
        if (entity != null && PersonRepository.getInstance().delete(entity)) {
            if (callback != null) {
                callback.onResponse(true);
            }
        } else {
            if (callback != null) {
                callback.onFail(new MessageException(""));
            }
        }
    }

    public void requestDeletePersonList(List<PersonEntity> list,
                                        IModelAsyncResponse<Boolean> callback) {
        if (list != null && PersonRepository.getInstance().delete(list)) {
            if (callback != null) {
                callback.onResponse(true);
            }
        } else {
            if (callback != null) {
                callback.onFail(new MessageException(""));
            }
        }
    }
}
