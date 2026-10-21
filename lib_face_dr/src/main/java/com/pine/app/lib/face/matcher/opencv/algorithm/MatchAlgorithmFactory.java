package com.pine.app.lib.face.matcher.opencv.algorithm;

import com.pine.app.lib.face.matcher.opencv.OcvFaceMatcher;

public class MatchAlgorithmFactory {
    public static MatchAlgorithm createInstance(int type) {
        switch (type) {
            case OcvFaceMatcher.FEATURE_TYPE_SF:
                return new SFMatchAlgorithm();
            case OcvFaceMatcher.FEATURE_TYPE_ARC_NET:
                return new ArcNetMatchAlgorithm();
            default:
                return new ORBMatchAlgorithm();
        }
    }
}
