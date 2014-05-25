package org.ktachibana.cloudemoji.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.Constants;

/**
 * POJO class holding a remote repository and its relevant information
 */
public class Repository extends SugarRecord<Repository> implements Constants, Parcelable {
    @Ignore
    public static Parcelable.Creator<Repository> CREATOR = new Parcelable.Creator<Repository>() {
        public Repository createFromParcel(Parcel source) {
            return new Repository(source);
        }

        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };
    private String url;
    private String alias;
    private int formatType;
    private String fileName;
    private boolean isAvailable;
    private boolean isVisible;

    public Repository(Context context) {
        super(context);
    }

    public Repository(Context context, String url, String alias) {
        super(context);
        this.url = url;
        this.alias = alias;
        String extension = FilenameUtils.getExtension(url);
        if (extension.equals("xml")) formatType = FORMAT_TYPE_XML;
        if (extension.equals("json")) formatType = FORMAT_TYPE_JSON;
        this.fileName = String.valueOf(url.hashCode()) + "." + extension;
        this.isAvailable = false;
        this.isVisible = false;
    }

    private Repository(Parcel in) {
        super(null);
        this.url = in.readString();
        this.alias = in.readString();
        this.formatType = in.readInt();
        this.fileName = in.readString();
        this.isAvailable = in.readByte() != 0;
        this.isVisible = in.readByte() != 0;
    }

    public String getUrl() {
        return url;
    }

    public String getAlias() {
        return alias;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.alias);
        dest.writeInt(this.formatType);
        dest.writeString(this.fileName);
        dest.writeByte(isAvailable ? (byte) 1 : (byte) 0);
        dest.writeByte(isVisible ? (byte) 1 : (byte) 0);
    }

    public static enum FormatType {XML, JSON}
}
