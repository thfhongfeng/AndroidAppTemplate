package com.pine.template.base.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.tool.util.ImageUtils;

import java.io.File;

public class GestureImageView extends View {

    private Bitmap mBitmap; // 图片
    private float mRotation; // 旋转角度
    private float mScaleFactor = 1.0f;
    private float mAlpha = 1.0f; // 透明度
    private float mTranslationX, mTranslationY; // 拖拽移动距离
    private float mLastTouchX1, mLastTouchY1, mLastTouchX2, mLastTouchY2; // 双指上次触摸点坐标
    private float mLastTouchX = Integer.MAX_VALUE, mLastTouchY = Integer.MAX_VALUE; // 单指上次触摸点坐标

    private ScaleGestureDetector mScaleGestureDetector;

    // 初始化画笔
    private Paint mPaint = new Paint();

    public GestureImageView(Context context) {
        super(context);
        initView(context);
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(@NonNull Context context) {
        mPaint.setAntiAlias(true);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
    }

    public void setupImg(String imgPath) {
        mBitmap = ImageUtils.getBitmap(new File(imgPath));
    }

    public void setupImg(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            return;
        }
        // 保存画布状态
        canvas.save();
        // 移动画布到中心点
        canvas.translate(getWidth() / 2f + mTranslationX, getHeight() / 2f + mTranslationY);
        // 缩放画布
        canvas.scale(mScaleFactor, mScaleFactor);
        // 旋转画布
        canvas.rotate(mRotation);
        // 绘制图片及设置透明度
        mPaint.setAlpha((int) (mAlpha * 255));
        canvas.drawBitmap(mBitmap, -mBitmap.getWidth() / 2f, -mBitmap.getHeight() / 2f, mPaint);
        // 恢复画布状态
        canvas.restore();
    }

    private int mPointActionIndex;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mBitmap == null) {
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 重置上次触摸点坐标
                // 两指触摸时，记录上次触摸点坐标
                mLastTouchX1 = event.getX(0);
                mLastTouchY1 = event.getY(0);
                mLastTouchX2 = event.getX(1);
                mLastTouchY2 = event.getY(1);
                mPointActionIndex = event.getActionIndex();
                mLastTouchX = mPointActionIndex == 0 ? mLastTouchX2 : mLastTouchX1;
                mLastTouchY = mPointActionIndex == 0 ? mLastTouchY2 : mLastTouchY1;
                // 重绘视图
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    // 计算旋转角度
                    float x1 = event.getX(0);
                    float y1 = event.getY(0);
                    float x2 = event.getX(1);
                    float y2 = event.getY(1);
                    double lastAngle = Math.atan2(mLastTouchX2 - mLastTouchX1, mLastTouchY2 - mLastTouchY1);
                    double currentAngle = Math.atan2(x2 - x1, y2 - y1);
                    mRotation += Math.toDegrees(lastAngle - currentAngle);
                    // 限制旋转角度的范围在0到360度之间
                    mRotation = (mRotation + 360) % 360;
                    // 更新上次触摸点坐标
                    mLastTouchX1 = event.getX(0);
                    mLastTouchY1 = event.getY(0);
                    mLastTouchX2 = event.getX(1);
                    mLastTouchY2 = event.getY(1);
                    mLastTouchX = mPointActionIndex == 0 ? mLastTouchX2 : mLastTouchX1;
                    mLastTouchY = mPointActionIndex == 0 ? mLastTouchY2 : mLastTouchY1;
                } else if (event.getPointerCount() == 1) {
                    // 拖拽移动
                    float x = event.getX();
                    float y = event.getY();
                    if (mLastTouchX != Integer.MAX_VALUE && mLastTouchY != Integer.MAX_VALUE) {
                        float deltaX = x - mLastTouchX;
                        float deltaY = y - mLastTouchY;
                        mTranslationX += deltaX;
                        mTranslationY += deltaY;
                    }
                    // 更新上次触摸点坐标
                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                // 重绘视图
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                // 记录触摸点坐标
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                // 手指按下时设置透明度
                mAlpha = 0.5f;
                // 重绘视图
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 手指抬起或者取消时恢复透明度
                mAlpha = 1f;
                // 重置触摸点坐标
                mLastTouchX1 = 0f;
                mLastTouchY1 = 0f;
                mLastTouchX2 = 0f;
                mLastTouchY2 = 0f;
                mLastTouchX = 0f;
                mLastTouchY = 0f;
                // 重绘视图
                invalidate();
                break;
        }
        return true;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            invalidate();
            return true;
        }
    }
}
