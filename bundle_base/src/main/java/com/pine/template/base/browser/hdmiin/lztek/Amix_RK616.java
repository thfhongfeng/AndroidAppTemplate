package com.pine.template.base.browser.hdmiin.lztek;

public final class Amix_RK616 implements Amix.Proxy {

    static {
        lineInState(false);
    }

    private static Object mMutext = new Object();

    private static boolean mOpen = false;

    private static final int[] mLevelValues = new int[]{
            0,        // 0
            6,        // 1
            8,        // 2
            10,    // 3
            12,    // 4
            14,    // 5
            16,    // 6
            18,    // 7
            20,    // 8
            22,    // 9
            24        // 10
    };

    private static int mLevel = mLevelValues.length - 3;

    private static void lineInState(boolean on) {
        if (on) {
            String[] command = new String[]{
                    "amix 3 2",
                    "amix 5 " + getValue(mLevel),
                    "amix 7 " + (getValue(mLevel) > 0 ? 1 : 0),
            };
            ExecUtl.execCommand(command);
        } else {
            String[] command = new String[]{
                    "amix 3 0",
                    "amix 5 24",
                    "amix 7 0"
            };
            ExecUtl.execCommand(command);
        }
    }

    private static int getValue(int level) {
        if (level < 0) {
            level = 0;
        } else if (mLevel >= mLevelValues.length) {
            level = mLevelValues.length - 1;
        }
        return mLevelValues[mLevel];
    }

    @Override
    public void setVolume(int level) {
        if (mLevelValues == null || mLevelValues.length <= 0) {
            return;
        }

        if (level < 0) {
            level = 0;
        } else if (level >= mLevelValues.length) {
            level = mLevelValues.length - 1;
        }

        synchronized (mMutext) {
            if (mLevel != level) {
                mLevel = level;
                if (mOpen) {
                    String[] command = new String[]{
                            "amix 5 " + getValue(mLevel),
                            "amix 7 " + (getValue(mLevel) > 0 ? 1 : 0),
                    };
                    ExecUtl.execCommand(command);
                }
            }
        }
    }

    @Override
    public void lineInOn() {
        if (mLevelValues == null || mLevelValues.length <= 0) {
            return;
        }

        synchronized (mMutext) {
            if (!mOpen) {
                lineInState(true);
                mOpen = true;
            }
        }
    }

    @Override
    public void lineInOff() {
        synchronized (mMutext) {
            if (mOpen) {
                lineInState(false);
                mOpen = false;
            }
        }
    }
}
