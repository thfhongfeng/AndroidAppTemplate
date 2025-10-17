package com.pine.template.base.browser.hdmiin.lztek;

public final class Amix_WM8988 implements Amix.Proxy {

    static {
        ExecUtl.execCommand(new String[]{
                "amix 40 0",
                "amix 42 0",
                "amix 44 0",
                "amix 46 0",
        });
        lineInState(false);
    }

    private static Object mMutext = new Object();

    private static boolean mOpen = false;

    private static final int[] mLevelValues = new int[]{
            0,        // 0
            140,    // 1
            150,    // 2
            160,    // 3
            170,    // 4
            175,    // 5
            180,    // 6
            185,    // 7
            190,    // 8
            195,    // 9
            200    // 10
    };

    private static int mLevel = mLevelValues.length - 3;

    private static void lineInState(boolean on) {
        if (on) {
            ExecUtl.execCommand(new String[]{
                    "amix 23 0",
                    "amix 22 " + getValue(mLevel),
                    "amix 49 1",
                    "amix 50 1",
                    "amix 51 0",
            });
        } else {
            ExecUtl.execCommand(new String[]{
                    "amix 51 2",
                    "amix 50 0",
                    "amix 49 0",
                    "amix 23 23",
                    "amix 22 236",
            });
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
                    ExecUtl.execCommand("amix 22 " + getValue(mLevel));
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
