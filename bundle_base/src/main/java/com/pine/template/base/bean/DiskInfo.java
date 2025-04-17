package com.pine.template.base.bean;

import com.pine.tool.util.DecimalUtils;

public class DiskInfo {
    private final long G_SIZE = 1024 * 1024 * 1024;
    private final long M_SIZE = 1024 * 1024;
    private final long K_SIZE = 1024;

    public String disPath;
    public long leftSize;
    public long totalSize;

    public long getUsedSize() {
        long used = totalSize - leftSize;
        return used < 0 ? 0 : used;
    }

    public int getUsedPercent() {
        if (totalSize <= 0) {
            return 0;
        }
        return (int) (getUsedSize() * 100 / totalSize);
    }

    public String getUsedShowTxt() {
        return getShowTxt(getUsedSize()) + "/" + getShowTxt(totalSize);
    }

    public int getLeftPercent() {
        if (totalSize <= 0) {
            return 100;
        }
        return (int) (leftSize * 100 / totalSize);
    }

    public String getLeftShowTxt() {
        return getShowTxt(leftSize) + "/" + getShowTxt(totalSize);
    }

    private String getShowTxt(long size) {
        if (size > G_SIZE) {
            return DecimalUtils.divide(size, G_SIZE, 1) + "GB";
        } else if (size > M_SIZE) {
            return DecimalUtils.divide(size, M_SIZE, 1) + "MB";
        } else if (size > K_SIZE) {
            return DecimalUtils.divide(size, K_SIZE, 1) + "KB";
        } else {
            return size + "B";
        }
    }

    @Override
    public String toString() {
        return "DiskInfo{" +
                "disPath='" + disPath + '\'' +
                ", leftSize=" + leftSize +
                ", totalSize=" + totalSize +
                '}';
    }
}
