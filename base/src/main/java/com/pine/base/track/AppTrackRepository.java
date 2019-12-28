package com.pine.base.track;

import android.content.Context;

import com.pine.base.db.DbRoomDatabase;
import com.pine.base.db.dao.AppTrackDao;
import com.pine.base.db.entity.AppTrack;
import com.pine.tool.util.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;

public class AppTrackRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackRepository mInstance = null;

    private AppTrackDao appTrackDao;

    public static AppTrackRepository getInstance(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            if (mInstance == null) {
                mInstance = new AppTrackRepository(application);
            }
            return mInstance;
        }
    }

    private DbRoomDatabase roomDatabase;

    private AppTrackRepository(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "new");
            roomDatabase = DbRoomDatabase.getINSTANCE(application);
            appTrackDao = roomDatabase.appTrackDao();
        }
    }

    /**
     * @param startTime include
     * @param endTime   exclude
     * @return
     */
    public List<AppTrack> queryTrackListByTime(long startTime, long endTime) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByTime startTime: " + startTime +
                    ", endTime: " + endTime);
            List<AppTrack> retList = null;
            if (startTime > 0 && endTime > 0) {
                retList = appTrackDao.queryListByTime(startTime, endTime);
            } else if (startTime > 0) {
                retList = appTrackDao.queryListByStartTime(startTime);
            } else if (endTime > 0) {
                retList = appTrackDao.queryListByEndTime(endTime);
            } else {
                retList = appTrackDao.queryAllList();
            }
            return retList;
        }
    }

    /**
     * @param moduleTag
     * @param startTime include
     * @param endTime   exclude
     * @return
     */
    public List<AppTrack> queryTrackListByModulesAndTime(@NonNull String moduleTag, long startTime, long endTime) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByModuleAndTime moduleTag: " + moduleTag +
                    ", startTime: " + startTime + ", endTime: " + endTime);
            List<AppTrack> retList = null;
            if (startTime > 0 && endTime > 0) {
                retList = appTrackDao.queryListByModuleAndTime(moduleTag, startTime, endTime);
            } else if (startTime > 0) {
                retList = appTrackDao.queryListByModuleAndStartTime(moduleTag, startTime);
            } else if (endTime > 0) {
                retList = appTrackDao.queryListByModuleAndEndTime(moduleTag, endTime);
            } else {
                retList = appTrackDao.queryAllListByModule(moduleTag);
            }
            return retList;
        }
    }

    /**
     * @param moduleTags
     * @param startTime  include
     * @param endTime    exclude
     * @return
     */
    public List<AppTrack> queryTrackListByModulesAndTime(@NonNull List<String> moduleTags, long startTime, long endTime) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByModuleAndTime moduleTags: " + moduleTags +
                    ", startTime: " + startTime + ", endTime: " + endTime);
            List<AppTrack> retList = null;
            if (startTime > 0 && endTime > 0) {
                retList = appTrackDao.queryListByModulesAndTime(moduleTags, startTime, endTime);
            } else if (startTime > 0) {
                retList = appTrackDao.queryListByModulesAndStartTime(moduleTags, startTime);
            } else if (endTime > 0) {
                retList = appTrackDao.queryListByModulesAndEndTime(moduleTags, endTime);
            } else {
                retList = appTrackDao.queryAllListByModules(moduleTags);
            }
            return retList;
        }
    }

    public boolean insert(@NonNull AppTrack appTrack) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "insert appTrack: " + appTrack);
            appTrack.setId(0);
            return appTrackDao.insert(appTrack) >= 0;
        }
    }

    public boolean delete(@NonNull List<AppTrack> appTrackList) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete appTrackList: " + appTrackList);
            return appTrackDao.delete(appTrackList) >= 0;
        }
    }

    public static void reset() {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            mInstance = null;
        }
    }
}
