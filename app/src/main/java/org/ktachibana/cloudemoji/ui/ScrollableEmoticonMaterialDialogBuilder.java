package org.ktachibana.cloudemoji.ui;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

public class ScrollableEmoticonMaterialDialogBuilder extends MaterialDialog.Builder {
    private Context mContext;
    private HorizontalScrollView mScrollView;
    private TextView mTextView;

    public ScrollableEmoticonMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        mContext = context;
        mScrollView = new HorizontalScrollView(context);
        mTextView = new TextView(context);
    }

    public ScrollableEmoticonMaterialDialogBuilder setEmoticon(String emoticon) {
        mTextView.setText(emoticon);
        mScrollView.addView(mTextView);

        super.customView(mScrollView, false);
        return this;
    }
}
