package com.pine.template.db_server.sqlite.server;

import static com.pine.template.db_server.DbConstants.FILE_INFO_TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.db_server.DbResponseGenerator;
import com.pine.template.db_server.sqlite.SQLiteDbHelper;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;
import com.pine.tool.request.UploadRequestBean;
import com.pine.tool.util.FileUtils;
import com.pine.tool.util.PathUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteFileServer extends SQLiteBaseServer {

    public static Response uploadSingleFile(@NonNull Context context, @NonNull RequestBean requestBean,
                                            @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            List<UploadRequestBean.FileBean> fileBeanList = ((UploadRequestBean) requestBean).getUploadFileList();
            Map<String, String> requestParams = requestBean.getParams();
            if (fileBeanList != null && fileBeanList.size() == 1) {
                ContentValues contentValues = new ContentValues();
                UploadRequestBean.FileBean fileBean = fileBeanList.get(0);
                String fileName = fileBean.getFileName();
                String filePath = fileBean.getFile().getAbsolutePath();
                if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(filePath)) {
                    return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                }
                String targetFileName = System.currentTimeMillis() + "_" + fileName;
                String targetFilePath = PathUtils.getExternalAppCachePath() + "/faceStorage/" + targetFileName;

                contentValues.put("fileName", targetFileName);
                contentValues.put("filePath", targetFilePath);
                if (!TextUtils.isEmpty(requestParams.get("bizType"))) {
                    contentValues.put("bizType", requestParams.get("bizType"));
                } else {
                    contentValues.put("bizType", "1");
                }
                if (!TextUtils.isEmpty(requestParams.get("orderNum"))) {
                    contentValues.put("orderNum", requestParams.get("orderNum"));
                } else {
                    contentValues.put("orderNum", "1");
                }
                if (!TextUtils.isEmpty(requestParams.get("descr"))) {
                    contentValues.put("descr", requestParams.get("descr"));
                }
                if (!TextUtils.isEmpty(requestParams.get("fileType"))) {
                    contentValues.put("fileType", requestParams.get("fileType"));
                } else {
                    contentValues.put("fileType", fileName.substring(fileName.lastIndexOf("\\.") + 1));
                }
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                long id = insert(db, FILE_INFO_TABLE_NAME, "filePath", contentValues);
                if (id == -1) {
                    return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                } else {
                    FileUtils.copyFile(filePath, targetFilePath);
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies,
                            "{'fileUrl':'" + targetFilePath + "','fileName':'" + targetFileName + "'}");
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

    public static Response uploadMultiFile(@NonNull Context context, @NonNull RequestBean requestBean,
                                           @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            List<UploadRequestBean.FileBean> fileBeanList = ((UploadRequestBean) requestBean).getUploadFileList();
            Map<String, String> requestParams = requestBean.getParams();
            if (fileBeanList != null && fileBeanList.size() > 0) {
                String filePaths = "";
                String fileNames = "";
                boolean isSuccess = true;
                db.beginTransaction();
                for (int i = 0; i < fileBeanList.size(); i++) {
                    ContentValues contentValues = new ContentValues();
                    UploadRequestBean.FileBean fileBean = fileBeanList.get(0);
                    String fileName = fileBean.getFileName();
                    String filePath = fileBean.getFile().getAbsolutePath();
                    if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(filePath)) {
                        return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                    }
                    String targetFileName = System.currentTimeMillis() + "_" + fileName;
                    String targetFilePath = PathUtils.getExternalAppCachePath() + "/faceStorage/" + targetFileName;
                    contentValues.put("fileName", targetFileName);
                    contentValues.put("filePath", targetFilePath);
                    if (!TextUtils.isEmpty(requestParams.get("bizType"))) {
                        contentValues.put("bizType", requestParams.get("bizType"));
                    } else {
                        contentValues.put("bizType", "1");
                    }
                    if (!TextUtils.isEmpty(requestParams.get("orderNum"))) {
                        contentValues.put("orderNum", requestParams.get("orderNum"));
                    } else {
                        contentValues.put("orderNum", "1");
                    }
                    if (!TextUtils.isEmpty(requestParams.get("descr"))) {
                        contentValues.put("descr", requestParams.get("descr"));
                    }
                    if (!TextUtils.isEmpty(requestParams.get("fileType"))) {
                        contentValues.put("fileType", requestParams.get("fileType"));
                    } else {
                        contentValues.put("fileType", fileName.substring(fileName.lastIndexOf("\\.") + 1));
                    }
                    contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    long id = insert(db, FILE_INFO_TABLE_NAME, "filePath", contentValues);
                    if (id == -1) {
                        isSuccess = false;
                        break;
                    }
                    filePaths += targetFilePath + ",";
                    fileNames += targetFileName + ",";
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
