package com.pine.template.base.map;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.json.JSONObject;

public abstract class IMapView extends RelativeLayout {
    public IMapView(Context context) {
        super(context);
    }

    public IMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setupUi(String keyword, boolean autoQuery, Bundle config);

    public abstract void onSpeechIntent(String action, JSONObject data);

    public abstract void onUiShow(boolean first, Bundle args);

    public abstract void onUiHide(Bundle args);

    public abstract void onCreate(Context context, Bundle args);

    public abstract void onStart();

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onDestroy();
}
