package com.pine.template.db_server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.tool.exception.MessageException;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.TypeConvertUtils;
import com.pine.tool.util.builder.ImageCodeBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DbResponseGenerator {

    public static DbResponse getSuccessUrlBitmapBytesRep(DbRequestBean requestBean,
                                                         HashMap<String, String> cookies,
                                                         String url) {
        Bitmap bitmap = urlToBitmap(url);
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(TypeConvertUtils.toByteArray(bitmap));
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "200");
        return response;
    }

    public static DbResponse getSuccessCodeBitmapBytesRep(DbRequestBean requestBean,
                                                          HashMap<String, String> cookies,
                                                          String data) {
        Bitmap bitmap = ImageCodeBuilder.getInstance().createBitmap(data);
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(TypeConvertUtils.toByteArray(bitmap));
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "200");
        return response;
    }

    public static DbResponse getSuccessJsonRep(DbRequestBean requestBean,
                                               HashMap<String, String> cookies,
                                               String data) {
        String dataContainer;
        if (!TextUtils.isEmpty(data)) {
            dataContainer = "{'success':true, 'code':200, 'message':'','data':" + data + "}";
        } else {
            dataContainer = "{'success':true, 'code':200, 'message':''}";
        }
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "200");
        return response;
    }

    public static DbResponse getLoginFailJsonRep(DbRequestBean requestBean,
                                                 HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':401, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setException(new MessageException(message));
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "401");
        return response;
    }

    public static DbResponse getServerFailJsonRep(DbRequestBean requestBean,
                                                  HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':501, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setException(new Exception(message));
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "501");
        return response;
    }

    public static DbResponse getServerDbOpFailJsonRep(DbRequestBean requestBean,
                                                      HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':502, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setException(new Exception(message));
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "502");
        return response;
    }

    public static DbResponse getExistAccountJsonRep(DbRequestBean requestBean,
                                                    HashMap<String, String> cookies, String message) {
        String dataContainer = "{'success':false, 'code':601, 'message':'" + message + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(true);
        response.setCookies(cookies);
        response.setException(new MessageException(message));
        response.setData(dataContainer);
        response.setTag(requestBean.getModuleTag());
        insertHeaders(response, cookies, "601");
        return response;
    }

    public static DbResponse getNoSuchTableJsonRep(DbRequestBean requestBean,
                                                   HashMap<String, String> cookies) {
        String dataContainer = "{'success':false, 'code':602, 'message':'No table'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new Exception("No table"));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        insertHeaders(response, cookies, "602");
        return response;
    }

    public static DbResponse getBadArgsJsonRep(DbRequestBean requestBean,
                                               HashMap<String, String> cookies) {
        return getBadArgsJsonRep(requestBean, cookies, "Bad args");
    }

    public static DbResponse getBadArgsJsonRep(DbRequestBean requestBean,
                                               HashMap<String, String> cookies, String msg) {
        String dataContainer = "{'success':false, 'code':603, 'message':'Bad args'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(new MessageException(msg));
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        insertHeaders(response, cookies, "603");
        return response;
    }

    public static DbResponse getExceptionJsonRep(DbRequestBean requestBean,
                                                 HashMap<String, String> cookies, Exception e) {
        String dataContainer = "{'success':false, 'code':604, 'message':'" + e.toString() + "'}";
        DbResponse response = new DbResponse();
        response.setSucceed(false);
        response.setCookies(cookies);
        response.setException(e);
        response.setTag(requestBean.getModuleTag());
        response.setData(dataContainer);
        insertHeaders(response, cookies, "604");
        return response;
    }

    private static void insertHeaders(@NonNull DbResponse response, HashMap<String, String> cookies,
                                      String responseCode) {
        if (response == null) {
            return;
        }
        Map<String, List<String>> headers = new HashMap<>();
        if (cookies != null && cookies.size() > 0) {
            List<String> cookieStrList = new ArrayList<>();
            Iterator<Map.Entry<String, String>> iterator = cookies.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                cookieStrList.add(entry.getKey() + "=" + entry.getValue());
            }
            headers.put("set-cookie", cookieStrList);
        }
        List<String> responseCodeStrList = new ArrayList<>();
        responseCodeStrList.add(responseCode);
        headers.put("ResponseCode", responseCodeStrList);
        List<String> serverStrList = new ArrayList<>();
        serverStrList.add("nginx/1.12.2");
        headers.put("Server", serverStrList);
        response.setHeaders(headers);
    }

    private static Bitmap urlToBitmap(final String url) {
        Bitmap bitmap = null;
        URL imageUrl = null;
        try {
            imageUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
