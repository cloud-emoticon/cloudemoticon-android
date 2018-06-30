package org.ktachibana.cloudemoji.parsing;

import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.database.FormatType;
import org.ktachibana.cloudemoji.database.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SourceReader {
    public Source readSourceFromDatabase(Repository repository) throws SourceParsingException, IOException {
        Source source = null;

        // Get repository file name
        String fileName = repository.getFileName();

        // Read the file from file system
        File file = new File(BaseApplication.Companion.context().getFilesDir(), fileName);
        FileReader fileReader = null;
        try {
            // Read it
            fileReader = new FileReader(file);

            // Parse
            FormatType formatType = repository.getFormatType();
            if (formatType == FormatType.XML) {
                source = new SourceXmlParser().parse(repository.getAlias(), fileReader);
            } else if (formatType == FormatType.JSON) {
                source = new SourceJsonParser().parse(repository.getAlias(), fileReader);
            } else {
                throw new RuntimeException("Unknown format type");
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
