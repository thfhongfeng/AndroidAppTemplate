package com.pine.app.lib.face.detect.normal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.FaceRange;

import java.util.ArrayList;
import java.util.List;

public class FaceBorderView extends View implements IFaceRectView {
    private final String TAG = this.getClass().getSimpleName();

    private final int BORDER_COLOR = Color.WHITE;
    private final int BORDER_WITH = 3;

    private DetectConfig detectConfig = new DetectConfig("");

    public FaceBorderView(Context context) {
        super(context);
        init();
    }

    public FaceBorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint paint;

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(BORDER_WITH);
        paint.setColor(BORDER_COLOR);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void setupDetectConfig(@NonNull DetectConfig detectConfig) {
        this.detectConfig = detectConfig;
    }

    @Override
    public synchronized void drawFacesBorder(@NonNull List<FacePosDetail> facePosDetails,
                                             DetectConfig config,
                                             int innerFrameW, int innerFrame) {
        faceBorderList.clear();
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
                faceBorder.borderPath.reset();
                faceBorder.borderPath.moveTo(leftX, topY);
                faceBorder.borderPath.lineTo(rightX, topY);
                faceBorder.borderPath.lineTo(rightX, bottomY);
                faceBorder.borderPath.lineTo(leftX, bottomY);
                faceBorder.borderPath.lineTo(leftX, topY);
                faceBorder.confidence = facePosDetail.confidence;
                faceBorder.liveConfidence = facePosDetail.liveConfidence;
                faceBorderList.add(faceBorder);
            }
        }
        postInvalidate();
    }

    private List<FaceBorder> faceBorderList = new ArrayList<FaceBorder>();

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

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (FaceBorder faceBorder : faceBorderList) {
            canvas.drawPath(faceBorder.borderPath, paint);
        }
    }

    @Override
    public synchronized void clearBorder() {
        faceBorderList.clear();
        postInvalidate();
    }

    class FaceBorder {
        public Path borderPath = new Path();
        public FaceRange faceRange = new FaceRange();
        public float confidence = 0f;
        public float liveConfidence = 0f;
    }
}
