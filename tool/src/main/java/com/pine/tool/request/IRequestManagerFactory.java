package com.pine.tool.request;

import android.content.Context;

import java.util.HashMap;

public interface IRequestManagerFactory {
    IRequestManager makeRequestManager(Context context, HashMap<String, String> head);
}
