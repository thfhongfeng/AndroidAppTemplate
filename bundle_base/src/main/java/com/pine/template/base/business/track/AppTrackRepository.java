package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.business.db.DbRoomDatabase;
import com.pine.template.base.business.track.dao.AppTrackDao;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.tool.util.LogUtils;

import java.util.List;

public class AppTrackRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackRepository mInstance = null;
    private Context mApplicationContext;

    private AppTrackDao appTrackDao;

    private int MAX_COUNT = 100000;
    private int count;
    private int MAX_BUSINESS_RECORD = 10000;
    private int businessRecordCount;

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
            mApplicationContext = application;
            MAX_COUNT = ConfigSwitcherServer.getConfigInt(BuildConfigKey.CONFIG_APP_TRACK_MAX_COUNT, 100000);
            roomDatabase = DbRoomDatabase.getINSTANCE(application);
            appTrackDao = roomDatabase.appTrackDao();
            count = appTrackDao.getCount();
            businessRecordCount = appTrackDao.getCountByModuleTag(TrackModuleTag.MODULE_OPERATION_RECORD);
            LogUtils.d(TAG, "new AppTrackRepository, data count:" + count);
        }
    }

    public List<AppTrack> queryTrackList(@NonNull String moduleTag, String actionName,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList moduleTag: " + moduleTag + ", actionName: " + actionName +
                    ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryListByPage(moduleTag, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryListByPage(moduleTag, actionName, startIndex, pageSize);
                }
            } else {
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryAllListByModule(moduleTag);
                } else {
                    retList = appTrackDao.queryAllListByModule(moduleTag, actionName);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(@NonNull String moduleTag, List<String> actionNames,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList moduleTag: " + moduleTag + ", actionNames: " + actionNames +
                    ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryListByPage(moduleTag, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryListByPage(moduleTag, actionNames, startIndex, pageSize);
                }
            } else {
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryAllListByModule(moduleTag);
                } else {
                    retList = appTrackDao.queryAllListByModule(moduleTag, actionNames);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(@NonNull List<String> moduleTags, int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList moduleTags: " + moduleTags +
                    ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                retList = appTrackDao.queryListByPage(moduleTags, startIndex, pageSize);
            } else {
                retList = appTrackDao.queryAllListByModules(moduleTags);
            }
            return retList;
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
            LogUtils.d(TAG, "insert appTrack: " + appTrack + ",cur count:" + count);
            AppTrack deleteTrack = null;
            boolean isBusinessRecord = TextUtils.equals(appTrack.getModuleTag(), TrackModuleTag.MODULE_OPERATION_RECORD);
            if (isBusinessRecord) {
                String moduleTag = TrackModuleTag.MODULE_OPERATION_RECORD;
                if (businessRecordCount > MAX_BUSINESS_RECORD) {
                    int curCount = businessRecordCount;
                    LogUtils.d(TAG, "insert business record ,cur count > " + MAX_BUSINESS_RECORD + " ,delete half old data");
                    appTrackDao.deleteOldDataByModuleTag(moduleTag, MAX_BUSINESS_RECORD / 2);
                    businessRecordCount = appTrackDao.getCountByModuleTag(moduleTag);
                    deleteTrack = getDeleteOldDataTrack(moduleTag, curCount - businessRecordCount);
                }

            } else if (count > MAX_COUNT) {
                int curCount = count;
                LogUtils.d(TAG, "insert appTrack ,cur count > " + MAX_COUNT + " ,delete half old data");
                appTrackDao.deleteOldData(MAX_COUNT / 2);
                count = appTrackDao.getCount();
                deleteTrack = getDeleteOldDataTrack("", curCount - count);
            }
            if (deleteTrack != null && appTrackDao.insert(deleteTrack) >= 0) {
                LogUtils.d(TAG, "insert appTrack ,add deleteTrack: " + deleteTrack);
                count++;
            }
            appTrack.setId(0);
            long id = appTrackDao.insert(appTrack);
            if (id >= 0) {
                appTrack.setId(id);
                count++;
                if (isBusinessRecord) {
                    businessRecordCount++;
                }
                return true;
            }
            return false;
        }
    }

    public boolean delete(@NonNull List<AppTrack> appTrackList) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete appTrackList: " + appTrackList);
            if (appTrackDao.delete(appTrackList) >= 0) {
                count -= appTrackList.size();
                return true;
            }
            return false;
        }
    }

    public static void reset() {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            mInstance = null;
        }
    }

    private AppTrack getDeleteOldDataTrack(String moduleTag, int count) {
        AppTrack appTrack = new AppTrack();
        appTrack.setModuleTag(TrackModuleTag.MODULE_BASE);
        appTrack.setTrackType(9999);
        appTrack.setCurClass(AppTrackRepository.class.getSimpleName());
        appTrack.setActionName("db_exceeded_del");
        appTrack.setActionData("delete " + (TextUtils.isEmpty(moduleTag) ? "" : moduleTag + " ")
                + count + " tracks for db exceeded");
        appTrack.setActionInStamp(System.currentTimeMillis());
        appTrack.setActionOutStamp(System.currentTimeMillis());
        AppTrackUtils.setBaseInfoAndIp(mApplicationContext, appTrack);
        return appTrack;
    }
}
