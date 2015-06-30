package org.ktachibana.cloudemoji.parsing;

import org.ktachibana.cloudemoji.models.disk.Repository;

public class SourceParsingException extends Exception {
    private Repository.FormatType mFormatType;

    public SourceParsingException(Repository.FormatType formatType) {
        mFormatType = formatType;
    }

    public Repository.FormatType getFormatType() {
        return mFormatType;
    }
}
