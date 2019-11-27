package com.pine.tool.permission.easy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class AppSettingsDialogHolderActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    public static final String REQUEST_PERMISSIONS_KEY = "request_permission_key";
    public static final String REQUEST_CODE_KEY = "request_code_key";
    private static final int APP_SETTINGS_RC = 7534;
    private AlertDialog mDialog;
    private int mIntentFlags;
    private int mRequestCode;
    private String[] mPermissions;

    public static Intent createShowDialogIntent(Context context, AppSettingsDialog dialog,
                                                int requestCode,
                                                @NonNull String... permissions) {
        Intent intent = new Intent(context, AppSettingsDialogHolderActivity.class);
        intent.putExtra(AppSettingsDialog.EXTRA_APP_SETTINGS, dialog);
        intent.putExtra(REQUEST_PERMISSIONS_KEY, permissions);
        intent.putExtra(REQUEST_CODE_KEY, requestCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettingsDialog appSettingsDialog = AppSettingsDialog.fromIntent(getIntent(), this);
        mIntentFlags = appSettingsDialog.getIntentFlags();
        mDialog = appSettingsDialog.showDialog(this, this);
        mRequestCode = getIntent().getIntExtra(REQUEST_CODE_KEY, -1);
        mPermissions = getIntent().getStringArrayExtra(REQUEST_PERMISSIONS_KEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(mIntentFlags);
            startActivityForResult(intent, APP_SETTINGS_RC);
        } else if (which == Dialog.BUTTON_NEGATIVE) {
            Intent data = new Intent();
            data.putExtra(REQUEST_CODE_KEY, mRequestCode);
            data.putExtra(REQUEST_PERMISSIONS_KEY, mPermissions);
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        } else {
            throw new IllegalStateException("Unknown button type: " + which);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            data = new Intent();
        }
        data.putExtra(REQUEST_PERMISSIONS_KEY, mPermissions);
        data.putExtra(REQUEST_CODE_KEY, mRequestCode);
        setResult(resultCode, data);
        finish();
    }
}
