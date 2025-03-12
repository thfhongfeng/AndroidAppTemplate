package com.pine.template.face.db;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pine.app.lib.face.matcher.FaceMatcher;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.db.repository.PersonRepository;
import com.pine.template.face.utils.DocumentUtils;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.io.File;
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

    public void requestPersonListData(IAsyncResponse<List<PersonEntity>> callback) {
        List<PersonEntity> list = PersonRepository.getInstance().queryAllList();
        if (callback != null) {
            callback.onResponse(list);
        }
    }

    public void requestPersonListData(int pageNo, int pageSize, IAsyncResponse<List<PersonEntity>> callback) {
        List<PersonEntity> list = PersonRepository.getInstance().queryByPage(pageNo, pageSize);
        if (callback != null) {
            callback.onResponse(list);
        }
    }

    public PersonEntity checkNameExist(String name) {
        PersonEntity exist = PersonRepository.getInstance().queryNameExist(name);
        return exist;
    }

    public void requestSavePerson(boolean newAdd, PersonEntity entity,
                                  IAsyncResponse<PersonEntity> callback) {
        if (newAdd) {
            requestAddPerson(entity, callback);
        } else {
            requestUpdatePerson(entity, callback);
        }
    }

    public void requestAddPerson(PersonEntity entity,
                                 IAsyncResponse<PersonEntity> callback) {
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
                                    IAsyncResponse<PersonEntity> callback) {
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
                                    IAsyncResponse<Boolean> callback) {
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
                                        IAsyncResponse<Boolean> callback) {
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

    public void requestClearPersonList(IAsyncResponse<Boolean> callback) {
        if (PersonRepository.getInstance().deleteAll()) {
            File dir = new File(FaceUrlConstants.PERSON_DB_FACE_DIR());
            if (dir.exists()) {
                dir.delete();
            }
            if (callback != null) {
                callback.onResponse(true);
            }
        } else {
            if (callback != null) {
                callback.onFail(new MessageException(""));
            }
        }
    }

    public void requestImportPersonList(Context context, Uri uri, IAsyncResponse<Boolean> callback) {
        Handler handler = new Handler();
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                DocumentFile dir = DocumentFile.fromTreeUri(context, uri);
                DocumentFile[] fileList = dir.listFiles();
                if (fileList != null) {
                    for (DocumentFile file : fileList) {
                        if (file.isFile() && file.getType() != null && file.getType().startsWith("image/")) {
                            String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                            if (PersonRepository.getInstance().queryNameExist(fileName) != null) {
                                continue;
                            }
                            String dbFilePath = FaceUrlConstants.PERSON_DB_FACE_PATH(file.getName());
                            File dbFile = new File(dbFilePath);
                            if (DocumentUtils.copyFile(context, file.getUri(), dbFile)) {
                                byte[] bytes = FaceMatcher.getInstance().toFaceFeatureBytes(dbFilePath);
                                if (bytes != null && bytes.length > 0) {
                                    PersonEntity personEntity = new PersonEntity();
                                    personEntity.setName(fileName);
                                    personEntity.setFacePath(dbFilePath);
                                    personEntity.setFaceFeatureBytes(bytes);
                                    PersonRepository.getInstance().insert(personEntity);
                                } else if (dbFile.exists()) {
                                    dbFile.delete();
                                }
                            }
                        }
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResponse(true);
                        }
                    }
                });
            }
        });
    }
}
