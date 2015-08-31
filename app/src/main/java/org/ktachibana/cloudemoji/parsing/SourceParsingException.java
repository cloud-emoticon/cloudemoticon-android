package org.ktachibana.cloudemoji.parsing;

import org.ktachibana.cloudemoji.Constants;

public class SourceParsingException extends Exception {
    @Constants.FormatType
    private int mFormatType;

    public SourceParsingException(@Constants.FormatType int formatType) {
        mFormatType = formatType;
    }

    @Constants.FormatType
    public int getFormatType() {
        return mFormatType;
    }
}
