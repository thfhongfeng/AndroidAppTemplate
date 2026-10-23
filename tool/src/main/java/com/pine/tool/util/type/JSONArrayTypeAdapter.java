package com.pine.tool.util.type;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class JSONArrayTypeAdapter extends TypeAdapter<JSONArray> {
    @Override
    public void write(JsonWriter out, JSONArray value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.jsonValue(value.toString());
    }

    @Override
    public JSONArray read(JsonReader in) throws IOException {
        try {
            return new JSONArray(new JsonParser().parse(in).toString());
        } catch (JSONException e) {
            return null;
        }
    }
}
