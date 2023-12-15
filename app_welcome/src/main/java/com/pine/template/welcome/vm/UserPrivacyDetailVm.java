package com.pine.template.welcome.vm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pine.template.welcome.WelUrlConstants;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

/**
 * Created by tanghongfeng on 2019/10/28.
 */

public class UserPrivacyDetailVm extends ViewModel {
    public MutableLiveData<Integer> mPrivacyTypeData = new MutableLiveData<>();
    public String mH5Url;

    @Override
    public boolean parseIntentData(Context activity, @NonNull Bundle bundle) {
        int privacyType = bundle.getInt("privacyType", -1);
        if (privacyType != 1 && privacyType != 2) {
            finishUi();
            return true;
        }
        mPrivacyTypeData.setValue(privacyType);
        if (privacyType == 1) {
            mH5Url = WelUrlConstants.H5_PRIVACY_USER();
        } else if (privacyType == 2) {
            mH5Url = WelUrlConstants.PRIVACY_POLICY();
        }
        return false;
    }
}