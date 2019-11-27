package com.pine.main.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.base.router.command.RouterMainCommand;
import com.pine.main.ui.activity.MainHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MainRemoteService {

    @RouterCommand(CommandName = RouterMainCommand.goMainHomeActivity)
    public void goMainHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, MainHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
