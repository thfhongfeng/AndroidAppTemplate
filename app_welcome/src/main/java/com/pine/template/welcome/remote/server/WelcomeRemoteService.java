package com.pine.template.welcome.remote.server;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
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
    private Gson sGson = new Gson().newBuilder().disableHtmlEscaping().create();

    @RouterCommand(CommandName = RouterWelcomeCommand.checkApkUpdate)
    public void checkApkUpdate(@NonNull final Context context, Bundle args,
                               @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        ApkVersionManager.getInstance().checkAndUpdateApk((Activity) context, true, true,
                new ApkVersionManager.IUpdateCallback() {
                    @Override
                    public void onNoNewVersion(String cause) {
                        responseBundle.putString("action", "onNoNewVersion");
                        responseBundle.putBoolean("newVersion", false);
                        callback.onResponse(responseBundle);
                    }

                    @Override
                    public void onNewVersionFound(VersionEntity versionEntity) {
                        responseBundle.putString("action", "onNewVersionFound");
                        responseBundle.putBoolean("newVersion", true);
                        responseBundle.putString("data", sGson.toJson(versionEntity));
                        responseBundle.putBoolean("forceUpdate", versionEntity.isForce());
                        callback.onResponse(responseBundle);
                    }

                    @Override
                    public boolean installApk(VersionEntity versionEntity, File apkFile) {
                        responseBundle.putString("action", "installApk");
                        responseBundle.putString("data", sGson.toJson(versionEntity));
                        responseBundle.putBoolean("forceUpdate", versionEntity.isForce());
                        responseBundle.putString("path", apkFile.getPath());
                        callback.onResponse(responseBundle);
                        return false;
                    }

                    @Override
                    public void onUpdateComplete(VersionEntity versionEntity) {
                        responseBundle.putString("action", "onUpdateComplete");
                        responseBundle.putString("data", sGson.toJson(versionEntity));
                        responseBundle.putBoolean("forceUpdate", versionEntity.isForce());
                        callback.onResponse(responseBundle);
                    }

                    @Override
                    public void onUpdateErr(int errCode, String errMsg,
                                            VersionEntity versionEntity) {
                        responseBundle.putString("action", "onUpdateErr");
                        responseBundle.putString("data", sGson.toJson(versionEntity));
                        responseBundle.putBoolean("forceUpdate", versionEntity.isForce());
                        responseBundle.putInt("errCode", errCode);
                        responseBundle.putString("errMsg", errMsg);
                        callback.onResponse(responseBundle);
                    }
                });
    }
}