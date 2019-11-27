package com.pine.mvvm.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.base.router.command.RouterMvvmCommand;
import com.pine.mvvm.ui.activity.MvvmHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

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
