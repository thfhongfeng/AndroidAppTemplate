package com.pine.template.mvp.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.biz_bundle_mvp.router.RouterMvpCommand;
import com.pine.template.mvp.MvpApplication;
import com.pine.template.mvp.ui.activity.MvpHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpRemoteService {
    @RouterCommand(CommandName = "onAppCreate")
    public void onAppCreate(@NonNull Context context, Bundle args) {
        MvpApplication.onCreate();
    }

    @RouterCommand(CommandName = "onAppAttach")
    public void onAppAttach(@NonNull Context context, Bundle args) {
        MvpApplication.attach();
    }

    @RouterCommand(CommandName = RouterMvpCommand.goMvpHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, MvpHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
