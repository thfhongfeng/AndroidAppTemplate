package com.pine.base.component.image_loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.pine.base.R;
import com.pine.base.component.image_loader.glide.GlideImageLoaderManager;
import com.pine.config.BuildConfig;

import java.io.File;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/10/12
 */

public class ImageLoaderManager {
    private static volatile ImageLoaderManager mInstance;
    private static volatile IImageLoaderManager mImpl;
    private final static int DEFAULT_ERROR_RES_ID = R.mipmap.base_ic_default_error_image;
    private final static int DEFAULT_LOADING_RES_ID = R.drawable.base_animated_loading;
    private final static int DEFAULT_EMPTY_RES_ID = R.mipmap.base_ic_default_image;

    private ImageLoaderManager() {

    }

    public synchronized static ImageLoaderManager getInstance() {
        if (mInstance == null) {
            mInstance = new ImageLoaderManager();
            switch (BuildConfig.APP_THIRD_IMAGE_LOADER_PROVIDER) {
                case "glide":
                    mImpl = GlideImageLoaderManager.getInstance();
                default:
                    mImpl = GlideImageLoaderManager.getInstance();
            }
            mImpl.initConfig(DEFAULT_ERROR_RES_ID, DEFAULT_LOADING_RES_ID, DEFAULT_EMPTY_RES_ID);
        }
        return mInstance;
    }

    /**
     * 设置下载请求监听
     *
     * @param listener
     * @return
     */
    public ImageLoaderManager downloadListener(IImageDownloadListener listener) {
        mImpl.downloadListener(listener);
        return mInstance;
    }

    /**
     * 加载本地Res图片
     *
     * @param context   Context
     * @param res       加载的图
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @DrawableRes int res,
                          @NonNull ImageView imageView) {
        loadImage(context, res, DEFAULT_ERROR_RES_ID, DEFAULT_LOADING_RES_ID, DEFAULT_EMPTY_RES_ID, imageView);
    }

    /**
     * 加载本地Res图片
     *
     * @param context     Context
     * @param res         加载的图
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @DrawableRes int res, @DrawableRes int placeholder,
                          @NonNull ImageView imageView) {
        loadImage(context, res, DEFAULT_ERROR_RES_ID, placeholder, DEFAULT_EMPTY_RES_ID, imageView);
    }

    /**
     * 加载本地Res图片
     *
     * @param context     Context
     * @param res         加载的图
     * @param error       加载错误时的默认图（0：使用初始化时配置的图片，无效id：无图）
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param empty       无图片的占位图（0：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @DrawableRes int res, @DrawableRes int error,
                          @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView) {
        mImpl.loadImage(context, res, error, placeholder, empty, imageView);
    }

    /**
     * 加载本地Res图片
     *
     * @param context     Context
     * @param res         加载的图
     * @param error       加载错误时的默认图（null：使用初始化时配置的图片）
     * @param placeholder 加载中的占位图（null：使用初始化时配置的图片）
     * @param empty       无图片的占位图（null：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @DrawableRes int res, Drawable error,
                          Drawable placeholder, Drawable empty, @NonNull ImageView imageView) {
        mImpl.loadImage(context, res, error, placeholder, empty, imageView);
    }

    /**
     * 加载网络图片
     *
     * @param context   Context
     * @param url       图片地址
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        loadImage(context, url, DEFAULT_ERROR_RES_ID, DEFAULT_LOADING_RES_ID, DEFAULT_EMPTY_RES_ID, imageView, null);
    }

    /**
     * 加载网络图片
     *
     * @param context     Context
     * @param url         图片地址
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url,
                          @DrawableRes int placeholder, @NonNull ImageView imageView) {
        loadImage(context, url, DEFAULT_ERROR_RES_ID, placeholder, DEFAULT_EMPTY_RES_ID, imageView, null);
    }

    /**
     * 加载网络图片
     *
     * @param context     Context
     * @param url         图片地址
     * @param error       加载错误时的默认图（0：使用初始化时配置的图片，无效id：无图）
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param empty       无图片的占位图（0：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url, @DrawableRes int error,
                          @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView) {
        loadImage(context, url, error, placeholder, empty, imageView, null);
    }

    /**
     * 加载网络图片
     *
     * @param context     Context
     * @param url         图片地址
     * @param error       加载错误时的默认图（null：使用初始化时配置的图片）
     * @param placeholder 加载中的占位图（null：使用初始化时配置的图片）
     * @param empty       无图片的占位图（null：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url, Drawable error,
                          Drawable placeholder, Drawable empty, @NonNull ImageView imageView) {
        loadImage(context, url, error, placeholder, empty, imageView, null);
    }

    /**
     * 加载网络图片
     *
     * @param context   Context
     * @param url       图片地址
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView,
                          ImageCacheStrategy cacheStrategy) {
        loadImage(context, url, DEFAULT_ERROR_RES_ID, DEFAULT_LOADING_RES_ID, DEFAULT_EMPTY_RES_ID, imageView, cacheStrategy);
    }

    /**
     * 加载网络图片
     *
     * @param context     Context
     * @param url         图片地址
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull String url, @DrawableRes int placeholder,
                          @NonNull ImageView imageView, ImageCacheStrategy cacheStrategy) {
        loadImage(context, url, DEFAULT_ERROR_RES_ID, placeholder, DEFAULT_EMPTY_RES_ID, imageView, cacheStrategy);
    }

    /**
     * 加载网络图片
     *
     * @param context       Context
     * @param url           图片地址
     * @param error         加载错误时的默认图（0：使用初始化时配置的图片，无效id：无图）
     * @param placeholder   加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param empty         无图片的占位图（0：使用初始化时配置的图片）
     * @param imageView
     * @param cacheStrategy ImageCacheStrategy
     */
    public void loadImage(@NonNull Context context, @NonNull String url, @DrawableRes int error,
                          @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView,
                          ImageCacheStrategy cacheStrategy) {
        mImpl.loadImage(context, url, error, placeholder, empty, imageView, cacheStrategy);
    }

