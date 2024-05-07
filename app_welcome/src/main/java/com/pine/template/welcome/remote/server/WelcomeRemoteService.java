package com.pine.template.welcome.remote.server;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.app_welcome.router.RouterWelcomeCommand;
import com.pine.template.welcome.updater.ApkVersionManager;
import com.pine.template.welcome.updater.VersionEntity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;

import java.io.File;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class WelcomeRemoteService {
    @RouterCommand(CommandName = RouterWelcomeCommand.checkApkUpdate)
    public void checkApkUpdate(@NonNull final Context context, Bundle args,
                               @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        ApkVersionManager.getInstance().checkAndUpdateApk((Activity) context, true, true,
                new ApkVersionManager.IUpdateCallback() {
                    @Override
                    public void onNoNewVersion() {
                        responseBundle.putBoolean("success", true);
                        responseBundle.putBoolean("newVersion", false);
                        callback.onResponse(responseBundle);
                    }

                    @Override
                    public void onNewVersionFound(VersionEntity versionEntity) {
                        responseBundle.putBoolean("success", true);
                        responseBundle.putBoolean("newVersion", true);
                        callback.onResponse(responseBundle);
                    }

                    @Override
                    public boolean installApk(VersionEntity versionEntity, File apkFile) {
                        return false;
                    }

                    @Override
                    public void onUpdateComplete(VersionEntity versionEntity) {
                        ApkVersionManager.getInstance().onClear();
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onUpdateErr(int errCode, String errMsg,
                                            VersionEntity versionEntity) {
                        responseBundle.putBoolean("success", false);
                        responseBundle.putInt("errCode", errCode);
                        responseBundle.putString("errMsg", errMsg);
                        responseBundle.putBoolean("newVersion", false);
                        callback.onResponse(responseBundle);
                    }
                });
    }
}
