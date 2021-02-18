package com.pine.template.base.component.image_loader.glide.loader;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Synthetic;
import com.pine.template.base.component.image_loader.IImageDownloadListener;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.RequestMethod;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.BitmapCallback;
import com.pine.tool.util.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Created by tanghongfeng on 2018/11/21
 */

public class HttpDataFetcher implements DataFetcher<InputStream> {
    /**
     * Returned when a connection error prevented us from receiving an http error.
     */
    private static final int INVALID_STATUS_CODE = -1;
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private final GlideUrl glideUrl;
    private final int timeout;

    private HttpURLConnection urlConnection;
    private InputStream stream;
    private volatile boolean isCancelled;
    private IImageDownloadListener listener;

    @VisibleForTesting
    HttpDataFetcher(GlideUrl glideUrl, int timeout,
                    IImageDownloadListener listener) {
        this.glideUrl = glideUrl;
        this.timeout = timeout;
        this.listener = listener;
    }

    // Referencing constants is less clear than a simple static method.
    private static boolean isHttpOk(int statusCode) {
        return statusCode / 100 == 2;
    }

    // Referencing constants is less clear than a simple static method.
    private static boolean isHttpRedirect(int statusCode) {
        return statusCode / 100 == 3;
    }

    @Override
    public void loadData(@NonNull Priority priority,
                         final @NonNull DataCallback<? super InputStream> callback) {
        Log.d(TAG, "GlideLoader: loadData");
        long startTime = LogTime.getLogTime();
        try {
            if (listener != null) {
                listener.onRequest(glideUrl.toURL(), glideUrl.getHeaders());
            }
            RequestBean requestBean = new RequestBean(glideUrl.toStringUrl(), hashCode(), new HashMap<String, String>());
            requestBean.setRequestMethod(RequestMethod.GET);
            RequestManager.setBitmapRequest(requestBean, new BitmapCallback() {
                @Override
                public void onResponse(int what, Bitmap bitmap, Response response) {
                    if (listener != null) {
                        listener.onResponse(response.getResponseCode(), response.getHeaders());
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                    callback.onDataReady(inputStream);
                }

                @Override
                public boolean onFail(int what, Exception e, Response response) {
                    if (listener != null) {
                        listener.onFail(response.getResponseCode(), "");
                    }
                    callback.onLoadFailed(e);
                    return false;
                }

                @Override
                public void onCancel(int what) {

                }
            });

        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Failed to load data for url", e);
            }
            callback.onLoadFailed(e);
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Finished http url fetcher fetch in " + LogTime.getElapsedMillis(startTime));
            }
        }
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        urlConnection = null;
    }

    @Override
    public void cancel() {
        // TODO: we should consider disconnecting the url connection here, but we can't do so
        // directly because cancel is often called on the main thread.
        isCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    private static class DefaultHttpUrlConnectionFactory implements HttpDataFetcher.HttpUrlConnectionFactory {

        @Synthetic
        DefaultHttpUrlConnectionFactory() {
        }

        @Override
        public HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }
}
