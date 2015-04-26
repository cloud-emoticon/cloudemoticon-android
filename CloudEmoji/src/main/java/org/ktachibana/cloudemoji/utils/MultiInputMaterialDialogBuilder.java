package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;


public class MultiInputMaterialDialogBuilder extends MaterialDialog.Builder {
    private Context mContext;
    private LinearLayout mRootView;
    private InputsCallback mCallback;

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

    public MultiInputMaterialDialogBuilder addInput(@StringRes int title, @StringRes int preFill, @StringRes int hint, int type) {
        return addInput(
                title == 0 ? null : mContext.getString(title),
                preFill == 0 ? null : mContext.getString(preFill),
                hint == 0 ? null : mContext.getString(hint),
                type
        );
    }

    public MultiInputMaterialDialogBuilder inputs(@NonNull InputsCallback callback) {
        mCallback = callback;
        return this;
    }

    public interface InputsCallback {
        void onInput(MaterialDialog dialog, List<CharSequence> inputs);
    }
}
