package com.pine.tool.util.type;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class JSONObjectTypeAdapter extends TypeAdapter<JSONObject> {
    @Override
    public void write(JsonWriter out, JSONObject value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.jsonValue(value.toString());
    }

    @Override
    public JSONObject read(JsonReader in) throws IOException {
        try {
            return new JSONObject(new JsonParser().parse(in).toString());
        } catch (JSONException e) {
            return null;
        }
    }
}
