package com.pine.mvp.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.base.router.command.RouterMvpCommand;
import com.pine.mvp.ui.activity.MvpHomeActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpRemoteService {

    @RouterCommand(CommandName = RouterMvpCommand.goMvpHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, MvpHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
