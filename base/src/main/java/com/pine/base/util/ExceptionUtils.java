package com.pine.base.util;

import com.pine.base.R;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.exception.MessageException;
import com.pine.tool.util.AppUtils;
import com.yanzhenjie.nohttp.error.NetworkError;

/**
 * Created by tanghongfeng on 2019/10/15.
 */

public class ExceptionUtils {
    public static String parseException(Exception exception) {
        if (exception instanceof MessageException || exception instanceof BusinessException) {
            return exception.toString();
        } else if (exception instanceof NetworkError) {
            return AppUtils.getApplicationContext().getString(R.string.base_network_err);
        } else {
            return AppUtils.getApplicationContext().getString(R.string.base_server_err);
        }
    }
}
