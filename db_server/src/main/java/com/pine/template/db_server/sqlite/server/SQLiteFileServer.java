package com.pine.template.db_server.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.pine.template.db_server.DbResponseGenerator;
import com.pine.template.db_server.sqlite.SQLiteDbHelper;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pine.template.db_server.DbConstants.FILE_INFO_TABLE_NAME;

public class SQLiteFileServer extends SQLiteBaseServer {

    public static DbResponse uploadSingleFile(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                              @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            List<DbRequestBean.FileBean> fileBeanList = requestBean.getUploadFileList();
            Map<String, String> requestParams = requestBean.getParams();
            if (fileBeanList != null && fileBeanList.size() == 1) {
                ContentValues contentValues = new ContentValues();
                String fileName = fileBeanList.get(0).getFileName();
                String filePath = fileBeanList.get(0).getFile().getAbsolutePath();
                contentValues.put("fileName", fileName);
                contentValues.put("filePath", filePath);
                contentValues.put("bizType", requestParams.get("bizType"));
                contentValues.put("orderNum", requestParams.get("orderNum"));
                contentValues.put("descr", requestParams.get("descr"));
                contentValues.put("fileType", requestParams.get("fileType"));
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                long id = insert(db, FILE_INFO_TABLE_NAME, "filePath", contentValues);
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
            if (fileBeanList != null && fileBeanList.size() > 0) {
                String filePaths = "";
                String fileNames = "";
                boolean isSuccess = true;
                db.beginTransaction();
                for (int i = 0; i < fileBeanList.size(); i++) {
                    ContentValues contentValues = new ContentValues();
                    String fileName = fileBeanList.get(i).getFileName();
                    String filePath = fileBeanList.get(i).getFile().getAbsolutePath();
                    contentValues.put("fileName", fileName);
                    contentValues.put("filePath", filePath);
                    contentValues.put("bizType", requestParams.get("bizType"));
                    contentValues.put("orderNum", requestParams.get("orderNum"));
                    contentValues.put("descr", requestParams.get("descr"));
                    contentValues.put("fileType", requestParams.get("fileType"));
                    contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    long id = insert(db, FILE_INFO_TABLE_NAME, "filePath", contentValues);
                    if (id == -1) {
                        isSuccess = false;
                        break;
                    }
                    filePaths += filePath + ",";
                    fileNames += fileNames + ",";
                }
                if (isSuccess) {
                    db.setTransactionSuccessful();
                }
                db.endTransaction();
                if (isSuccess) {
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies,
                            "{'fileUrls':'" + filePaths.substring(0, filePaths.length() - 1) +
                                    "','fileNames':'" + fileNames.substring(0, fileNames.length() - 1) + "'}");
                } else {
                    return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
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
