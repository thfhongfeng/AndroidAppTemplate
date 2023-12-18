package com.pine.template.base.business.track;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrackHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private final int MSG_UPLOAD_TRACK = 0;

    private HandlerThread mWorkThread;
    private WorkHandler mWorkHandler;
    // 单位秒
    private int mLoopInterval = 60;

    private Map<String, Boolean> mOpenModuleMap = new HashMap<>();

    private Context mContext;
    private String mUploadUrl;

    public TrackHelper(@NonNull Context application, @NonNull String uploadUrl,
                       @NonNull Map<String, Boolean> openModuleMap) {
        mContext = application;
        mUploadUrl = uploadUrl;
        mOpenModuleMap = openModuleMap;

        mWorkThread = new HandlerThread("app track");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
    }

    private class WorkHandler extends Handler {
        WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD_TRACK:
                    long timeStamp = System.currentTimeMillis();
                    updateTrackTask(-1, -1);
                    mWorkHandler.removeMessages(MSG_UPLOAD_TRACK);
                    long updateSum = System.currentTimeMillis() - timeStamp;
                    long delay = mLoopInterval * 1000 > updateSum ? mLoopInterval * 1000 - updateSum : 0;
                    mWorkHandler.sendEmptyMessageDelayed(MSG_UPLOAD_TRACK, delay);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param appTrack
     */
    public void track(@NonNull final AppTrack appTrack, boolean immediately) {
        if (immediately) {
            AppTrackRepository.getInstance(mContext).insert(appTrack);
        } else {
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    AppTrackRepository.getInstance(mContext).insert(appTrack);
                }
            });
        }
    }

    public List<AppTrack> getTrackList(@NonNull String moduleTag, String actionName,
                                       int pageNo, int pageSize) {
        if (TextUtils.isEmpty(moduleTag)) {
            return null;
        }
        return AppTrackRepository.getInstance(mContext).queryTrackList(moduleTag, actionName, pageNo, pageSize);
    }

    public List<AppTrack> getTrackList(@NonNull String moduleTag, List<String> actionNames,
                                       int pageNo, int pageSize) {
        if (TextUtils.isEmpty(moduleTag)) {
            return null;
        }
        return AppTrackRepository.getInstance(mContext).queryTrackList(moduleTag, actionNames, pageNo, pageSize);
    }

    public List<AppTrack> getTrackList(@NonNull List<String> moduleTagList,
                                       int pageNo, int pageSize) {
        if (moduleTagList == null || moduleTagList.size() < 1) {
            return null;
        }
        return AppTrackRepository.getInstance(mContext).queryTrackList(moduleTagList, pageNo, pageSize);
    }

    /**
     * @param startTimeStamp include
     * @param endTimeStamp   exclude
     */
    public void uploadTrack(final long startTimeStamp, final long endTimeStamp) {
        LogUtils.d(TAG, "uploadTrack startTimeStamp:" + startTimeStamp
                + ",endTimeStamp:" + endTimeStamp);
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                updateTrackTask(startTimeStamp, endTimeStamp);
            }
        });
    }

    /**
     * @param delay
     */
    public void startLoopUploadTrack(long delay) {
        stopLoopUploadTrack();
        mWorkHandler.sendEmptyMessageDelayed(MSG_UPLOAD_TRACK, delay > 0 ? delay : 0);
    }

    public void stopLoopUploadTrack() {
        mWorkHandler.removeMessages(MSG_UPLOAD_TRACK);
    }

    public void clearAllWaitTrackTask() {
        mWorkHandler.removeCallbacksAndMessages(null);
    }

    private volatile boolean mUploading = false;

    private void updateTrackTask(long startTimeStamp, long endTimeStamp) {
        if (mUploading) {
            LogUtils.d(TAG, "update track, task already processing, return");
            return;
        }
        final List<AppTrack> uploadList = new ArrayList<>();
        List<String> moduleList = new ArrayList<>();
        synchronized (mOpenModuleMap) {
            Iterator<Map.Entry<String, Boolean>> iterator = mOpenModuleMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> entry = iterator.next();
                if (entry.getValue()) {
                    moduleList.add(entry.getKey());
                }
            }
        }
        if (moduleList.size() < 1) {
            LogUtils.d(TAG, "update track, there is no open module");
            return;
        }
        HashMap<String, List<AppTrack>> trackListMap = new HashMap<>();
        for (String module : moduleList) {
            List<AppTrack> list = AppTrackRepository.getInstance(mContext)
                    .queryTrackListByModulesAndTime(module, startTimeStamp, endTimeStamp);
            if (list != null && list.size() > 0) {
                trackListMap.put(module, list);
            }
        }
        if (trackListMap.size() < 1) {
            LogUtils.d(TAG, "update track, there is no track for update");
            return;
        }
        JSONObject dataObj = new JSONObject();
        JSONObject trackObj = new JSONObject();
        Gson gson = new Gson();
        try {
            Set<String> keys = trackListMap.keySet();
            for (String key : keys) {
                List<AppTrack> list = trackListMap.get(key);
                if (list == null || list.size() < 1) {
                    continue;
                }
                JSONArray trackArr = new JSONArray();
                for (AppTrack track : list) {
                    JSONObject obj = new JSONObject(gson.toJson(track));
                    trackArr.put(obj);
                }
                trackObj.put(key, trackArr);
                uploadList.addAll(list);
            }
            dataObj.put("trackData", trackObj);
            dataObj.put("deviceInfo", new JSONObject(new Gson().toJson(AppTrackUtils.getTrackHeader(mContext))));
        } catch (JSONException e) {
            LogUtils.d(TAG, "update track, data format incorrect");
            return;
        }
        if (dataObj != null && dataObj.length() > 0) {
            HashMap<String, String> params = new HashMap<>();
            params.put("data", dataObj.toString());
            RequestBean requestBean = new RequestBean(mUploadUrl, 9999, params);
            mUploading = true;
            RequestManager.setJsonRequest(requestBean, new JsonCallback() {
                @Override
                public void onResponse(int what, JSONObject jsonObject, Response response) {
                    if (9999 == what) {
                        if (jsonObject.optBoolean("success")
                                && (jsonObject.optInt("code") == 200 || jsonObject.optInt("code") == 0)) {
                            AppTrackRepository.getInstance(mContext).delete(uploadList);
                        }
                    }
                    mUploading = false;
                }

                @Override
                public boolean onFail(int what, Exception e, Response response) {
                    mUploading = false;
                    return true;
                }

                @Override
                public void onCancel(int what) {
                    mUploading = false;
                }
            });
        }
    }
}
