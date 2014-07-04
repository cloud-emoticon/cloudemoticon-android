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
    // Constants
    private static final int PRIMARY_STYLE_INT = 0;
    private static final int SECONDARY_STYLE_INT = 1;
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
    private Style mStyle;

    public IconifiedListItemView(Context context) {
        super(context);
        mText = "Hi";
        mIcon = getResources().getDrawable(android.R.drawable.ic_menu_preferences);
        mStyle = Style.PRIMARY;
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
            mStyle = intToStyle(array.getInteger(R.styleable.IconifiedListItemView_style, PRIMARY_STYLE_INT));
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
        internalSetStyle();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        internalSetText();
    }

    public Style getStyle() {
        return mStyle;
    }

    public void setStyle(Style style) {
        this.mStyle = style;
        internalSetStyle();
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

    private void internalSetStyle() {
        // Setup style
        float minHeight;
        float paddingLeft;
        float paddingRight;
        int textAppearance;
        boolean allCaps;
        int marginBetweenIconAndText;

        Resources resources = getResources();
        switch (mStyle) {
            case PRIMARY: {
                minHeight = resources.getDimension(R.dimen.listPreferredItemHeightSmall);
                paddingLeft = resources.getDimension(R.dimen.listPreferredItemPaddingLeft);
                paddingRight = resources.getDimension(R.dimen.listPreferredItemPaddingRight);
                textAppearance = android.R.style.TextAppearance_Medium;
                allCaps = false;
                marginBetweenIconAndText = 15;
                break;
            }
            case SECONDARY: {
                minHeight = resources.getDimension(R.dimen.listPreferredSecondaryItemHeightSmall);
                paddingLeft = resources.getDimension(R.dimen.listPreferredSecondaryItemPaddingLeft);
                paddingRight = resources.getDimension(R.dimen.listPreferredSecondaryItemPaddingRight);
                textAppearance = android.R.style.TextAppearance_Small;
                allCaps = true;
                marginBetweenIconAndText = 10;
                break;
            }
            default: {
                minHeight = resources.getDimension(R.dimen.listPreferredItemHeightSmall);
                paddingLeft = resources.getDimension(R.dimen.listPreferredItemPaddingLeft);
                paddingRight = resources.getDimension(R.dimen.listPreferredItemPaddingRight);
                textAppearance = android.R.style.TextAppearance_Medium;
                allCaps = false;
                marginBetweenIconAndText = 15;
            }
        }

        mRoot.setMinimumHeight((int) minHeight);
        mRoot.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);
        mTextView.setTextAppearance(getContext(), textAppearance);
        mTextView.setAllCaps(allCaps);
        LayoutParams params
                = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginBetweenIconAndText, 0, 0, 0);
        mTextView.setLayoutParams(params);
    }

    private Style intToStyle(int intStyle) {
        switch (intStyle) {
            case PRIMARY_STYLE_INT:
                return Style.PRIMARY;
            case SECONDARY_STYLE_INT:
                return Style.SECONDARY;
            default:
                return Style.PRIMARY;
        }
    }

    public enum Style {PRIMARY, SECONDARY}
}
