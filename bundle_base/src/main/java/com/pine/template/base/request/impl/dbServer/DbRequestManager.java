package com.pine.template.base.request.impl.dbServer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.tool.request.DownloadRequestBean;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IResponseListener;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;
import com.pine.tool.request.UploadRequestBean;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.TypeConvertUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private HashMap<String, String> mSessionIdMap = new HashMap<>();

    private HashMap<Object, UploadRequestData> mCancelableUploadMap = new HashMap<>();
    private HashMap<Object, DownloadRequestData> mCancelableDownloadMap = new HashMap<>();

    // 请求工作线程
    private HandlerThread mHandlerThread;
    // 请求工作线程的Handler
    private Handler mThreadHandler;
    // Bitmap请求工作线程
    private HandlerThread mBitmapHandlerThread;
    // Bitmap请求工作线程的Handler
    private Handler mBitmapThreadHandler;
    // 下载线程
    private HandlerThread mDownloadHandlerThread;
    // 下载线程的Handler
    private Handler mDownloadThreadHandler;
    // 上传线程
    private HandlerThread mUploadHandlerThread;
    // 上传线程的Handler
    private Handler mUploadThreadHandler;
    // 主线程Handler
    private Handler mMainHandler;

    private DbRequestManager() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("DbRequestManager");
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper());
        }
        if (mBitmapHandlerThread == null) {
            mBitmapHandlerThread = new HandlerThread("DbRequestManager_Bitmap");
            mBitmapHandlerThread.start();
            mBitmapThreadHandler = new Handler(mBitmapHandlerThread.getLooper());
        }
        if (mDownloadHandlerThread == null) {
            mDownloadHandlerThread = new HandlerThread("DbRequestManager_Download");
            mDownloadHandlerThread.start();
            mDownloadThreadHandler = new Handler(mDownloadHandlerThread.getLooper());
        }
        if (mUploadHandlerThread == null) {
            mUploadHandlerThread = new HandlerThread("DbRequestManager_Upload");
            mUploadHandlerThread.start();
            mUploadThreadHandler = new Handler(mUploadHandlerThread.getLooper());
        }
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static DbRequestManager getInstance(IDbRequestServer requestServer) {
        if (mInstance == null) {
            synchronized (DbRequestManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use request: db");
                    mInstance = new DbRequestManager();
                    mRequestServer = requestServer;
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(@NonNull Context context, HashMap<String, String> head) {
        mApplicationContext = context;
        if (head != null) {
            mHeaderParams = head;
        }
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private void dispatchStartResponse(final @NonNull RequestBean requestBean,
                                       final @NonNull IResponseListener.OnResponseListener listener,
                                       boolean requestFromMainThread) {
        if (requestFromMainThread) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onStart(requestBean.getWhat());
                }
            });
        } else {
            listener.onStart(requestBean.getWhat());
        }
    }

    private void dispatchResponse(final @NonNull RequestBean requestBean, final Response response,
                                  final @NonNull IResponseListener.OnResponseListener listener,
                                  boolean requestFromMainThread) {
        if (requestFromMainThread) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (response.isSucceed()) {
                        listener.onSucceed(requestBean.getWhat(), response);
                    } else {
                        listener.onFailed(requestBean.getWhat(), response);
                    }
                    listener.onFinish(requestBean.getWhat());
                }
            });
        } else {
            if (response.isSucceed()) {
                listener.onSucceed(requestBean.getWhat(), response);
            } else {
                listener.onFailed(requestBean.getWhat(), response);
            }
            listener.onFinish(requestBean.getWhat());
        }
    }

    @Override
    public void setBytesRequest(final @NonNull RequestBean requestBean,
                                final @NonNull IResponseListener.OnResponseListener listener) {
        if (isMainThread()) {
            mThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    setBytesRequestWorker(requestBean, listener, true);
                }
            });
        } else {
            setBytesRequestWorker(requestBean, listener, false);
        }
    }

    private void setBytesRequestWorker(@NonNull RequestBean requestBean,
                                       @NonNull IResponseListener.OnResponseListener listener,
                                       boolean requestFromMainThread) {
        dispatchStartResponse(requestBean, listener, requestFromMainThread);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IDbRequestServer.requestBeanKey, requestBean);
        bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);

        Response response = mRequestServer.request(bundle);
        if (response == null) {
            response = new Response();
            response.setSucceed(false);
            response.setData(null);
            response.setException(new Exception("remote error"));
            dispatchResponse(requestBean, response, listener, requestFromMainThread);
            return;
        }
        response.setData(TypeConvertUtils.toByteArray(response.getData()));

        mCookies = response.getCookies();
        dispatchResponse(requestBean, response, listener, requestFromMainThread);
    }

    @Override
    public void setStringRequest(final @NonNull RequestBean requestBean,
                                 final @NonNull IResponseListener.OnResponseListener listener) {
        if (isMainThread()) {
            mThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    setStringRequestWorker(requestBean, listener, true);
                }
            });
        } else {
            setStringRequestWorker(requestBean, listener, false);
        }
    }

    private void setStringRequestWorker(@NonNull RequestBean requestBean,
                                        @NonNull IResponseListener.OnResponseListener listener,
                                        boolean requestFromMainThread) {
        dispatchStartResponse(requestBean, listener, requestFromMainThread);

        Bundle bundle = new Bundle();
        bundle.putSerializable(IDbRequestServer.requestBeanKey, requestBean);
        bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
        Response response = mRequestServer.request(bundle);
        if (response == null) {
            response = new Response();
            response.setSucceed(false);
            response.setData(new JSONObject().toString());
            response.setException(new Exception("remote error"));
            dispatchResponse(requestBean, response, listener, requestFromMainThread);
            return;
        }
        response.setData(response.getData().toString());

        mCookies = response.getCookies();
        dispatchResponse(requestBean, response, listener, requestFromMainThread);
    }

    @Override
    public void setBitmapRequest(final @NonNull RequestBean requestBean,
                                 final @NonNull IResponseListener.OnResponseListener listener) {
        if (isMainThread()) {
            mBitmapThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    setBitmapRequestWorker(requestBean, listener, true);
                }
            });
        } else {
            setBitmapRequestWorker(requestBean, listener, false);
        }
    }

    private void setBitmapRequestWorker(@NonNull RequestBean requestBean,
                                        @NonNull IResponseListener.OnResponseListener listener,
                                        boolean requestFromMainThread) {
        dispatchStartResponse(requestBean, listener, requestFromMainThread);

        Bundle bundle = new Bundle();
        bundle.putSerializable(IDbRequestServer.requestBeanKey, requestBean);
        bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
        Response response = mRequestServer.request(bundle);
        if (response == null) {
            response = new Response();
            response.setSucceed(false);
            response.setData(null);
            response.setException(new Exception("remote error"));
            dispatchResponse(requestBean, response, listener, requestFromMainThread);
            return;
        }
        Bitmap bitmap = TypeConvertUtils.toBitmap((byte[]) response.getData());
        if (bitmap != null) {
            response.setData(bitmap);
        }

        mCookies = response.getCookies();
        dispatchResponse(requestBean, response, listener, requestFromMainThread);
    }

    private void dispatchDownloadErrorResponse(final @NonNull RequestBean requestBean, final Exception exception,
                                               final @NonNull IResponseListener.OnDownloadListener listener,
                                               boolean requestFromMainThread) {
        if (requestFromMainThread) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadError(requestBean.getWhat(), exception);
                    listener.onFinish(requestBean.getWhat(), "");
                }
            });
        } else {
            listener.onDownloadError(requestBean.getWhat(), exception);
            listener.onFinish(requestBean.getWhat(), "");
        }
    }

    @Override
    public void setDownloadRequest(final @NonNull DownloadRequestBean requestBean,
                                   final @NonNull IResponseListener.OnDownloadListener listener) {
        if (isMainThread()) {
            sendMessage(requestBean, mDownloadThreadHandler, new Runnable() {
                @Override
                public void run() {
                    setDownloadRequestWorker(requestBean, listener, true);
                }
            });
        } else {
            setDownloadRequestWorker(requestBean, listener, false);
        }
    }

    private void setDownloadRequestWorker(final @NonNull DownloadRequestBean requestBean,
                                          final @NonNull IResponseListener.OnDownloadListener listener,
                                          final boolean requestFromMainThread) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IDbRequestServer.requestBeanKey, requestBean);
        bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
        Response response = mRequestServer.request(bundle);
        if (response == null) {
            dispatchDownloadErrorResponse(requestBean, new Exception("remote error"), listener, requestFromMainThread);
            return;
        }
        DownloadRequestData requestData = new DownloadRequestData();
        requestData.handler = mDownloadThreadHandler;
        requestData.listener = listener;
        requestData.what = requestBean.getWhat();
        mCancelableDownloadMap.put(requestBean.getSign(), requestData);
        sendMessage(requestBean, mDownloadThreadHandler, new Runnable() {
            int progress = 0;
            boolean isStart = true;

            @Override
            public void run() {
                if (isStart) {
                    if (requestFromMainThread) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onStart(requestBean.getWhat(), false, 10000, response.getHeaders(), 100000);
                            }
                        });
                    } else {
                        listener.onStart(requestBean.getWhat(), false, 10000, response.getHeaders(), 100000);
                    }
                    isStart = false;
                }
                progress = progress > 100 ? 100 : progress;
                if (requestFromMainThread) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgress(requestBean.getWhat(), progress, 100000, 6000);
                        }
                    });
                } else {
                    listener.onProgress(requestBean.getWhat(), progress, 100000, 6000);
                }
                if (new Random().nextInt(20) < 1) {
                    dispatchDownloadErrorResponse(requestBean, new Exception("simulation downloadError"), listener, requestFromMainThread);
                    mCancelableDownloadMap.remove(requestBean.getSign());
                } else {
                    if (this.progress < 100) {
                        this.progress = this.progress + 6;
                        sendDelayMessage(requestBean, mDownloadThreadHandler, this, 500);
                    } else {
                        sendDelayMessage(requestBean, mDownloadThreadHandler, new Runnable() {
                            @Override
                            public void run() {
                                if (requestFromMainThread) {
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onFinish(requestBean.getWhat(),
                                                    requestBean.getSaveFolder() + File.pathSeparator + requestBean.getSaveFileName());
                                        }
                                    });
                                } else {
                                    listener.onFinish(requestBean.getWhat(),
                                            requestBean.getSaveFolder() + File.pathSeparator + requestBean.getSaveFileName());
                                }
                                mCancelableDownloadMap.remove(requestBean.getSign());
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    private void dispatchUploadFailResponse(final @NonNull RequestBean requestBean,
                                            final UploadRequestBean.FileBean fileBean, final Exception exception,
                                            final @NonNull IResponseListener.OnUploadListener listener,
                                            boolean requestFromMainThread) {
        if (requestFromMainThread) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(requestBean.getWhat(), fileBean, exception);
                    listener.onFinish(requestBean.getWhat(), null);
                }
            });
        } else {
            listener.onError(requestBean.getWhat(), fileBean, exception);
            listener.onFinish(requestBean.getWhat(), null);
        }
    }

    private HashMap<Integer, Integer> mUploadCountMap = new HashMap<>();

    @Override
    public void setUploadRequest(final @NonNull UploadRequestBean requestBean,
                                 final @NonNull IResponseListener.OnUploadListener processListener,
                                 final @NonNull IResponseListener.OnResponseListener responseListener) {
        if (isMainThread()) {
            sendMessage(requestBean, mUploadThreadHandler, new Runnable() {
                @Override
                public void run() {
                    setUploadRequestWorker(requestBean, processListener, responseListener, true);
                }
            });
        } else {
            setUploadRequestWorker(requestBean, processListener, responseListener, false);
        }
    }

    private void setUploadRequestWorker(final @NonNull UploadRequestBean requestBean,
                                        final @NonNull IResponseListener.OnUploadListener processListener,
                                        final @NonNull IResponseListener.OnResponseListener responseListener,
                                        final boolean requestFromMainThread) {
        List<UploadRequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
        if (fileBeanList == null || fileBeanList.size() < 1) {
            Exception exception = new Exception("file is null");
            Response response = new Response();
            response.setSucceed(false);
            response.setException(exception);
            dispatchUploadFailResponse(requestBean, null, exception, processListener, requestFromMainThread);
            dispatchResponse(requestBean, response, responseListener, requestFromMainThread);
            return;
        }
        boolean isAllSuccess = true;
        final boolean isMultiUpload = TextUtils.isEmpty(requestBean.getUpLoadFileKey());
        mUploadCountMap.put(requestBean.hashCode(), fileBeanList.size());
        HashMap<String, String> cookies = new HashMap<>();
        final List<Object> respDataList = new ArrayList<>();
        dispatchStartResponse(requestBean, responseListener, requestFromMainThread);
        for (final UploadRequestBean.FileBean fileBean : fileBeanList) {
            if (requestFromMainThread) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        processListener.onStart(fileBean.getWhat(), null);
                    }
                });
            } else {
                processListener.onStart(fileBean.getWhat(), null);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(IDbRequestServer.requestBeanKey, requestBean);
            bundle.putSerializable(IDbRequestServer.cookiesKey, mCookies);
            Response r = mRequestServer.request(bundle);
            if (r == null) {
                r = new Response();
                r.setSucceed(false);
                r.setException(new Exception("remote error"));
            } else {
                cookies = r.getCookies();
            }
            final Response response = r;
            UploadRequestData requestData = new UploadRequestData();
            requestData.handler = mUploadThreadHandler;
            requestData.listener = processListener;
            requestData.what = requestBean.getWhat();
            requestData.fileBean = fileBean;
            mCancelableUploadMap.put(requestBean.getSign(), requestData);
            if (response.isSucceed()) {
                isAllSuccess = isAllSuccess && true;
                sendMessage(requestBean, mUploadThreadHandler, new Runnable() {
                    int progress = 0;
                    int interval = new Random().nextInt(10) * 6 + 6;

                    @Override
                    public void run() {
                        progress = progress > 100 ? 100 : progress;
                        if (requestFromMainThread) {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    processListener.onProgress(fileBean.getWhat(), fileBean, progress);
                                }
                            });
                        } else {
                            processListener.onProgress(fileBean.getWhat(), fileBean, progress);
                        }
                        if (this.progress < 100) {
                            this.progress = this.progress + interval;
                            sendDelayMessage(requestBean, mUploadThreadHandler, this, 500);
                        } else {
                            sendDelayMessage(requestBean, mUploadThreadHandler, new Runnable() {
                                @Override
                                public void run() {
                                    if (requestFromMainThread) {
                                        mMainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                processListener.onFinish(fileBean.getWhat(), fileBean);
                                            }
                                        });
                                    } else {
                                        processListener.onFinish(fileBean.getWhat(), fileBean);
                                    }
                                    if (mUploadCountMap.containsKey(requestBean.hashCode())) {
                                        mUploadCountMap.put(requestBean.hashCode(), (mUploadCountMap.get(requestBean.hashCode()) - 1));
                                    }
                                    mCancelableUploadMap.remove(requestBean.getSign());
                                }
                            }, 500);
                        }
                    }
                });
                respDataList.add(response.getData());
            } else {
                isAllSuccess = false;
                sendDelayMessage(requestBean, mUploadThreadHandler, new Runnable() {
                    @Override
                    public void run() {
                        dispatchUploadFailResponse(requestBean, null, response.getException(), processListener, requestFromMainThread);
                        if (mUploadCountMap.containsKey(requestBean.hashCode())) {
                            mUploadCountMap.put(requestBean.hashCode(), (mUploadCountMap.get(requestBean.hashCode()) - 1));
                        }
                        mCancelableUploadMap.remove(requestBean.getSign());
                    }
                }, 1000);
            }
        }
        final HashMap<String, String> finalCookies = cookies;
        if (!isAllSuccess) {
            sendDelayMessage(requestBean, mUploadThreadHandler, new Runnable() {
                @Override
                public void run() {
                    if (!mUploadCountMap.containsKey(requestBean.hashCode()) || mUploadCountMap.get(requestBean.hashCode()) < 1) {
                        mUploadCountMap.remove(requestBean.hashCode());
                        Response response = new Response();
                        response.setSucceed(false);
                        response.setException(new Exception("fail"));
                        dispatchResponse(requestBean, response, responseListener, requestFromMainThread);
                    } else {
                        sendDelayMessage(requestBean, mUploadThreadHandler, this, 500);
                    }
                }
            }, 500);
        } else {
            sendDelayMessage(requestBean, mUploadThreadHandler, new Runnable() {
                @Override
                public void run() {
                    if (!mUploadCountMap.containsKey(requestBean.hashCode()) || mUploadCountMap.get(requestBean.hashCode()) < 1) {
                        mUploadCountMap.remove(requestBean.hashCode());
                        mCookies = finalCookies;
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
                                Response response = new Response();
                                response.setSucceed(false);
                                response.setException(new Exception("fail"));
                                dispatchResponse(requestBean, response, responseListener, requestFromMainThread);
                                return;
                            }
                        } else {
                            successRep.setData(respDataList.get(0));
                        }
                        successRep.setResponseCode(200);
                        successRep.setException(new Exception("fail"));
                        dispatchResponse(requestBean, successRep, responseListener, requestFromMainThread);
                    } else {
                        sendDelayMessage(requestBean, mUploadThreadHandler, this, 500);
                    }
                }
            }, 500);
        }
    }

    private void sendMessage(RequestBean requestBean, Handler handler, Runnable runnable) {
        Message message = Message.obtain(handler, runnable);
        message.obj = requestBean.getSign();
        handler.sendMessage(message);
    }

    private void sendDelayMessage(RequestBean requestBean, Handler handler, Runnable runnable, long delayMillis) {
        Message message = Message.obtain(handler, runnable);
        message.obj = requestBean.getSign();
        handler.sendMessageDelayed(message, delayMillis);
    }

    @Override
    public void cancelBySign(Object sign) {
        if (mCancelableDownloadMap.containsKey(sign)) {
            DownloadRequestData requestData = mCancelableDownloadMap.remove(sign);
            requestData.handler.removeCallbacksAndMessages(sign);
            requestData.listener.onCancel(requestData.what);
        } else if (mCancelableUploadMap.containsKey(sign)) {
            UploadRequestData requestData = mCancelableUploadMap.remove(sign);
            requestData.handler.removeCallbacksAndMessages(sign);
            requestData.listener.onCancel(requestData.what, requestData.fileBean);
        }
    }

    @Override
    public void cancelAll() {
        Iterator<HashMap.Entry<Object, DownloadRequestData>> downloadIterator = mCancelableDownloadMap.entrySet().iterator();
        while (downloadIterator.hasNext()) {
            HashMap.Entry<Object, DownloadRequestData> entry = downloadIterator.next();
            cancelBySign(entry.getKey());
        }
        Iterator<HashMap.Entry<Object, UploadRequestData>> uploadIterator = mCancelableUploadMap.entrySet().iterator();
        while (uploadIterator.hasNext()) {
            HashMap.Entry<Object, UploadRequestData> entry = uploadIterator.next();
            cancelBySign(entry.getKey());
        }
    }

    @Override
    public void clearCookie() {
        mCookies = new HashMap<>();
    }

    @Override
    public Map<String, String> getLastSessionCookie() {
        return mCookies;
    }

    @Override
    public String getSessionId(String sysTag) {
        return mSessionIdMap.get(sysTag);
    }

    @Override
    public void setSessionId(String sysTag, String sessionId) {
        mSessionIdMap.put(sysTag, sessionId);
    }

    public class DownloadRequestData {
        int what;
        Handler handler;
        IResponseListener.OnDownloadListener listener;
    }

    public class UploadRequestData {
        int what;
        Handler handler;
        IResponseListener.OnUploadListener listener;
        UploadRequestBean.FileBean fileBean;
    }
}
