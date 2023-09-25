package org.ktachibana.cloudemoji.ui;

import android.content.Context;
import android.util.TypedValue;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

public class ScrollableEmoticonMaterialDialogBuilder extends MaterialDialog.Builder {
    private HorizontalScrollView mScrollView;
    private TextView mTextView;

    public ScrollableEmoticonMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        mScrollView = new HorizontalScrollView(context);
        mTextView = new TextView(context);
    }

    public ScrollableEmoticonMaterialDialogBuilder setEmoticon(String emoticon) {
        mTextView.setText(emoticon);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        mScrollView.addView(mTextView);
        mScrollView.setPadding(20, 20, 20, 20);

        super.customView(mScrollView, false);
        return this;
    }
}
