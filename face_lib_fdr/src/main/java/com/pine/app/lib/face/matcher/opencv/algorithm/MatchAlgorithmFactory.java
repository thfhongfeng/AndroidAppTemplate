package com.pine.app.lib.face.matcher.opencv.algorithm;

import com.pine.app.lib.face.matcher.IFaceMatcher;

public class MatchAlgorithmFactory {
    public static MatchAlgorithm createInstance(int type) {
        switch (type) {
            case IFaceMatcher.FEATURE_TYPE_SF:
                return new SFMatchAlgorithm();
            default:
                return new ORBMatchAlgorithm();
        }
    }
}