    /**
     * 加载网络图片
     *
     * @param context       Context
     * @param url           图片地址
     * @param error         加载错误时的默认图（null：使用初始化时配置的图片）
     * @param placeholder   加载中的占位图（null：使用初始化时配置的图片）
     * @param empty         无图片的占位图（null：使用初始化时配置的图片）
     * @param imageView
     * @param cacheStrategy ImageCacheStrategy
     */
    void loadImage(@NonNull Context context, @NonNull String url, Drawable error,
                   Drawable placeholder, Drawable empty, @NonNull ImageView imageView,
                   ImageCacheStrategy cacheStrategy) {
        mImpl.loadImage(context, url, error, placeholder, empty, imageView, cacheStrategy);
    }

    /**
     * 加载本地File图片
     *
     * @param context   Context
     * @param file      图片地址
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull File file,
                          @NonNull ImageView imageView) {
        loadImage(context, file, DEFAULT_ERROR_RES_ID, DEFAULT_LOADING_RES_ID, DEFAULT_EMPTY_RES_ID, imageView);
    }

    /**
     * 加载本地File图片
     *
     * @param context     Context
     * @param file        图片地址
     * @param error       加载错误时的默认图（0：使用初始化时配置的图片，无效id：无图）
     * @param placeholder 加载中的占位图（0：使用初始化时配置的图片，无效id：无图）
     * @param empty       无图片的占位图（0：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull File file, @DrawableRes int error,
                          @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView) {
        mImpl.loadImage(context, file, error, placeholder, empty, imageView);
    }

    /**
     * 加载本地File图片
     *
     * @param context     Context
     * @param file        图片地址
     * @param error       加载错误时的默认图（null：使用初始化时配置的图片）
     * @param placeholder 加载中的占位图（null：使用初始化时配置的图片）
     * @param empty       无图片的占位图（null：使用初始化时配置的图片）
     * @param imageView
     */
    public void loadImage(@NonNull Context context, @NonNull File file, Drawable error,
                          Drawable placeholder, Drawable empty, @NonNull ImageView imageView) {
        mImpl.loadImage(context, file, error, placeholder, empty, imageView);
    }

    /**
     * 清空缓存
     *
     * @param context Context
     */
    public void clearDiskCache(@NonNull Context context) {
        mImpl.clearDiskCache(context);
    }

    /**
     * 清空内存
     *
     * @param context Context
     */
    public void clearMemory(@NonNull Context context) {
        mImpl.clearMemory(context);
    }
}
