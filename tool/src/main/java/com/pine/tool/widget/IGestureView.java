package com.pine.tool.widget;

/**
 * Created by tanghongfeng on 2019/10/10.
 */

public interface IGestureView {
    /**
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     * @return
     */
    boolean canScroll(boolean checkV, int dx, int x, int y);
}
