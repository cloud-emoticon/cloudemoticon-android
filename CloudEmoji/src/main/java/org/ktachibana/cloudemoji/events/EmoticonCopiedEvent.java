package org.ktachibana.cloudemoji.events;

/**
 * An emoticon is copied
 */
public class EmoticonCopiedEvent {
    private String mEmoticon;

    public EmoticonCopiedEvent(String string) {
        this.mEmoticon = string;
    }

    public String getEmoticon() {
        return mEmoticon;
    }
}
