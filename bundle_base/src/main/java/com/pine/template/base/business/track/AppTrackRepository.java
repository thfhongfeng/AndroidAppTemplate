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
            roomDatabase = DbRoomDatabase.getINSTANCE(application);
            appTrackDao = roomDatabase.appTrackDao();
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
     * @param moduleTags
     * @param actionNames
     * @param endTime     exclude
     * @param count
     * @return
     */
    public List<AppTrack> queryTrackListByEndTime(@NonNull List<String> moduleTags,
                                                  @NonNull List<String> actionNames, long endTime, int count) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByCount moduleTags: " + moduleTags + ", actionNames: " + actionNames +
                    ", endTime: " + endTime + ", count: " + count);
            List<AppTrack> retList = null;
            retList = appTrackDao.queryTrackListByEndTime(moduleTags, actionNames, endTime, count);
            return retList;
        }
    }

    /**
     * @param moduleTags
     * @param actionNames
     * @param startTime   exclude
     * @param count
     * @return
     */
    public List<AppTrack> queryTrackListByStartTime(@NonNull List<String> moduleTags,
                                                    @NonNull List<String> actionNames, long startTime, int count) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "queryTrackListByCount moduleTags: " + moduleTags + ", actionNames: " + actionNames +
                    ", startTime: " + startTime + ", count: " + count);
            List<AppTrack> retList = null;
            retList = appTrackDao.queryTrackListByStartTime(moduleTags, actionNames, startTime, count);
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
                appTrack.setModuleTag(TrackDefaultBuilder.MODULE_DEFAULT);
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
        int count = appTrackDao.getCount();
        int maxStore = AppTrackManager.getInstance().getMaxStoreCount();
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            for (String moduleTag : keys) {
                List<AppTrack> list = map.get(moduleTag);
                LogUtils.d(TAG, "insert appTrack list: " + list + ",cur count:" + count);
                List<AppTrack> deleteTrackList = new ArrayList<>();
                if (count + newCount > maxStore) {
                    int curCount = count;
                    LogUtils.d(TAG, "insert appTrack, cur count > " + maxStore + ", delete half old data");
                    appTrackDao.deleteOldData(maxStore / 4);
                    count = appTrackDao.getCount();
                    deleteTrackList.add(TrackDefaultBuilder.getDeleteOldDataTrackForDbOut(mApplicationContext, "", curCount - count));
                }
                int moduleCount = appTrackDao.getCountByModuleTag(moduleTag);
                int maxModuleCount = AppTrackManager.getInstance().getModuleMaxCount(moduleTag);
                if (moduleCount + newCount > maxModuleCount) {
                    int curCount = moduleCount;
                    LogUtils.d(TAG, "insert module record, cur count > " + maxModuleCount + ", delete half old data");
                    appTrackDao.deleteOldDataByModuleTag(moduleTag, maxModuleCount / 4);
                    deleteTrackList.add(TrackDefaultBuilder.getDeleteOldDataTrackForDbOut(mApplicationContext, moduleTag, curCount - moduleCount));
                }
                try {
                    if (deleteTrackList.size() > 0) {
                        Long[] ids = appTrackDao.insertAll(deleteTrackList);
                        int size = ids == null ? 0 : ids.length;
                        LogUtils.d(TAG, "insert appTrack, add deleteTrack size : " + size);
                    }
                    Long[] ids = appTrackDao.insertAll(list);
                    if (ids != null && ids.length == list.size()) {
                        for (int i = 0; i < ids.length; i++) {
                            list.get(i).setId(ids[i]);
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

    public int deleteForStorageOut(int delCount, int minLeft) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "deleteForStorageOut delCount: " + delCount + ", minLeft:" + minLeft);
            int curCount = appTrackDao.getCount();
            if (curCount < minLeft) {
                return 0;
            }
            delCount = delCount < 0 ? 1000 : delCount;
            delCount = curCount - minLeft > delCount ? delCount : curCount - minLeft;
            int del = appTrackDao.deleteOldData(delCount);
            if (del > 0) {
                appTrackDao.insert(TrackDefaultBuilder.getDeleteOldDataTrackForStorageOut(mApplicationContext, delCount));
                return del;
            }
            return 0;
        }
    }

    public boolean delete(@NonNull List<AppTrack> appTrackList) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "delete appTrackList: " + appTrackList);
            if (appTrackDao.delete(appTrackList) >= 0) {
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
