package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.Date;

/**
 * POJO class holding a remote repository and its relevant information
 */
public class Repository extends SugarRecord<Repository> {
    private static final int FORMAT_TYPE_XML = 0;
    private static final int FORMAT_TYPE_JSON = 1;

    private String remoteAddress;
    private String fileName;
    private int formatType;
    private long lastUpdate;

    public Repository(Context context) {
        super(context);
    }

    public Repository(Context context, String remoteAddress, DateTime lastUpdate) {
        super(context);
        this.remoteAddress = remoteAddress;
        this.fileName = FilenameUtils.getName(remoteAddress);
        String extension = FilenameUtils.getExtension(remoteAddress);
        if (extension.equals("xml")) formatType = FORMAT_TYPE_XML;
        if (extension.equals("json")) formatType = FORMAT_TYPE_JSON;
        this.lastUpdate = lastUpdate.getMillis();
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public FormatType getFormatType() {
        if (formatType == FORMAT_TYPE_XML) return FormatType.XML;
        else return FormatType.JSON;
    }

    public DateTime getLastUpdate() {
        return new DateTime(lastUpdate);
    }

    public void setLastUpdate(DateTime lastUpdate) {
        this.lastUpdate = lastUpdate.getMillis();
    }

    public static enum FormatType {XML, JSON}
}
