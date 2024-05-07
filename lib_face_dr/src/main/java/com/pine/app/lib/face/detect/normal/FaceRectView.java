package com.pine.app.lib.face.detect.normal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.R;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.FaceRange;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FaceRectView extends View implements IFaceRectView {
    private final String TAG = this.getClass().getSimpleName();

    private Paint paint;
    private int realColor;
    private int fakeColor;
    private float radius;
    private float textSize;
    private int textPadding;
    private DetectConfig detectConfig = new DetectConfig("");

    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    public FaceRectView(Context context) {
        super(context);
        initView(null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private void initView(AttributeSet attrs) {
        realColor = Color.parseColor("#00FFFF");
        fakeColor = Color.parseColor("#F84813");
        radius = dpToPx(3);
        textSize = spToPx(13);
        textPadding = dpToPx(6);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FaceRectView);
            realColor = typedArray.getColor(R.styleable.FaceRectView_real_color, Color.parseColor("#00FFFF"));
            fakeColor = typedArray.getColor(R.styleable.FaceRectView_fake_color, Color.parseColor("#F84813"));
            textSize = typedArray.getDimension(R.styleable.FaceRectView_textSize, spToPx(13));
            textPadding = typedArray.getDimensionPixelOffset(R.styleable.FaceRectView_textPadding, dpToPx(6));
            radius = typedArray.getDimension(R.styleable.FaceRectView_radius, dpToPx(3));
            typedArray.recycle();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dpToPx(2));
        paint.setTextSize(textSize);
    }

    @Override
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);

        FaceRange rect;
        RectF leftTopAcrRectF;
        RectF rightTopAcrRectF;
        RectF leftBottomAcrRectF;
        RectF rightBottomAcrRectF;
        Rect textBackgroundRect;
        Rect textBoundsRect;

        for (int i = 0; i < faceBorderList.size(); i++) {
            FaceBorder faceBorder = faceBorderList.get(i);
            rect = faceBorder.faceRange;
            leftTopAcrRectF = new RectF();
            rightTopAcrRectF = new RectF();
            leftBottomAcrRectF = new RectF();
            rightBottomAcrRectF = new RectF();

            if (detectConfig.liveConfidenceEnable) {
                if (detectConfig.liveCheckForAllFace || i == 0) {
                    paint.setColor(faceBorder.liveConfidence > detectConfig.liveConfidenceThreshold ? realColor : fakeColor);
                } else {
                    paint.setColor(realColor);
                }
            } else {
                paint.setColor(realColor);
            }

            float lineLength = Math.min(Math.abs(rect.left - rect.right), Math.abs(rect.top - rect.bottom)) / 4;
            lineLength = Math.max(lineLength, 4.0f);

            // left top
            canvas.drawLine(rect.left, rect.top + lineLength, rect.left, rect.top + radius, paint);
            leftTopAcrRectF.left = rect.left;
            leftTopAcrRectF.top = rect.top;
            leftTopAcrRectF.right = rect.left + radius * 2;
            leftTopAcrRectF.bottom = rect.top + radius * 2;
            canvas.drawArc(leftTopAcrRectF, 180F, 90F, false, paint);
            canvas.drawLine(rect.left + radius, rect.top, rect.left + lineLength, rect.top, paint);

            // right top
            canvas.drawLine(rect.right - lineLength, rect.top, rect.right - radius, rect.top, paint);
            rightTopAcrRectF.left = rect.right - radius * 2;
            rightTopAcrRectF.top = rect.top;
            rightTopAcrRectF.right = rect.right;
            rightTopAcrRectF.bottom = rect.top + radius * 2;
            canvas.drawArc(rightTopAcrRectF, 0F, -90F, false, paint);
            canvas.drawLine(rect.right, rect.top + radius, rect.right, rect.top + lineLength, paint);

            //left bottom
            canvas.drawLine(rect.left, rect.bottom - lineLength, rect.left, rect.bottom - radius, paint);
            leftBottomAcrRectF.left = rect.left;
            leftBottomAcrRectF.top = rect.bottom - radius * 2;
            leftBottomAcrRectF.right = rect.left + radius * 2;
            leftBottomAcrRectF.bottom = rect.bottom;
            canvas.drawArc(leftBottomAcrRectF, 180F, -90F, false, paint);
            canvas.drawLine(rect.left + radius, rect.bottom, rect.left + lineLength, rect.bottom, paint);

            // right bottom
            canvas.drawLine(rect.right - lineLength, rect.bottom, rect.right - radius, rect.bottom, paint);
            rightBottomAcrRectF.left = rect.right - radius * 2;
            rightBottomAcrRectF.top = rect.bottom - radius * 2;
            rightBottomAcrRectF.right = rect.right;
            rightBottomAcrRectF.bottom = rect.bottom;
            canvas.drawArc(rightBottomAcrRectF, 0F, 90F, false, paint);
            canvas.drawLine(rect.right, rect.bottom - radius, rect.right, rect.bottom - lineLength, paint);

            boolean showTxt = detectConfig.showConfidenceTxt &&
                    (detectConfig.liveCheckForAllFace || faceBorder.mainFace);
            if (showTxt) {
                textBackgroundRect = new Rect();
                textBoundsRect = new Rect();
                String text = decimalFormat.format(detectConfig.liveConfidenceEnable
                        ? faceBorder.liveConfidence : faceBorder.confidence);
                paint.getTextBounds(text, 0, text.length(), textBoundsRect);
                int textWidth = textBoundsRect.width();
                int textHeight = textBoundsRect.height();

                textBackgroundRect.left = (int) rect.left;
                textBackgroundRect.top = (int) rect.top - textHeight - 2 * textPadding;
                textBackgroundRect.right = (int) rect.left + textWidth + 2 * textPadding;
                textBackgroundRect.bottom = (int) rect.top;
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(textBackgroundRect, paint);

                textBoundsRect.left = (int) rect.left + textPadding;
                textBoundsRect.top = (int) rect.top - textPadding - textHeight;
                textBoundsRect.right = (int) rect.left + textPadding + textWidth;
                textBoundsRect.bottom = (int) rect.top - textPadding;
                paint.setColor(Color.WHITE);
                canvas.drawText(text, (float) textBoundsRect.left, (float) textBoundsRect.bottom, paint);
            }
        }
    }

    @Override
    public void setupDetectConfig(@NonNull DetectConfig detectConfig) {
        this.detectConfig = detectConfig;
    }

    private List<FaceBorder> faceBorderList = new ArrayList<>();

    /**
     * @param facePosDetails 人脸特征参数
     * @param config         DetectConfig
     * @param innerFrameW
     * @param innerFrameH
     */
    @Override
    public synchronized void drawFacesBorder(List<FacePosDetail> facePosDetails, DetectConfig config,
                                             int innerFrameW, int innerFrameH) {
        faceBorderList.clear();
        FaceBorder mainFaceBorder = null;
        double maxFaceArea = 0;
        for (FacePosDetail facePosDetail : facePosDetails) {
            //可行度大于0.5才进行绘制
            if (facePosDetail.confidence > config.confidenceThreshold) {
                float bordHalfWidth = (facePosDetail.eyesDist / 2) * 5 / 2;
                if (facePosDetail.width > 0) {
                    bordHalfWidth = facePosDetail.width / 2;
                }
                float bordHalfHeight = (facePosDetail.eyesDist / 2) * 4;
                if (facePosDetail.height > 0) {
                    bordHalfHeight = facePosDetail.height / 2;
                }
                float midPointX = facePosDetail.midPointX;
                float midPointY = facePosDetail.midPointY;

                float leftX = midPointX - bordHalfWidth;
                float rightX = midPointX + bordHalfWidth;
                float topY = midPointY - bordHalfHeight;
                float bottomY = midPointY + bordHalfHeight;
                FaceBorder faceBorder = new FaceBorder();
                faceBorder.faceRange.left = leftX;
                faceBorder.faceRange.right = rightX;
                faceBorder.faceRange.top = topY;
                faceBorder.faceRange.bottom = bottomY;
                faceBorder.confidence = facePosDetail.confidence;
                faceBorder.liveConfidence = facePosDetail.liveConfidence;
                faceBorderList.add(faceBorder);
                double faceArea = Math.abs(rightX - leftX) * Math.abs(bottomY - topY);
                if (faceArea > maxFaceArea) {
                    maxFaceArea = faceArea;
                    mainFaceBorder = faceBorder;
                }
            }
        }
        if (mainFaceBorder != null) {
            mainFaceBorder.mainFace = true;
        }
        postInvalidate();
    }

    @Override
    public synchronized void clearBorder() {
        faceBorderList.clear();
        postInvalidate();
    }

    @Override
    public synchronized List<FaceRange> getFaceRangList() {
        List<FaceRange> list = new ArrayList<>();
        for (FaceBorder faceBorder : faceBorderList) {
            if (!detectConfig.liveConfidenceEnable
                    || faceBorder.liveConfidence > detectConfig.liveConfidenceThreshold) {
                list.add(faceBorder.faceRange);
            }
        }
        return list;
    }

    class FaceBorder {
        public FaceRange faceRange = new FaceRange();
        public boolean mainFace = false;
        public float confidence = 0f;
        public float liveConfidence = 0f;
    }
}
