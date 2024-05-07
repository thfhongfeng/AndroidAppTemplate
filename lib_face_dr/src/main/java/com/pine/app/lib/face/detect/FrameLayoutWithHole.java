package com.pine.app.lib.face.detect;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pine.app.lib.face.R;

public class FrameLayoutWithHole extends FrameLayout {
    private final String TAG = "FrameLayoutWithHole";

    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private Paint mEraser;
    private Context mContext;

    // 单位由mDpUnit确定
    private float mRadius;
    private int mBackgroundColor;
    // 单位由mDpUnit确定
    private float mRx;//默认在中心x位置
    // 单位由mDpUnit确定
    private float mRy;//默认在中心y位置
    // 单位是否dp，否则px
    private boolean mDpUnit = false;

    public FrameLayoutWithHole(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        /*
        hole_radius 为镂空圆的半径，单位dp
        background_color 为透明色背景
        radius_x 为圆心的x轴坐标，单位dp
        radius_y 为圆心的y轴坐标，单位dp
        */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FrameLayoutWithHole);
        mBackgroundColor = ta.getColor(R.styleable.FrameLayoutWithHole_hole_container_bg, -1);
        mRadius = ta.getFloat(R.styleable.FrameLayoutWithHole_hole_radius, 0);
        mRx = ta.getFloat(R.styleable.FrameLayoutWithHole_hole_radius_x, 0);
        mRy = ta.getFloat(R.styleable.FrameLayoutWithHole_hole_radius_y, 0);
        mDpUnit = true;
        init(null, 0);
        ta.recycle();
    }

    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FrameLayoutWithHole(Context context, int backgroundColor, int radius
            , int rx, int ry, boolean dpUnit) {//半径位置
        this(context);

        mBackgroundColor = backgroundColor;
        this.mRadius = radius;
        this.mRx = rx;
        this.mRy = ry;
        this.mDpUnit = dpUnit;
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);

        Point size = new Point();
        size.x = mContext.getResources().getDisplayMetrics().widthPixels;
        size.y = mContext.getResources().getDisplayMetrics().heightPixels;

        if (mContext instanceof Activity) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
            size.x = outMetrics.widthPixels;
            size.y = outMetrics.heightPixels;
        }
        mRadius = mRadius != 0 ? mRadius : 150;

        mBackgroundColor = mBackgroundColor != -1 ? mBackgroundColor : Color.parseColor("#55000000");

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);

        if (mDpUnit) {
            float density = mContext.getResources().getDisplayMetrics().density;
            mRx = mRx * density;
            mRy = mRy * density;
            mRadius = mRadius * density;
        }

        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        Log.d(TAG, "size.x: " + size.x + ",size.y:" + size.y + ",mRadius:" + mRadius
                + ",mRx:" + mRx + ", mRy:" + mRy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(mBackgroundColor);

        mEraserCanvas.drawCircle(
                mRx,
                mRy,
                mRadius, mEraser);

        canvas.drawBitmap(mEraserBitmap, 0, 0, null);

    }
}

