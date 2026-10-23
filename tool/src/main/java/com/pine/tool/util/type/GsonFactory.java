package com.pine.tool.util.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class GsonFactory {
    public static Gson createSimpleGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, new JSONObjectTypeAdapter())
                .registerTypeAdapter(JSONArray.class, new JSONArrayTypeAdapter()).create();
    }

    public static Gson createGson() {
        return new GsonBuilder().disableHtmlEscaping()
                .registerTypeAdapter(JSONObject.class, new JSONObjectTypeAdapter())
                .registerTypeAdapter(JSONArray.class, new JSONArrayTypeAdapter()).create();
    }
}
