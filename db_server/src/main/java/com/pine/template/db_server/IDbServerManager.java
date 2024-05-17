package com.pine.template.db_server;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;

import java.util.HashMap;

public interface IDbServerManager {
    Response callCommand(@NonNull Context context, @NonNull RequestBean requestBean,
                         HashMap<String, String> header);
}
