package com.pine.base.component.image_loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by tanghongfeng on 2018/10/12
 */

public interface IImageLoaderManager {

    /**
     * 初始化基本配置项
     *
     * @param errorImageResId 加载错误时的默认图
     * @return
     */
    void initConfig(@NonNull int errorImageResId);

    /**
     * 初始化基本配置项
     *
     * @param errorImageResId       加载错误时的默认图
     * @param placeholderImageResId 加载中的占位图
     * @param emptyImageResId       无图片的占位图
     * @return
     */
    void initConfig(@NonNull int errorImageResId, @NonNull int placeholderImageResId, @NonNull int emptyImageResId);

    /**
     * 设置下载请求监听
     *
     * @param listener
     * @return
     */
    void downloadListener(IImageDownloadListener listener);

    /**
     * 加载本地Res图片
     *
     * @param context     Context
     * @param res         加载的图
     * @param error       加载错误时的默认图
     * @param placeholder 加载中的占位图
     * @param empty       无图片的占位图
     * @param imageView
     */
    void loadImage(@NonNull Context context, @DrawableRes int res, @DrawableRes int error,
                   @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView);

    /**
     * 加载本地Res图片
     *
     * @param context     Context
     * @param res         加载的图
     * @param error       加载错误时的默认图
     * @param placeholder 加载中的占位图
     * @param empty       无图片的占位图
     * @param imageView
     */
    void loadImage(@NonNull Context context, @DrawableRes int res, Drawable error,
                   Drawable placeholder, Drawable empty, @NonNull ImageView imageView);

    /**
     * 加载网络图片
     *
     * @param context       Context
     * @param url           图片地址
     * @param error         加载错误时的默认图
     * @param placeholder   加载中的占位图
     * @param empty         无图片的占位图
     * @param imageView
     * @param cacheStrategy ImageCacheStrategy
     */
    void loadImage(@NonNull Context context, @NonNull String url, @DrawableRes int error,
                   @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView,
                   ImageCacheStrategy cacheStrategy);

    /**
     * 加载网络图片
     *
     * @param context       Context
     * @param url           图片地址
     * @param error         加载错误时的默认图
     * @param placeholder   加载中的占位图
     * @param empty         无图片的占位图
     * @param imageView
     * @param cacheStrategy ImageCacheStrategy
     */
    void loadImage(@NonNull Context context, @NonNull String url, Drawable error,
                   Drawable placeholder, Drawable empty, @NonNull ImageView imageView,
                   ImageCacheStrategy cacheStrategy);

    /**
     * 加载本地File图片
     *
     * @param context     Context
     * @param file        图片地址
     * @param error       加载错误时的默认图
     * @param placeholder 加载中的占位图
     * @param empty       无图片的占位图
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull File file, @DrawableRes int error,
                   @DrawableRes int placeholder, @DrawableRes int empty, @NonNull ImageView imageView);

    /**
     * 加载本地File图片
     *
     * @param context     Context
     * @param file        图片地址
     * @param error       加载错误时的默认图
     * @param placeholder 加载中的占位图
     * @param empty       无图片的占位图
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull File file, Drawable error,
                   Drawable placeholder, Drawable empty, @NonNull ImageView imageView);

    /**
     * 清空缓存
     *
     * @param context Context
     */
    void clearDiskCache(@NonNull Context context);

    /**
     * 清空内存
     *
     * @param context Context
     */
    void clearMemory(@NonNull Context context);
}
