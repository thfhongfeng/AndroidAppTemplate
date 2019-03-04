package com.pine.mvvm.remote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.mvvm.ui.activity.MvvmHomeActivity;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.command.RouterMvvmCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvvmUiRemoteService {

    @RouterAnnotation(CommandName = RouterMvvmCommand.goMvvmHomeActivity)
    public void goBusinessHomeActivity(@NonNull Activity activity, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        activity.startActivity(new Intent(activity, MvvmHomeActivity.class));
        callback.onResponse(responseBundle);
    }
}
