package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;


public class MultiInputMaterialDialogBuilder extends MaterialDialog.Builder {
    private Context mContext;
    private LinearLayout mRootView;

    public MultiInputMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        mContext = context;

        mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
    }

    public MultiInputMaterialDialogBuilder addInput(CharSequence title, CharSequence preFill, CharSequence hint, int type) {
        EditText newEditText = new EditText(mContext);
        newEditText.setText(preFill);
        newEditText.setHint(hint);

        mRootView.addView(newEditText);
        this.customView(mRootView, true);
        return this;
    }

    public MultiInputMaterialDialogBuilder addInput(int title, int preFill, int hint, int type) {
        return addInput(
                title == 0 ? null : mContext.getString(title),
                preFill == 0 ? null : mContext.getString(preFill),
                hint == 0 ? null : mContext.getString(hint),
                type
        );
    }
}
