package com.pine.tool.request.impl.database;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IResponseListener;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class DbRequestManager implements IRequestManager {
    private final static String TAG = LogUtils.makeLogTag(DbRequestManager.class);
    private static volatile DbRequestManager mInstance;
    private static Context mApplicationContext;
    private static HashMap<String, String> mHeaderParams = new HashMap<>();
    private static HashMap<String, String> mCookies = new HashMap<>();
    private static IDbRequestServer mRequestServer;
    private String mSessionId;

    private DbRequestManager() {

    }

    public static DbRequestManager getInstance() {
        if (mInstance == null) {
            synchronized (DbRequestManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use request: db");
                    mInstance = new DbRequestManager();
                }
            }
        }
        return mInstance;
    }

    public IRequestManager init(@NonNull Context context, HashMap<String, String> head, IDbRequestServer requestServer) {
        mApplicationContext = context;
        if (head != null) {
            mHeaderParams = head;
        }
        mRequestServer = requestServer;
        return this;
    }

    @Override
    public void setJsonRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnResponseListener listener) {
        DbRequestBean dbRequestBean = toDbRequestBean(requestBean);

        Bundle bundle = new Bundle();
        bundle.putSerializable(IDbRequestServer.requestBeanKey, dbRequestBean);
        bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
        listener.onStart(requestBean.getWhat());
        DbResponse dbResponse = mRequestServer.request(bundle);
        if (dbResponse == null) {
            Response failRsp = new Response();
            failRsp.setSucceed(false);
            failRsp.setData(new JSONObject());
            failRsp.setException(new Exception("remote error"));
            listener.onFailed(requestBean.getWhat(), failRsp);
            return;
        }
        Response response = toResponse(dbResponse);

        mCookies = dbResponse.getCookies();
        if (response.isSucceed()) {
            listener.onSucceed(requestBean.getWhat(), response);
        } else {
            listener.onFailed(requestBean.getWhat(), response);
        }
        listener.onFinish(requestBean.getWhat());
    }

    @Override
    public void setDownloadRequest(final @NonNull RequestBean requestBean,
                                   final @NonNull IResponseListener.OnDownloadListener listener) {
        DbRequestBean dbRequestBean = toDbRequestBean(requestBean);
        // Test code begin
        listener.onStart(requestBean.getWhat(), false, 10000, 100000);
        new Handler().post(new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                progress = progress > 100 ? 100 : progress;
                listener.onProgress(requestBean.getWhat(), progress, 100000, 6000);
                if (new Random().nextInt(20) < 1) {
                    listener.onDownloadError(requestBean.getWhat(), new Exception("simulation downloadError"));
                } else {
                    if (this.progress < 100) {
                        this.progress = this.progress + 6;
                        new Handler().postDelayed(this, 500);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFinish(requestBean.getWhat(), "");
                            }
                        }, 1000);
                    }
                }
            }
        });
        // Test code end
    }

    @Override
    public void setUploadRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnUploadListener processListener,
                                 @NonNull IResponseListener.OnResponseListener responseListener) {
        List<RequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
        responseListener.onStart(requestBean.getWhat());
        if (fileBeanList == null || fileBeanList.size() < 1) {
            Exception exception = new Exception("file is null");
            processListener.onError(requestBean.getWhat(), null, exception);
            Response failRsp = new Response();
            failRsp.setSucceed(false);
            failRsp.setException(exception);
            responseListener.onFailed(requestBean.getWhat(), failRsp);

            processListener.onFinish(requestBean.getWhat(), null);
            responseListener.onFinish(requestBean.getWhat());
            return;
        }
        boolean isAllSuccess = true;
        boolean isMultiUpload = true;
        HashMap<String, String> cookies = new HashMap<>();
        List<Object> respDataList = new ArrayList<>();
        for (RequestBean.FileBean fileBean : fileBeanList) {
            DbRequestBean bean = toDbRequestBean(requestBean);
            ArrayList<DbRequestBean.FileBean> list = new ArrayList<>();
            list.add(new DbRequestBean.FileBean(fileBean.getFileKey(), fileBean.getFileName(), fileBean.getFile(), fileBean.getPosition()));
            bean.setUploadFileList(list);
            processListener.onStart(bean.getWhat(), null);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IDbRequestServer.requestBeanKey, bean);
            bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
            DbResponse dbResponse = mRequestServer.request(bundle);
            if (dbResponse == null) {
                dbResponse = new DbResponse();
                dbResponse.setSucceed(false);
                dbResponse.setException(new Exception("remote error"));
            } else {
                cookies = dbResponse.getCookies();
            }
            Response response = toResponse(dbResponse);
            if (response.isSucceed()) {
                isAllSuccess = isAllSuccess && true;
                processListener.onProgress(bean.getWhat(), fileBean, 100);
                processListener.onFinish(bean.getWhat(), fileBean);
                isMultiUpload = dbResponse.isMultiUpload();
                respDataList.add(response.getData());
            } else {
                isAllSuccess = false;
                processListener.onError(bean.getWhat(), fileBean, response.getException());
            }
        }
        if (!isAllSuccess) {
            Response failRsp = new Response();
            failRsp.setSucceed(false);
            failRsp.setException(new Exception("fail"));
            responseListener.onFailed(requestBean.getWhat(), failRsp);
        } else {
            mCookies = cookies;
            Response successRep = new Response();
            successRep.setSucceed(true);
            successRep.setCookies(mCookies);
            if (isMultiUpload) {
                String paths = "";
                String names = "";
                try {
                    JSONObject allData = new JSONObject(respDataList.get(0).toString());
                    for (Object obj : respDataList) {
                        JSONObject entity = new JSONObject(obj.toString());
                        JSONObject data = entity.optJSONObject("data");
                        paths += data.opt("fileUrls") + ",";
                        names += data.opt("fileNames") + ",";
                    }
                    allData.optJSONObject("data").put("fileUrls", paths.substring(0, paths.length() - 1));
                    allData.optJSONObject("data").put("fileNames", names.substring(0, paths.length() - 1));
                    successRep.setData(allData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Response failRsp = new Response();
                    failRsp.setSucceed(false);
                    failRsp.setException(new Exception("fail"));
                    responseListener.onFailed(requestBean.getWhat(), failRsp);
                    responseListener.onFinish(requestBean.getWhat());
                    return;
                }
            } else {
                successRep.setData(respDataList.get(0));
            }
            successRep.setResponseCode(200);
            successRep.setException(new Exception("fail"));
            responseListener.onSucceed(requestBean.getWhat(), successRep);
        }
        responseListener.onFinish(requestBean.getWhat());
    }

    @Override
    public void cancelBySign(Object sign) {

    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void addGlobalSessionCookie(HashMap<String, String> headerMap) {
        if (headerMap == null) {
            return;
        }
        mHeaderParams.putAll(headerMap);
    }

    @Override
    public void removeGlobalSessionCookie(List<String> keyList) {
        if (keyList == null || keyList.size() < 1) {
            return;
        }
        for (String key : keyList) {
            mHeaderParams.remove(key);
        }
    }

    @Override
    public String getSessionId() {
        return mSessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    @Override
    public void clearCookie() {
        mCookies = new HashMap<>();
    }

    @Override
    public Map<String, String> getSessionCookie() {
        return mCookies;
    }

    private DbRequestBean toDbRequestBean(@NonNull RequestBean requestBean) {
        DbRequestBean dbRequestBean = new DbRequestBean(requestBean.getWhat());
        dbRequestBean.setUrl(requestBean.getUrl());
        dbRequestBean.setParams(requestBean.getParams());
        dbRequestBean.setModuleTag(requestBean.getModuleTag());
        dbRequestBean.setNeedLogin(requestBean.isNeedLogin());
        dbRequestBean.setActionType(requestBean.getActionType());

        dbRequestBean.setSaveFolder(requestBean.getSaveFolder());
        dbRequestBean.setSaveFileName(requestBean.getSaveFileName());
        dbRequestBean.setContinue(requestBean.isContinue());
        dbRequestBean.setDeleteOld(requestBean.isDeleteOld());

        dbRequestBean.setUpLoadFileKey(requestBean.getUpLoadFileKey());
        List<RequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
        if (fileBeanList != null && requestBean.getUploadFileList().size() > 0) {
            List<DbRequestBean.FileBean> dbFileBeanList = new ArrayList<>();
            for (RequestBean.FileBean entity : fileBeanList) {
                DbRequestBean.FileBean fileBean = new DbRequestBean.FileBean(entity.getFileKey(),
                        entity.getFileName(), entity.getFile(), entity.getPosition());
                dbFileBeanList.add(fileBean);
            }
            dbRequestBean.setUploadFileList(dbFileBeanList);
        }
        return dbRequestBean;
    }

    private Response toResponse(@NonNull DbResponse dbResponse) {
        Response response = new Response();
        response.setSucceed(dbResponse.isSucceed());
        response.setTag(dbResponse.getTag());
        response.setResponseCode(dbResponse.getResponseCode());
        response.setData(dbResponse.getData());
        response.setCookies(dbResponse.getCookies());
        response.setException(dbResponse.getException());
        return response;
    }
}
