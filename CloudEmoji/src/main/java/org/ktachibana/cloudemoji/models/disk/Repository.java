package org.ktachibana.cloudemoji.models.disk;

import com.orm.SugarRecord;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * POJO class holding a remote repository and its relevant information
 */
public class Repository extends SugarRecord<Repository> implements Constants, Serializable {
    private String url;
    private String alias;
    private int formatType;
    private String fileName;
    private boolean isAvailable;
    private boolean isVisible;

    public Repository() {
    }

    public Repository(String url, String alias) {
        this.url = url;
        this.alias = alias;
        String extension = FilenameUtils.getExtension(url);
        if (extension.equals("xml")) formatType = FORMAT_TYPE_XML;
        if (extension.equals("json")) formatType = FORMAT_TYPE_JSON;
        this.fileName = String.valueOf(url.hashCode()) + "." + extension;
        this.isAvailable = false;
        this.isVisible = false;
    }

    public String getUrl() {
        return url;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public FormatType getFormatType() {
        if (formatType == FORMAT_TYPE_XML) return FormatType.XML;
        else return FormatType.JSON;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public static boolean hasDuplicateUrl(String url) {
        List<Repository> repositories = listAll(Repository.class);
        for (Repository repository : repositories) {
            if (url.equals(repository.getUrl())) {
                return true;
            }
        }
        return false;
    }

    public static enum FormatType {XML, JSON}
}
