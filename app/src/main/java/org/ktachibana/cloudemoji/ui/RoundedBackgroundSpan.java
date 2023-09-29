package org.ktachibana.cloudemoji.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

/**
 * https://stackoverflow.com/questions/27198155/adding-a-padding-margin-to-a-spannable
 */
public class RoundedBackgroundSpan extends ReplacementSpan {
    private final int mBackgroundColor;
    private final int mTextColor;
    private final int mPaddingLeft;
    private final int mPaddingRight;
    private final int mMarginLeft;
    private final int mMarginRight;

    /**
     * Add rounded background for text in TextView.
     * @param backgroundColor background color
     * @param textColor       text color
     * @param paddingLeft     padding left(including background)
     * @param paddingRight    padding right(including background)
     * @param marginLeft      margin left(not including background)
     * @param marginRight     margin right(not including background)
     */
    public RoundedBackgroundSpan(int backgroundColor, int textColor,
                                 int paddingLeft,
                                 int paddingRight,
                                 int marginLeft,
                                 int marginRight) {
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mPaddingLeft = paddingLeft;
        mPaddingRight = paddingRight;
        mMarginLeft = marginLeft;
        mMarginRight = marginRight;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        return (int) (mMarginLeft + mPaddingLeft +
                paint.measureText(text.subSequence(start, end).toString()) +
                mPaddingRight  + mMarginRight);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, Paint paint) {
        float width = paint.measureText(text.subSequence(start, end).toString());
        RectF rect = new RectF(x + mMarginLeft, top
                - paint.getFontMetricsInt().top + paint.getFontMetricsInt().ascent
                , x + width + mMarginLeft + mPaddingLeft + mPaddingRight, bottom);
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, 5, 5, paint);
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x + mMarginLeft + mPaddingLeft,
                y - paint.getFontMetricsInt().descent / 2, paint);
    }
}
