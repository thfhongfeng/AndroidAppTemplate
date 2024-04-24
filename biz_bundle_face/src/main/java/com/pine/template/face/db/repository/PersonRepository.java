package com.pine.template.face.db.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.template.face.FaceApplication;
import com.pine.template.face.db.FaceDatabase;
import com.pine.template.face.db.dao.PersonDao;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.tool.util.LogUtils;

import java.util.List;

public class PersonRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile PersonRepository mInstance = null;

    private PersonDao personDao;

    public static PersonRepository getInstance() {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            if (mInstance == null) {
                mInstance = new PersonRepository(FaceApplication.mApplication);
            }
            return mInstance;
        }
    }

    private FaceDatabase roomDatabase;

    private PersonRepository(Context application) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "new");
            roomDatabase = FaceDatabase.getINSTANCE(application);
            personDao = roomDatabase.personDao();
        }
    }

    public List<PersonEntity> queryAllList() {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryAllList");
            List<PersonEntity> retList = personDao.queryAllList();
            LogUtils.d(TAG, "queryAllList ret list size:" + (retList == null ? 0 : retList.size()));
            return retList;
        }
    }

    public List<PersonEntity> queryByPage(int pageNo, int pageSize) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryByPage pageNo:" + pageNo + ", pageSize:" + pageSize);
            int startIndex = (pageNo - 1) * pageSize;
            List<PersonEntity> retList = personDao.queryByPage(startIndex, pageSize);
            LogUtils.d(TAG, "queryByPage ret list size:" + (retList == null ? 0 : retList.size()));
            return retList;
        }
    }

    public boolean insert(@NonNull PersonEntity entity) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "insert entity: " + entity);
            entity.setId(0);
            long id = personDao.insert(entity);
            boolean success = id >= 0;
            if (success) {
                entity.setId(id);
            }
            LogUtils.d(TAG, "insert success: " + success);
            return success;
        }
    }

    public boolean insertList(@NonNull List<PersonEntity> entities) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "insertList entities size: " + entities.size());
            List<Long> countList = personDao.insertList(entities);
            boolean success = countList != null && countList.size() >= 0;
            if (success) {
                List<PersonEntity> newList = personDao.queryInList(countList);
                if (newList != null) {
                    entities.clear();
                    entities.addAll(newList);
                }
            }
            LogUtils.d(TAG, "insertList success: " + success);
            return success;
        }
    }

    public boolean update(@NonNull PersonEntity entity) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "update entity: " + entity);
            boolean success = personDao.update(entity) >= 0;
            LogUtils.d(TAG, "update success: " + success);
            return success;
        }
    }

    public boolean delete(@NonNull PersonEntity entity) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete entity: " + entity);
            boolean success = personDao.delete(entity) >= 0;
            LogUtils.d(TAG, "delete success: " + success);
            return success;
        }
    }

    public boolean delete(@NonNull List<PersonEntity> list) {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete list: " + list);
            boolean success = personDao.delete(list) >= 0;
            LogUtils.d(TAG, "delete list success: " + success);
            return success;
        }
    }

    public boolean deleteAll() {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete all");
            boolean success = personDao.deleteAll() >= 0;
            LogUtils.d(TAG, "delete all success: " + success);
            return success;
        }
    }

    public static void reset() {
        synchronized (FaceDatabase.DB_SYNC_LOCK) {
            mInstance = null;
        }
    }
}
