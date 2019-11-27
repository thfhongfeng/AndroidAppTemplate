package com.pine.tool.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.pine.tool.R;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class RoundImageView extends AppCompatImageView {
    private int mRadius;
    private float[] mSrcRadiusArr;
    private PorterDuffXfermode mXfermode;
    private Paint mPaint;

    public RoundImageView(Context context) {
        this(context, null, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundImageView_riv_radius,
                getResources().getDimensionPixelOffset(R.dimen.dp_5));
        init();
    }

    private void init() {
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mSrcRadiusArr = new float[]{mRadius, mRadius, mRadius, mRadius,
                mRadius, mRadius, mRadius, mRadius};
    }

    //设置圆角角度
    public void setRadius(int radius) {
        mRadius = radius;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF srcRectF = new RectF(0, 0, getWidth(), getHeight());
        // 使用离屏缓存，新建一个srcRectF区域大小的图层
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        // ImageView自身的绘制流程，即绘制图片
        super.onDraw(canvas);
        Path path = new Path();
        // 给path添加一个圆角矩形
        path.addRoundRect(srcRectF, mSrcRadiusArr, Path.Direction.CCW);
        // 设置混合模式
        mPaint.setXfermode(mXfermode);
        // 绘制path
        canvas.drawPath(path, mPaint);
        // 清除Xfermode
        mPaint.setXfermode(null);
        // 恢复画布状态
        canvas.restore();
    }
}
