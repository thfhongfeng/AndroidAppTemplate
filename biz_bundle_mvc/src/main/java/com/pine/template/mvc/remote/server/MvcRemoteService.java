package com.pine.template.mvc.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.biz_bundle_mvc.router.RouterMvcCommand;
import com.pine.template.mvc.MvcApplication;
import com.pine.template.mvc.ui.activity.MvcHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvcRemoteService {
    @RouterCommand(CommandName = "onAppCreate")
    public void onAppCreate(@NonNull Context context, Bundle args) {
        MvcApplication.onCreate();
    }

    @RouterCommand(CommandName = "onAppAttach")
    public void onAppAttach(@NonNull Context context, Bundle args) {
        MvcApplication.attach();
    }

    @RouterCommand(CommandName = RouterMvcCommand.goMvcHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, MvcHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
