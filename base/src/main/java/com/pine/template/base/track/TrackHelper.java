package com.pine.template.base.track;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.template.base.db.entity.AppTrack;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class TrackHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private Map<String, Boolean> mOpenModuleMap = new HashMap<>();

    private Context mContext;
    private String mUploadUrl;
    private String mPackageName;
    private String mOSName;

    public TrackHelper(@NonNull Context application, @NonNull String uploadUrl,
                       @NonNull Map<String, Boolean> openModuleMap) {
        mContext = application;
        mUploadUrl = uploadUrl;
        mOpenModuleMap = openModuleMap;
        mPackageName = AppUtils.getApplication().getPackageName();
        mOSName = android.os.Build.BRAND.trim().toUpperCase();
    }

    /**
     * @param appTrack
     */
    public void track(@NonNull AppTrack appTrack) {
        LogUtils.d(TAG, "track appTrack :" + appTrack);
        AppTrackRepository.getInstance(mContext).insert(appTrack);
    }

    /**
     * @param trackModuleTagList list for TrackModuleTag, see {@link TrackModuleTag}, null for all
     * @param startTimeStamp     include
     * @param endTimeStamp       exclude
     */
    public void uploadTrack(List<String> trackModuleTagList, long startTimeStamp, long endTimeStamp) {
        LogUtils.d(TAG, "uploadTrack moduleTags :" + trackModuleTagList +
                ",startTimeStamp:" + startTimeStamp + ",endTimeStamp:" + endTimeStamp);
        new UploadSyncTask().execute(trackModuleTagList, startTimeStamp, endTimeStamp);
    }

    private SimpleDateFormat mUploadFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private class UploadSyncTask extends AsyncTask<Object, Integer, JSONObject> {
        List<AppTrack> uploadList = new ArrayList<>();

        @Override
        protected JSONObject doInBackground(Object... arrayLists) {
            long startTimeStamp = (long) arrayLists[1];
            long endTimeStamp = (long) arrayLists[2];
            List<String> moduleList = new ArrayList<>();
            if (arrayLists[0] == null) {
                synchronized (mOpenModuleMap) {
                    Iterator<Map.Entry<String, Boolean>> iterator = mOpenModuleMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Boolean> entry = iterator.next();
                        if (entry.getValue()) {
                            moduleList.add(entry.getKey());
                        }
                    }
                }
            } else {
                moduleList = (List<String>) arrayLists[0];
            }
            if (moduleList.size() < 1) {
                return null;
            }
            HashMap<String, List<AppTrack>> trackListMap = new HashMap<>();
            for (String module : moduleList) {
                List<AppTrack> list = AppTrackRepository.getInstance(mContext).queryTrackListByModulesAndTime(module, startTimeStamp, endTimeStamp);
                if (list != null && list.size() > 0) {
                    trackListMap.put(module, list);
                }
                LogUtils.d(TAG, "the size of " + module + " module track is " + (list == null ? "0" : list.size()));
            }
            if (trackListMap.size() < 1) {
                LogUtils.d(TAG, "there is no track for last startup of the app");
                return null;
            }
            JSONObject argsObj = new JSONObject();
            try {
                List<AppTrack> list = trackListMap.get(TrackModuleTag.MODULE_DEFAULT);
                argsObj.put("default", new Gson().toJson(list, new TypeToken<List<AppTrack>>() {
                }.getType()));
                uploadList.addAll(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return argsObj;
        }

        protected void onPostExecute(final JSONObject result) {
            if (result != null && result.length() > 0) {
                HashMap<String, String> params = new HashMap<>();
                LogUtils.d(TAG, "mUploadUrl:" + mUploadUrl + ", size:" + uploadList.size() + ", args:" + result.toString());
                params.put("args", result.toString());
                AppTrackRepository.getInstance(mContext).delete(uploadList);
            }
        }
    }
}
