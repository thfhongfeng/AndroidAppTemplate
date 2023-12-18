package com.pine.template.base.business.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.base.remote.BaseRouterClient;
import com.pine.tool.util.NetWorkUtils;

import java.util.HashMap;

public class AccountUtils {
    public static HashMap getAccountInfoAndIpParams(@NonNull Context context) {
        AccountBean accountBean = BaseRouterClient.getLoginAccount(context, null);
        HashMap<String, String> params = new HashMap<>();
        if (accountBean != null) {
            params.put("accountId", accountBean.getId());
            params.put("account", accountBean.getAccount());
            params.put("accountType", String.valueOf(accountBean.getAccountType()));
        }
        String ip = NetWorkUtils.getIpAddress();
        if (!TextUtils.isEmpty(ip)) {
            params.put("ip", ip);
        }
        return params;
    }
}
