package com.pine.tool.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class EncryptData {
    private String content;
    private String iv;

    public EncryptData(String content, String iv) {
        this.content = content;
        this.iv = iv;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getJsonString() {
        JSONObject object = new JSONObject();
        try {
            object.put("content", content);
            object.put("iv", iv);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static EncryptData fromJsonString(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            return new EncryptData(object.getString("content"), object.getString("iv"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "EncryptData{" +
                "content='" + content + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}
