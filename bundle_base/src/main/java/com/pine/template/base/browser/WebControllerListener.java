package com.pine.template.base.browser;

import android.widget.ImageView;

public abstract class WebControllerListener {
    public static final int CONTROLLER_TYPE_GO_BACK = 1;
    public static final int CONTROLLER_TYPE_REFRESH = 2;
    public static final int CONTROLLER_TYPE_GO_HOME = 3;

    public boolean onControllerClick(ImageView view, int actionType) {
        return false;
    }
}
