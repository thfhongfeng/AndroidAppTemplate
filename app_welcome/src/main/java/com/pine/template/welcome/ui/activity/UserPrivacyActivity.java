package com.pine.template.welcome.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.welcome.R;
import com.pine.template.welcome.WelcomeKeyConstants;
import com.pine.template.welcome.databinding.UserPrivacyActivityBinding;
import com.pine.template.welcome.vm.UserPrivacyVm;
import com.pine.tool.util.SharePreferenceUtils;

public class UserPrivacyActivity extends BaseMvvmFullScreenActivity<UserPrivacyActivityBinding, UserPrivacyVm> {

    private Dialog mTipDialog;

    private int mTipDialogShowCount = 0;

    protected void beforeSuperOnCreate(Bundle savedInstanceState) {
        setTheme(R.style.BaseFullActivityTheme);
    }

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        boolean userPrivacyAgree = SharePreferenceUtils.readBooleanFromConfig(WelcomeKeyConstants.USER_PRIVACY_AGREE, false);
        // 是否需要用户隐私授权协议签署界面
        boolean enableUserPrivacyAssign = ConfigSwitcherServer.isEnable(BuildConfigKey.ENABLE_USER_PRIVACY_ASSIGN, false);
        if (userPrivacyAgree || !enableUserPrivacyAssign) {
            goLoadingActivity();
        }
        return !isTaskRoot() || userPrivacyAgree || !enableUserPrivacyAssign;
    }

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {

    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_user_privacy;
    }

    @Override
    protected void init(Bundle onCreateSavedInstanceState) {
        mBinding.setPresenter(new Presenter());

        Spanned tipText = Html.fromHtml(getResources().getString(R.string.wel_user_privacy_tip));
        mBinding.tipTv.setMovementMethod(LinkMovementMethod.getInstance());
        URLSpan[] urlSpan = tipText.getSpans(0, tipText.length(), URLSpan.class);
        SpannableStringBuilder stylesBuilder = new SpannableStringBuilder(tipText);
        stylesBuilder.clearSpans(); // should clear old spans
        for (URLSpan url : urlSpan) {
            TextViewURLSpan myURLSpan = new TextViewURLSpan(url.getURL());
            stylesBuilder.setSpan(myURLSpan, tipText.getSpanStart(url),
                    tipText.getSpanEnd(url), tipText.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mBinding.tipTv.setHighlightColor(getResources().getColor(android.R.color.transparent));
        mBinding.tipTv.setText(stylesBuilder);

        mTipDialogShowCount = 0;
    }

    private void showTipDialog() {
        if (mTipDialogShowCount > 0) {
            finish();
            return;
        }
        mTipDialogShowCount++;
        if (mTipDialog == null) {
            mTipDialog = DialogUtils.showTipDialog(this, "", getString(R.string.wel_user_privacy_tip_dialog_content), true);
        }
        mTipDialog.show();
    }

    @Override
    public void onDestroy() {
        if (mTipDialog != null && mTipDialog.isShowing()) {
            mTipDialog.dismiss();
        }
        mTipDialog = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                showTipDialog();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    public class Presenter {
        public void onAgree(View view) {
            SharePreferenceUtils.saveToConfig(WelcomeKeyConstants.USER_PRIVACY_AGREE, true);
            goLoadingActivity();
            finish();
        }

        public void onDisagree(View view) {
            showTipDialog();
        }
    }

    private class TextViewURLSpan extends ClickableSpan {
        private String clickString;

        public TextViewURLSpan(String clickString) {
            this.clickString = clickString;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.dark_green));
            ds.setUnderlineText(false); //去掉下划线
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(UserPrivacyActivity.this, UserPrivacyDetailActionBarActivity.class);
            if (clickString.equals("privacy_user")) {
                // 查看用户协议
                intent.putExtra("privacyType", 1);
                startActivity(intent);
            } else if (clickString.equals("privacy_policy")) {
                // 查看隐私政策
                intent.putExtra("privacyType", 2);
                startActivity(intent);
            }
        }
    }
}
