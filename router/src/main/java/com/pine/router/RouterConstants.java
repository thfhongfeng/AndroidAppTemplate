package com.pine.router;

import com.pine.config.Constants;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public interface RouterConstants extends Constants {
    String TYPE_UI_COMMAND = "ui_command";
    String TYPE_DATA_COMMAND = "data_command";
    String TYPE_OP_COMMAND = "op_command";

    String ON_SUCCEED = "onSucceed";
    String ON_EXCEPTION = "onException";
    String ON_FAILED = "onFailed";
}
