package org.ktachibana.cloudemoji.adapters;

import android.view.View;

import org.ktachibana.cloudemoji.Constants;

public class DrawerListItem implements Constants {
    private int type;
    private String text;
    private int drawble;

    public DrawerListItem(int type, String text, int drawable) {
        this.type = type;
        this.text = text;
        this.drawble = drawable;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getDrawble() {
        return drawble;
    }
}
