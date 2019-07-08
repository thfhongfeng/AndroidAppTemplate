package com.pine.db_server.sqlite.server;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pine.db_server.sqlite.DbResponseGenerator;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pine.db_server.DbConstants.FILE_INFO_TABLE_NAME;

public class SQLiteFileServer extends SQLiteBaseServer {

    public static DbResponse uploadSingleFile(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                              @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            List<DbRequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
            Map<String, String> requestParams = requestBean.getParams();
            if (fileBeanList != null && fileBeanList.size() == 1) {
                String fileName = fileBeanList.get(0).getFileName();
                String filePath = fileBeanList.get(0).getFile().getAbsolutePath();
                requestParams.put("fileName", fileName);
                requestParams.put("filePath", filePath);
                long id = insert(db, FILE_INFO_TABLE_NAME, requestParams);
                if (id == -1) {
                    return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                } else {
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies,
                            "{'fileUrl':'" + filePath + "','fileName':'" + fileName + "'}");
                }
            } else {
                return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse uploadMultiFile(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                             @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            List<DbRequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
            Map<String, String> requestParams = requestBean.getParams();
            if (fileBeanList != null && fileBeanList.size() == 1) {
                String fileName = fileBeanList.get(0).getFileName();
                String filePath = fileBeanList.get(0).getFile().getAbsolutePath();
                requestParams.put("fileName", fileName);
                requestParams.put("filePath", filePath);
                long id = insert(db, FILE_INFO_TABLE_NAME, requestParams);
                if (id == -1) {
                    return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                } else {
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies,
                            "{'fileUrls':'" + filePath + "','fileNames':'" + fileName + "'}");
                }
            } else {
                return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }
}
