package org.ktachibana.cloudemoji.parsing;

import com.google.gson.Gson;

import org.ktachibana.cloudemoji.models.memory.Source;

public class SourceJsonParser {

    public Source parse(String json) {
        return new Gson().fromJson(json, Source.class);
    }

    public String serialize(Source source) {
        return new Gson().toJson(source);
    }
}
