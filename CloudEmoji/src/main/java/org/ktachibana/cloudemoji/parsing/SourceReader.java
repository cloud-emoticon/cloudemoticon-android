package org.ktachibana.cloudemoji.parsing;

import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseApplication;
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
        File file = new File(BaseApplication.context().getFilesDir(), fileName);
        FileReader fileReader = null;
        try {
            // Read it
            fileReader = new FileReader(file);

            // Parse
            Repository.FormatType formatType = repository.getFormatType();
            if (formatType == Repository.FormatType.XML) {
                source = new SourceXmlParser().parse(alias, fileReader);
            } else if (formatType == Repository.FormatType.JSON) {
                source = new SourceJsonParser().parse(IOUtils.toString(fileReader));
            }
        } catch (XmlPullParserException e) {
            throw new SourceParsingException(Repository.FormatType.XML);
        } catch (JsonParseException e) {
            throw new SourceParsingException(Repository.FormatType.JSON);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }

        return source;
    }
}
