package com.pine.template.base.component.image_loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class ImageOptionDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] mMatrixValues = new float[9];
    private int mWidth, mHeight;
    private Bitmap mResource;

    public ImageOptionDrawable(Resources res, @DrawableRes int resourceId) {
        String resType = res.getResourceTypeName(resourceId);
        if (TextUtils.equals(resType, "drawable") || TextUtils.equals(resType, "mipmap")) {
            // 该资源 ID 是 res/drawable 目录下的图片资源文件
            mResource = BitmapFactory.decodeResource(res, resourceId);
        } else if (res.getResourceTypeName(resourceId).equals("attr")) {
            // 该资源 ID 是 XML 定义的 drawable
            Drawable drawable = res.getDrawable(resourceId);
            // 对 drawable 进行操作
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            // 获取 bitmap 对象
            mResource = bitmapDrawable.getBitmap();
        }
        init();
    }

    public ImageOptionDrawable(Resources res, @DrawableRes int resourceId, int width, int height) {
        String resType = res.getResourceTypeName(resourceId);
        if (TextUtils.equals(resType, "drawable") || TextUtils.equals(resType, "mipmap")) {
            // 该资源 ID 是 res/drawable 目录下的图片资源文件
            mResource = BitmapFactory.decodeResource(res, resourceId);
        } else if (res.getResourceTypeName(resourceId).equals("attr")) {
            // 该资源 ID 是 XML 定义的 drawable
            Drawable drawable = res.getDrawable(resourceId);
            // 对 drawable 进行操作
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            // 获取 bitmap 对象
            mResource = bitmapDrawable.getBitmap();
        }
        this.mHeight = width;
        this.mWidth = height;
    }

    public ImageOptionDrawable(Bitmap resource) {
        this.mResource = resource;
        init();
    }

    public ImageOptionDrawable(Bitmap resource, int width, int height) {
        this.mResource = resource;
        this.mHeight = width;
        this.mWidth = height;
    }

    private void init() {
        if (mResource == null) {
            return;
        }
        this.mHeight = mResource.getHeight();
        this.mWidth = mResource.getWidth();
    }

    @Override
    public int getMinimumHeight() {
        return mHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mResource == null) {
            return;
        }
        //canvas.getMatrix()这个方法已经@Deprecated了,但是这里要实现功能不得不用,缩放,位移啊,数据都在matrix里了
        Matrix matrix = canvas.getMatrix();
        matrix.getValues(mMatrixValues);
        //由于缩放的中心是在左上角,而不是图片中心,故需要再平衡一下因为缩放造成的位移
        mMatrixValues[Matrix.MTRANS_X] = ((canvas.getWidth() - mWidth) / 2 - mMatrixValues[Matrix.MTRANS_X]) / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MTRANS_Y] = ((canvas.getHeight() - mHeight) / 2 - mMatrixValues[Matrix.MTRANS_Y]) / mMatrixValues[Matrix.MSCALE_Y];
        //尺寸反向缩放
        mMatrixValues[Matrix.MSCALE_X] = 1 / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MSCALE_Y] = 1 / mMatrixValues[Matrix.MSCALE_Y];
        matrix.setValues(mMatrixValues);
        canvas.drawBitmap(mResource, matrix, mPaint);
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
