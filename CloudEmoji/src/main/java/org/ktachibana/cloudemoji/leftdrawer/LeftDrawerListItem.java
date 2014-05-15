package org.ktachibana.cloudemoji.leftdrawer;

import org.ktachibana.cloudemoji.Constants;

public class LeftDrawerListItem implements Constants {
    private String text;
    private int drawable;
    private long id;

    public LeftDrawerListItem(String text, int drawable, long id) {
        this.text = text;
        this.drawable = drawable;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getDrawable() {
        return drawable;
    }

    public long getId() {
        return id;
    }
}
