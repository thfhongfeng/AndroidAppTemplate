package com.pine.tool.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class UrlUtils {
    /**
     * 获取url 中参数
     *
     * @param url
     * @return
     */
    public static HashMap<String, String> getParameters(String url) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (url == null || "".equals(url.trim())) {
            return params;
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String[] split = url.split("[?]");
        if (split.length == 2 && !"".equals(split[1].trim())) {
            String[] parameters = split[1].split("&");
            if (parameters != null && parameters.length != 0) {
                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i] != null
                            && parameters[i].trim().contains("=")) {
                        String[] split2 = parameters[i].split("=");
                        //split2可能为1，可能为2
                        if (split2.length == 1) {
                            //有这个参数但是是空的
                            params.put(split2[0], "");
                        } else if (split2.length == 2) {
                            if (!"".equals(split2[0].trim())) {
                                params.put(split2[0], split2[1]);
                            }
                        }
                    }
                }
            }
        }
        return params;
    }

    /**
     * 获取url中的指定参数  判断是否使用返回js交互
     *
     * @param url
     * @param name
     * @return
     */
    public static String getValueByNameFromUrl(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }

    /**
     * 取url中"<>"中数据
     *
     * @param redirectUrl
     * @return
     */
    public static String getClassNameFromRedirectUrl(String redirectUrl) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(redirectUrl);
        try {//如果字符串中没有尖括号，.find方法会抛出数组越界异常
            if (matcher.find(1)) {
                return matcher.group(1);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * URLEncoder编码
     */
    public static String encodedUrl(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            return null;
        }
    }

    /**
     * URLDecoder解码
     */
    public static String decoderUrl(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String url = new String(paramString.getBytes(), "UTF-8");
            url = URLDecoder.decode(url, "UTF-8");
            return url;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static boolean urlEquals(String url1, String url2) {
        url1 = url1 == null ? "" : decoderUrl(url1);
        url2 = url2 == null ? "" : decoderUrl(url2);
        if (url1 == null || url2 == null) {
            return false;
        }
        return TextUtils.equals(url1, url2);
    }
}
