package org.ktachibana.cloudemoji.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class EmoticonTextView extends TextView {
    private int width = -1;
    private int height = -1;
    private boolean sizeObtained = false;

    public EmoticonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        sizeObtained = true;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (sizeObtained) {

        }
    }
}
