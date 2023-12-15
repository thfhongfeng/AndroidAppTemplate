package com.pine.tool.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.pine.tool.BuildConfig;

import java.io.File;

public class InstallUtil {
    private static final String TAG = "InstallUtil";

    private static final String EXTRA_SILENT_INSTALL = "silent_install";

    public static String getFileProviderAuthority(Context context) {
        return BuildConfig.FILE_PROVIDER_AUTHORITY;
    }

    public static boolean install(Context context, String path, boolean silent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return installO(context, path, silent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return installN(context, path, silent);
        } else {
            return installOther(context, path, silent);
        }
    }

    public static boolean installApk(Context context, String path, boolean silent) {
        return install(context, path, silent);
    }

    /**
     * android1.x-6.x
     */
    private static boolean installOther(Context context, String path, boolean silent) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_SILENT_INSTALL, silent);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * android7.x
     */
    private static boolean installN(Context context, String path, boolean silent) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), new File(path));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.putExtra(EXTRA_SILENT_INSTALL, silent);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean installO(Context context, String path, boolean silent) {
        return installN(context, path, silent);
    }

    /**
     * 卸载apk
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }
}
