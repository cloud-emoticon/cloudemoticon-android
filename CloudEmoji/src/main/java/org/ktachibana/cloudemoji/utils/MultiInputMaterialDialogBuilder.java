package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;


public class MultiInputMaterialDialogBuilder extends MaterialDialog.Builder {
    private Context mContext;
    private List<EditText> mEditTexts;
    private LinearLayout mRootView;

    public MultiInputMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        mContext = context;
        mEditTexts = new ArrayList<>();

        mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
    }

    public MultiInputMaterialDialogBuilder addInput(CharSequence preFill, CharSequence hint) {
        EditText newEditText = new EditText(mContext);
        newEditText.setText(preFill);
        newEditText.setHint(hint);

        mEditTexts.add(newEditText);
        mRootView.addView(newEditText);

        this.customView(mRootView, true);
        return this;
    }

    public MultiInputMaterialDialogBuilder addInput(@StringRes int preFill, @StringRes int hint) {
        return addInput(
                preFill == 0 ? null : mContext.getString(preFill),
                hint == 0 ? null : mContext.getString(hint)
        );
    }

    public MultiInputMaterialDialogBuilder inputs(@NonNull final InputsCallback callback) {
        this.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);

                List<CharSequence> inputs = new ArrayList<>();
                for (EditText editText : mEditTexts) {
                    inputs.add(editText.getText());
                }
                callback.onInput(dialog, inputs);
            }
        });
        return this;
    }

    public interface InputsCallback {
        void onInput(MaterialDialog dialog, List<CharSequence> inputs);
    }
}
