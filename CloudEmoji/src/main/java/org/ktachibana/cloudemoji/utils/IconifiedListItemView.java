package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IconifiedListItemView extends LinearLayout {
    // Members
    @InjectView(R.id.root)
    LinearLayout mRoot;
    @InjectView(R.id.icon)
    ImageView mImageView;
    @InjectView(R.id.text)
    TextView mTextView;
    // Attributes
    private String mText;
    private Drawable mIcon;

    public IconifiedListItemView(Context context) {
        super(context);
        mText = "Hi";
        mIcon = getResources().getDrawable(android.R.drawable.ic_menu_preferences);
        init();
    }

    public IconifiedListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init();
    }

    public IconifiedListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
        init();
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        // Get attributes
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.IconifiedListItemView,
                0, 0);
        try {
            mText = array.getString(R.styleable.IconifiedListItemView_text);
            mIcon = array.getDrawable(R.styleable.IconifiedListItemView_theIcon);
        } finally {
            array.recycle();
        }
    }

    private void init() {
        // Inflate
        View rootView = inflate(getContext(), R.layout.view_iconified_list_item, this);
        ButterKnife.inject(this, rootView);

        internalSetIcon();
        internalSetText();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        internalSetText();
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
        internalSetIcon();
    }

    private void internalSetText() {
        mTextView.setText(mText);
    }

    private void internalSetIcon() {
        mImageView.setImageDrawable(mIcon);
    }
}
