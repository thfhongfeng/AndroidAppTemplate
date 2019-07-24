package com.pine.mvvm.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.mvvm.ui.activity.MvvmHomeActivity;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterCommand;
import com.pine.router.command.RouterMvvmCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvvmRemoteService {

    @RouterCommand(CommandName = RouterMvvmCommand.goMvvmHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        context.startActivity(new Intent(context, MvvmHomeActivity.class));
        callback.onResponse(responseBundle);
    }
}
