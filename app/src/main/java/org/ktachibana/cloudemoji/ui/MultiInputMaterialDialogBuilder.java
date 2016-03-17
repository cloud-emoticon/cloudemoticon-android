package org.ktachibana.cloudemoji.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;


public class MultiInputMaterialDialogBuilder extends MaterialDialog.Builder {
    private Context mContext;
    private List<EditText> mEditTexts;
    private List<InputValidator> mValidators;
    private LinearLayout mRootView;

    public MultiInputMaterialDialogBuilder(@NonNull Context context) {
        super(context);
        mContext = context;
        mEditTexts = new ArrayList<>();
        mValidators = new ArrayList<>();

        mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);

        this.autoDismiss(false);
    }

    public MultiInputMaterialDialogBuilder addInput(CharSequence preFill, CharSequence hint, @NonNull InputValidator validator) {
        EditText newEditText = new EditText(mContext);
        newEditText.setText(preFill);
        newEditText.setHint(hint);

        mEditTexts.add(newEditText);
        mValidators.add(validator);
        mRootView.addView(newEditText);

        this.customView(mRootView, true);
        return this;
    }

    public MultiInputMaterialDialogBuilder addInput(CharSequence preFill, CharSequence hint) {
        return addInput(preFill, hint, new InputValidator() {
            @Override
            public CharSequence validate(CharSequence input) {
                return null;
            }
        });
    }

    public MultiInputMaterialDialogBuilder addInput(@StringRes int preFill, @StringRes int hint, @NonNull InputValidator validator) {
        return addInput(
                preFill == 0 ? null : mContext.getString(preFill),
                hint == 0 ? null : mContext.getString(hint),
                validator
        );
    }

    public MultiInputMaterialDialogBuilder addInput(@StringRes int preFill, @StringRes int hint) {
        return addInput(preFill, hint, new InputValidator() {
            @Override
            public CharSequence validate(CharSequence input) {
                return null;
            }
        });
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

                boolean allInputsValidated = true;
                for (int i = 0; i < inputs.size(); i++) {
                    CharSequence input = inputs.get(i);
                    CharSequence errorMessage = mValidators.get(i).validate(input);
                    boolean validated = errorMessage == null;
                    if (!validated) {
                        mEditTexts.get(i).setError(errorMessage);
                        allInputsValidated = false;
                    }
                }

                callback.onInputs(dialog, inputs, allInputsValidated);

                if (allInputsValidated) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                dialog.dismiss();
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                super.onNeutral(dialog);
                dialog.dismiss();
            }
        });
        return this;
    }

    public interface InputsCallback {
        void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated);
    }

    public interface InputValidator {
        /**
         * Validate text input
         *
         * @param input text to be validated
         * @return error message. Null if validated
         */
        CharSequence validate(CharSequence input);
    }

    public InputValidator NonEmptyInputValidator = new InputValidator() {
        @Override
        public CharSequence validate(CharSequence input) {
            return TextUtils.isEmpty(input) ? mContext.getString(android.R.string.no) : null;
        }
    };
}
