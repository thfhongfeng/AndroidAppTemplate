package com.pine.template.base.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureViewHelper {
    private final String TAG = this.getClass().getSimpleName();

    public static final int ERR_SAVE_NO_DIR = 1;
    public static final int ERR_SAVE_EXCEPTION = 2;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private ExecutorService mExecutor;

    public void init() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void release() {
        mMainHandler.removeCallbacksAndMessages(null);
        mExecutor.shutdownNow();
    }

    // 修改后的完整截图方法
    public void captureAndSaveView(Context context, String fileName, @NonNull View view, ICaptureCallback callback) {
        int visibilityTitle = view.getVisibility();
        // 确保布局可见
        view.setVisibility(View.VISIBLE);
        int count = 0;
        int height = 0;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);

        Bitmap titleViewBt = captureLayout(view);
        if (titleViewBt != null) {
            height += view.getMeasuredHeight();
            bitmapCache.put(String.valueOf(count++), titleViewBt);
        }
        // 保存到缓存目录（无需权限）
        saveBitmapToCache(context, bitmapCache, view.getMeasuredWidth(), height, fileName, new ICaptureCallback() {
            @Override
            public void onSuccess(String path) {
                // 截图后恢复
                view.setVisibility(visibilityTitle);
                if (callback != null) {
                    callback.onSuccess(path);
                }
            }

            @Override
            public void onErr(int errCode, String msg) {
                // 截图后恢复
                view.setVisibility(visibilityTitle);
                if (callback != null) {
                    callback.onErr(errCode, msg);
                }
            }
        });
    }

    // 修改后的完整截图方法
    public void captureAndSaveViews(Context context, String fileName, @NonNull List<View> viewList, ICaptureCallback callback) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);
        HashMap<View, Integer> visibilityMap = new HashMap<>();
        HashMap<View, Drawable> rvBgMap = new HashMap<>();
        int count = 0;
        int width = 0;
        int height = 0;
        for (View view : viewList) {
            visibilityMap.put(view, view.getVisibility());
            // 确保布局可见
            view.setVisibility(View.VISIBLE);
            if (view instanceof RecyclerView) {
                RecyclerView rv = (RecyclerView) view;
                // 临时移除背景（保留子元素内容）
                Drawable rvBg = view.getBackground();
                rvBgMap.put(view, rvBg);
                view.setBackgroundColor(Color.TRANSPARENT);

                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null) {
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        RecyclerView.ViewHolder holder = adapter.createViewHolder(rv, adapter.getItemViewType(i));
                        adapter.onBindViewHolder(holder, i);
                        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(rv.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                        holder.itemView.setDrawingCacheEnabled(true);
                        holder.itemView.buildDrawingCache();
                        Bitmap drawingCache = holder.itemView.getDrawingCache();
                        if (drawingCache != null) {
                            bitmapCache.put(String.valueOf(count++), drawingCache);
                        }
                        height += holder.itemView.getMeasuredHeight();
                    }
                }
            } else {
                Bitmap viewBt = captureLayout(view);
                if (viewBt != null) {
                    height += view.getMeasuredHeight();
                    bitmapCache.put(String.valueOf(count++), viewBt);
                }
            }
            width = view.getMeasuredWidth() > width ? view.getMeasuredWidth() : width;
        }
        // 保存到缓存目录（无需权限）
        saveBitmapToCache(context, bitmapCache, width, height, fileName, new ICaptureCallback() {
            @Override
            public void onSuccess(String path) {
                // 恢复原始背景
                Set<Map.Entry<View, Drawable>> rvBgSet = rvBgMap.entrySet();
                for (Map.Entry<View, Drawable> item : rvBgSet) {
                    item.getKey().setBackground(item.getValue());
                }
                // 截图后恢复
                Set<Map.Entry<View, Integer>> visSet = visibilityMap.entrySet();
                for (Map.Entry<View, Integer> item : visSet) {
                    item.getKey().setVisibility(item.getValue());
                }
                if (callback != null) {
                    callback.onSuccess(path);
                }
            }

            @Override
            public void onErr(int errCode, String msg) {
                // 恢复原始背景
                Set<Map.Entry<View, Drawable>> rvBgSet = rvBgMap.entrySet();
                for (Map.Entry<View, Drawable> item : rvBgSet) {
                    item.getKey().setBackground(item.getValue());
                }
                // 截图后恢复
                Set<Map.Entry<View, Integer>> visSet = visibilityMap.entrySet();
                for (Map.Entry<View, Integer> item : visSet) {
                    item.getKey().setVisibility(item.getValue());
                }
                if (callback != null) {
                    callback.onErr(errCode, msg);
                }
            }
        });
    }

    public void captureAndSaveRv(Context context, String fileName, @NonNull RecyclerView dataRv, ICaptureCallback callback) {
        int visibilityRv = dataRv.getVisibility();
        // 确保布局可见
        dataRv.setVisibility(View.VISIBLE);
        // 临时移除背景（保留子元素内容）
        Drawable rvBg = dataRv.getBackground();
        dataRv.setBackgroundColor(Color.TRANSPARENT);

        int count = 0;
        int height = 0;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);

        RecyclerView.Adapter adapter = dataRv.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(dataRv, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(dataRv.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {
                    bitmapCache.put(String.valueOf(count++), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }
        }
        // 保存到缓存目录（无需权限）
        saveBitmapToCache(context, bitmapCache, dataRv.getMeasuredWidth(), height, fileName, new ICaptureCallback() {
            @Override
            public void onSuccess(String path) {
                // 恢复原始背景
                dataRv.setBackground(rvBg);
                // 截图后恢复
                dataRv.setVisibility(visibilityRv);
                if (callback != null) {
                    callback.onSuccess(path);
                }
            }

            @Override
            public void onErr(int errCode, String msg) {
                // 恢复原始背景
                dataRv.setBackground(rvBg);
                // 截图后恢复
                dataRv.setVisibility(visibilityRv);
                if (callback != null) {
                    callback.onErr(errCode, msg);
                }
            }
        });
    }

    // 修改后的保存方法（无需权限）
    public boolean saveBitmapToCache(Context context, LruCache<String, Bitmap> bitmapCache, int width, int height, String fileName, ICaptureCallback callback) {
        File cacheDir = context.getExternalCacheDir(); // 获取应用缓存目录
        if (cacheDir == null) {
            if (callback != null) {
                callback.onErr(ERR_SAVE_NO_DIR, "");
            }
            return false;
        }
        mExecutor.submit(() -> {
            // 生成 Bitmap
            Bitmap fullBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(fullBitmap);
            int iHeight = 0;
            Paint paint = new Paint();
            for (int i = 0; i < bitmapCache.size(); i++) {
                Bitmap bitmap = bitmapCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
            File imageFile = new File(cacheDir, fileName);
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                fullBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess(imageFile.getAbsolutePath());
                        }
                    }
                });
                fullBitmap.recycle();
            } catch (IOException e) {
                fullBitmap.recycle();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onErr(ERR_SAVE_EXCEPTION, e.toString());
                        }
                    }
                });
            }
        });
        return true;
    }

    // 截图方法（保持不变）
    public Bitmap captureLayout(View layout) {
        int totalHeight = layout.getMeasuredHeight();
        int totalWidth = layout.getWidth();
        if (totalHeight > 0 && totalWidth > 0) {
            Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            layout.draw(canvas);
            return bitmap;
        }
        return null;
    }

    public interface ICaptureCallback {
        void onSuccess(String path);

        void onErr(int errCode, String msg);
    }
}
