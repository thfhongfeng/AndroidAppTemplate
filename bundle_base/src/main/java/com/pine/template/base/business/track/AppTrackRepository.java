package com.pine.template.base.business.track;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.business.db.DbRoomDatabase;
import com.pine.template.base.business.track.dao.AppTrackDao;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AppTrackRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AppTrackRepository mInstance = null;
    private Context mApplicationContext;

    private AppTrackDao appTrackDao;

    private int MAX_COUNT;
    private int count;

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
            MAX_COUNT = AppTrackUtils.getMaxStoreCount();
            roomDatabase = DbRoomDatabase.getINSTANCE(application);
            appTrackDao = roomDatabase.appTrackDao();
            count = appTrackDao.getCount();
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
                    retList = appTrackDao.queryPageListByModule(moduleTag, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(moduleTag, actionName, startIndex, pageSize);
                }
            } else {
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryAllListByModule(moduleTag);
                } else {
                    retList = appTrackDao.queryAllList(moduleTag, actionName);
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
                    retList = appTrackDao.queryPageListByModule(moduleTag, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(moduleTag, actionNames, startIndex, pageSize);
                }
            } else {
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryAllListByModule(moduleTag);
                } else {
                    retList = appTrackDao.queryAllList(moduleTag, actionNames);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(@NonNull List<String> moduleTags, String actionName,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList moduleTags: " + moduleTags + ", actionName: " + actionName +
                    ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryPageListByModules(moduleTags, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(moduleTags, actionName, startIndex, pageSize);
                }
            } else {
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryAllListByModules(moduleTags);
                } else {
                    retList = appTrackDao.queryAllList(moduleTags, actionName);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(@NonNull List<String> moduleTags, List<String> actionNames,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList moduleTags: " + moduleTags + ", actionNames: " + actionNames +
                    ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryPageListByModules(moduleTags, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(moduleTags, actionNames, startIndex, pageSize);
                }
            } else {
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryAllListByModules(moduleTags);
                } else {
                    retList = appTrackDao.queryAllList(moduleTags, actionNames);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(int trackType, @NonNull List<String> moduleTags, String actionName,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList trackType: " + trackType + ", moduleTags: " + moduleTags +
                    ", actionName: " + actionName + ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryPageListByModules(trackType, moduleTags, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(trackType, moduleTags, actionName, startIndex, pageSize);
                }
            } else {
                if (TextUtils.isEmpty(actionName)) {
                    retList = appTrackDao.queryAllListByModules(trackType, moduleTags);
                } else {
                    retList = appTrackDao.queryAllList(trackType, moduleTags, actionName);
                }
            }
            return retList;
        }
    }

    public List<AppTrack> queryTrackList(int trackType, @NonNull List<String> moduleTags, List<String> actionNames,
                                         int pageNo, int pageSize) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackList trackType: " + trackType + ", moduleTags: " + moduleTags +
                    ", actionNames: " + actionNames + ", pageNo: " + pageNo + ", pageSize: " + pageSize);
            List<AppTrack> retList = null;
            if (pageSize > 0 && pageNo > 0) {
                int startIndex = (pageNo - 1) * pageSize;
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryPageListByModules(trackType, moduleTags, startIndex, pageSize);
                } else {
                    retList = appTrackDao.queryPageList(trackType, moduleTags, actionNames, startIndex, pageSize);
                }
            } else {
                if (actionNames == null || actionNames.size() < 1) {
                    retList = appTrackDao.queryAllListByModules(trackType, moduleTags);
                } else {
                    retList = appTrackDao.queryAllList(trackType, moduleTags, actionNames);
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
                retList = appTrackDao.queryPageListByModules(moduleTags, startIndex, pageSize);
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
     * @param moduleTags
     * @param actionNames
     * @param startTime   include
     * @param endTime     exclude
     * @return
     */
    public List<AppTrack> queryTrackListByTime(@NonNull List<String> moduleTags,
                                               @NonNull List<String> actionNames, long startTime, long endTime) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByTime moduleTags: " + moduleTags + ", actionNames: " + actionNames +
                    ", startTime: " + startTime + ", endTime: " + endTime);
            List<AppTrack> retList = null;
            if (startTime > 0 && endTime > 0) {
                retList = appTrackDao.queryListByTime(moduleTags, actionNames, startTime, endTime);
            } else if (startTime > 0) {
                retList = appTrackDao.queryListByStartTime(moduleTags, actionNames, startTime);
            } else if (endTime > 0) {
                retList = appTrackDao.queryListByEndTime(moduleTags, actionNames, endTime);
            } else {
                retList = appTrackDao.queryAllList(moduleTags, actionNames);
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
        List<AppTrack> list = new ArrayList<>();
        list.add(appTrack);
        return insert(list);
    }

    public boolean insert(@NonNull List<AppTrack> appTrackList) {
        HashMap<String, List<AppTrack>> map = new HashMap<>();
        for (AppTrack appTrack : appTrackList) {
            appTrack.setId(0);
            if (TextUtils.isEmpty(appTrack.getModuleTag())) {
                appTrack.setModuleTag(TrackModuleTag.MODULE_DEFAULT);
            }
            List<AppTrack> list = map.get(appTrack.getModuleTag());
            if (list == null) {
                list = new ArrayList<>();
                map.put(appTrack.getModuleTag(), list);
            }
            list.add(appTrack);
        }
        int newCount = appTrackList.size();
        Set<String> keys = map.keySet();
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            for (String moduleTag : keys) {
                List<AppTrack> list = map.get(moduleTag);
                LogUtils.d(TAG, "insert appTrack list: " + list + ",cur count:" + count);
                List<AppTrack> deleteTrackList = new ArrayList<>();
                if (count + newCount > MAX_COUNT) {
                    int curCount = count;
                    LogUtils.d(TAG, "insert appTrack, cur count > " + MAX_COUNT + ", delete half old data");
                    appTrackDao.deleteOldData(MAX_COUNT / 4);
                    count = appTrackDao.getCount();
                    deleteTrackList.add(TrackModuleTag.getDeleteOldDataTrack(mApplicationContext, "", curCount - count));
                }
                int moduleCount = appTrackDao.getCountByModuleTag(moduleTag);
                int maxModuleCount = AppTrackUtils.getModuleMaxCount(moduleTag);
                if (moduleCount + newCount > maxModuleCount) {
                    int curCount = moduleCount;
                    LogUtils.d(TAG, "insert module record, cur count > " + maxModuleCount + ", delete half old data");
                    appTrackDao.deleteOldDataByModuleTag(moduleTag, maxModuleCount / 4);
                    deleteTrackList.add(TrackModuleTag.getDeleteOldDataTrack(mApplicationContext, moduleTag, curCount - moduleCount));
                }
                try {
                    if (deleteTrackList.size() > 0) {
                        Long[] ids = appTrackDao.insertAll(deleteTrackList);
                        int size = ids == null ? 0 : ids.length;
                        LogUtils.d(TAG, "insert appTrack, add deleteTrack size : " + size);
                        count = count + size;
                    }
                    Long[] ids = appTrackDao.insertAll(list);
                    if (ids != null && ids.length == list.size()) {
                        for (int i = 0; i < ids.length; i++) {
                            list.get(i).setId(ids[i]);
                            count++;
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "insert appTrack, Exception : " + e);
                }
                return false;
            }
        }
        return true;
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
}
