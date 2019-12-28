package com.pine.base.component.image_loader.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pine.base.component.image_loader.IImageDownloadListener;
import com.pine.base.component.image_loader.IImageLoaderManager;
import com.pine.base.component.image_loader.ImageCacheStrategy;
import com.pine.base.component.image_loader.glide.loader.HttpRequestLoader;
import com.pine.tool.util.LogUtils;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/10/11
 */

public class GlideImageLoaderManager implements IImageLoaderManager {
    private final static String TAG = LogUtils.makeLogTag(GlideImageLoaderManager.class);
    private static volatile IImageLoaderManager mInstance;
    private RequestOptions mDefaultOption = new RequestOptions();
    private int mEmptyImageResId;

    private GlideImageLoaderManager() {
    }

    public static IImageLoaderManager getInstance() {
        if (mInstance == null) {
            synchronized (GlideImageLoaderManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use image loader: glide");
                    mInstance = new GlideImageLoaderManager();
                }
            }
        }
        HttpRequestLoader.listener = null;
        return mInstance;
    }

    @Override
    public void initConfig(@NonNull int errorImageResId) {
        mDefaultOption.error(errorImageResId)    // 加载错误时的默认图
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    @Override
    public void initConfig(@NonNull int errorImageResId, @NonNull int placeholderImageResId,
                           @NonNull int emptyImageResId) {
        mDefaultOption.error(errorImageResId)    // 加载错误时的默认图
                .placeholder(placeholderImageResId)   // 加载中的占位图
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        mEmptyImageResId = emptyImageResId;
    }

    @Override
    public void downloadListener(IImageDownloadListener listener) {
        HttpRequestLoader.listener = listener;
    }

    @SuppressLint("ResourceType")
    @Override
    public void loadImage(@NonNull Context context, int res, int error, int placeholder, int empty,
                          @NonNull ImageView imageView) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (error != 0) {
            options.error(error);
        }
        if (placeholder != 0) {
            options.placeholder(placeholder);
        }
        Glide.with(context)
                .load(res == 0 ? (empty == 0 ? mEmptyImageResId : empty) : res)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadImage(@NonNull Context context, int res, Drawable error,
                          Drawable placeholder, Drawable empty, @NonNull ImageView imageView) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (error != null) {
            options.error(error);
        }
        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        Glide.with(context)
                .load(res == 0 ? (empty == null ? mEmptyImageResId : empty) : res)
                .apply(options)
                .into(imageView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, int error,
                          int placeholder, int empty, @NonNull ImageView imageView, ImageCacheStrategy cacheStrategy) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (error != 0) {
            options.error(error);
        }
        if (placeholder != 0) {
            options.placeholder(placeholder);
        }
        if (cacheStrategy != null) {
            switch (cacheStrategy) {
                case NONE:
                    options.diskCacheStrategy(DiskCacheStrategy.NONE);
                    break;
                case DATA:
                    options.diskCacheStrategy(DiskCacheStrategy.DATA);
                    break;
                case RESOURCE:
                    options.diskCacheStrategy(DiskCacheStrategy.DATA);
                    break;
                case ALL:
                    options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;
                case AUTOMATIC:
                    options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    break;
            }
        }
        if (TextUtils.isEmpty(url)) {

        }
        Glide.with(context)
                .load(TextUtils.isEmpty(url) ? (empty == 0 ? mEmptyImageResId : empty) : url)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, Drawable error,
                          Drawable placeholder, Drawable empty, @NonNull ImageView imageView, ImageCacheStrategy cacheStrategy) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (error != null) {
            options.error(error);
        }
        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        if (cacheStrategy != null) {
            switch (cacheStrategy) {
                case NONE:
                    options.diskCacheStrategy(DiskCacheStrategy.NONE);
                    break;
                case DATA:
                    options.diskCacheStrategy(DiskCacheStrategy.DATA);
                    break;
                case RESOURCE:
                    options.diskCacheStrategy(DiskCacheStrategy.DATA);
                    break;
                case ALL:
                    options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;
                case AUTOMATIC:
                    options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    break;
            }
        }
        Glide.with(context)
                .load(TextUtils.isEmpty(url) ? (empty == null ? mEmptyImageResId : empty) : url)
                .apply(options)
                .into(imageView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void loadImage(@NonNull Context context, @NonNull File file, int error,
                          int placeholder, int empty, @NonNull ImageView imageView) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (error != 0) {
            options.error(error);
        }
        if (placeholder != 0) {
            options.placeholder(placeholder);
        }
        Glide.with(context)
                .load(file == null ? (empty == 0 ? mEmptyImageResId : empty) : file)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadImage(@NonNull Context context, @NonNull File file,
                          Drawable error, Drawable placeholder, Drawable empty, @NonNull ImageView imageView) {
        RequestOptions options = mDefaultOption.clone()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (error != null) {
            options.error(error);
        }
        if (placeholder != null) {
            options.placeholder(placeholder);
        }
        Glide.with(context)
                .load(file == null ? (empty == null ? mEmptyImageResId : empty) : file)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void clearDiskCache(@NonNull Context context) {
        Glide.get(context).clearDiskCache();
    }

    @Override
    public void clearMemory(@NonNull Context context) {
        Glide.get(context).clearMemory();
    }
}
