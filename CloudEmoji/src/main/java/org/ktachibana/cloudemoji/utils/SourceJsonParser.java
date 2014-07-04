package org.ktachibana.cloudemoji.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.ktachibana.cloudemoji.models.Source;

import java.io.IOException;
import java.io.Reader;

public class SourceJsonParser {

    public Source parse(Reader reader) throws JSONException, IOException {
        return new Gson().fromJson(reader, Source.class);
    }

    public String serialize(Source source) {
        return new Gson().toJson(source);
    }
}
