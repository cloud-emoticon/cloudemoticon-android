package org.ktachibana.cloudemoji.parsing;

import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SourceReader {
    public Source readSourceFromDatabaseId(String alias, long id) throws SourceParsingException, IOException {
        Source source = null;

        // Get repository file name
        final Repository repository = Repository.findById(Repository.class, id);
        String fileName = repository.getFileName();

        // Read the file from file system
        File file = new File(BaseApplication.Companion.context().getFilesDir(), fileName);
        FileReader fileReader = null;
        try {
            // Read it
            fileReader = new FileReader(file);

            // Parse
            @Constants.FormatType int formatType = repository.getFormatType();
            if (formatType == Constants.FORMAT_TYPE_XML) {
                source = new SourceXmlParser().parse(alias, fileReader);
            } else if (formatType == Constants.FORMAT_TYPE_JSON) {
                source = new SourceJsonParser().parse(alias, fileReader);
            }
        } catch (XmlPullParserException e) {
            throw new SourceParsingException(Constants.FORMAT_TYPE_XML);
        } catch (JsonParseException e) {
            throw new SourceParsingException(Constants.FORMAT_TYPE_JSON);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }

        return source;
    }
}
