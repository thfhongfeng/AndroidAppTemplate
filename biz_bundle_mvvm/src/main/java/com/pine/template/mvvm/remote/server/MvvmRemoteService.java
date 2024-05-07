package com.pine.template.mvvm.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.biz_bundle_mvvm.router.RouterMvvmCommand;
import com.pine.template.mvvm.MvvmApplication;
import com.pine.template.mvvm.ui.activity.MvvmHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvvmRemoteService {
    @RouterCommand(CommandName = "onAppCreate")
    public void onAppCreate(@NonNull Context context, Bundle args) {
        MvvmApplication.onCreate();
    }

    @RouterCommand(CommandName = "onAppAttach")
    public void onAppAttach(@NonNull Context context, Bundle args) {
        MvvmApplication.attach();
    }

    @RouterCommand(CommandName = RouterMvvmCommand.goMvvmHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        context.startActivity(new Intent(context, MvvmHomeActivity.class));
        callback.onResponse(responseBundle);
    }
}
