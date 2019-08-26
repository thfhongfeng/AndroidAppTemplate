package com.pine.mvc.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.mvc.ui.activity.MvcHomeActivity;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterCommand;
import com.pine.base.router.command.RouterMvcCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvcRemoteService {

    @RouterCommand(CommandName = RouterMvcCommand.goMvcHomeActivity)
    public void goBusinessHomeActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, MvcHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
