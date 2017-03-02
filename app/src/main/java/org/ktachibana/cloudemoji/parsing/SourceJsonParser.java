package org.ktachibana.cloudemoji.parsing;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.models.memory.Source;

import java.io.IOException;
import java.io.Reader;

public class SourceJsonParser {

    public Source parse(String alias, Reader reader) throws IOException {
        String json = IOUtils.toString(reader);
        Source newSource = new Gson().fromJson(json, Source.class);
        newSource.setAlias(alias);
        return newSource;
    }

    public String serialize(Source source) {
        return new Gson().toJson(source);
    }
}
