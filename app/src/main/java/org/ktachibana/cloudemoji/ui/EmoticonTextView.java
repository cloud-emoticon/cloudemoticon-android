package org.ktachibana.cloudemoji.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class EmoticonTextView extends TextView {
    public EmoticonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getText() != null || getText().toString() != null || getText().toString().length() != 0) {
            String string = getText().toString();
            String[] lines = string.split("\\n");
            int longestLineLength = 0;
            for (String line : lines) {
                longestLineLength = Math.max(longestLineLength, line.length());
            }
            float longestLineWidth = longestLineLength * getTextSize();
            if (longestLineWidth > w) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, w / longestLineLength);
            }
        }
    }
}
