package com.pine.app.lib.face.detect;

import java.util.List;

public class FaceRange {
    private final String TAG = this.getClass().getSimpleName();

    public float left;
    public float right;
    public float top;
    public float bottom;

    public void reset() {
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;
    }

    public boolean matchDetect(List<FaceRange> detectRanges, DetectConfig config) {
        float centerFactor = config.matchCenterDiffFactor > 0 ? config.matchCenterDiffFactor : -1;
        float edgeFactor = config.matchEdgeDiffFactor <= 0 ? 0.1f : config.matchEdgeDiffFactor;
        // 微视截框较小，适当增加边缘偏差
        if (config.cameraDetectProvider == DetectConfig.DETECT_PROVIDER_MINI_VISION) {
            edgeFactor = edgeFactor + 0.1f;
        }

        float validCenterX = (left + right) / 2;
        float validCenterY = (top + bottom) / 2;
        float validWidth = Math.abs(right - left);
        float validHeight = Math.abs(bottom - top);
        if (validWidth <= 0 || validHeight <= 0) {
            return false;
        }
        double validArea = validWidth * validHeight;
        double matchMaxArea = validArea * (1 + edgeFactor) * (1 + edgeFactor);
        float minEdgeFactorScale = 1 - edgeFactor;
        minEdgeFactorScale = minEdgeFactorScale > 0 ? minEdgeFactorScale : 0;
        double matchMinArea = validArea * minEdgeFactorScale * minEdgeFactorScale;
        for (FaceRange detectRange : detectRanges) {
            float detectWidth = Math.abs(detectRange.right - detectRange.left);
            float detectHeight = Math.abs(detectRange.bottom - detectRange.top);
            float detectCenterX = (detectRange.left + detectRange.right) / 2;
            float detectCenterY = (detectRange.top + detectRange.bottom) / 2;
            double detectArea = detectWidth * detectHeight;
            boolean centerMatch = false;
            if (centerFactor > 0) {
                centerMatch = Math.abs(validCenterX - detectCenterX) < validWidth * centerFactor &&
                        Math.abs(validCenterY - detectCenterY) < validHeight * centerFactor;
            } else { // 框内
                double validA = validArea / 4;
                double leftXOffsetA = Math.abs(validCenterX - detectRange.left) * Math.abs(validCenterX - detectRange.left);
                double rightXOffsetA = Math.abs(validCenterX - detectRange.right) * Math.abs(validCenterX - detectRange.right);
                double topYOffsetA = Math.abs(validCenterY - detectRange.top) * Math.abs(validCenterY - detectRange.top);
                double bottomYOffsetA = Math.abs(validCenterY - detectRange.bottom) * Math.abs(validCenterY - detectRange.bottom);
                centerMatch = leftXOffsetA + topYOffsetA < validA
                        && rightXOffsetA + topYOffsetA < validA
                        && leftXOffsetA + bottomYOffsetA < validA
                        && rightXOffsetA + bottomYOffsetA < validA;
            }
            boolean rectangleMatch = detectArea < matchMaxArea && detectArea > matchMinArea;
            if (centerMatch && rectangleMatch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "FaceRange{" +
                "left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                '}';
    }
}
