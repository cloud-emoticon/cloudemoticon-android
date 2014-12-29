package org.ktachibana.cloudemoji.ime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.ktachibana.cloudemoji.R;

public class InputMethod extends InputMethodService {

    private InputMethodManager mManager;
    private Keyboard mKeyboard;
    private KeyboardView mKeyboardView;

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        mKeyboard = new Keyboard(this, R.xml.ime_layout_template, "qwertyuiop", 5, 1);
    }

    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.view_ime, null);
        mKeyboardView.setKeyboard(mKeyboard);
        return mKeyboardView;
    }
}
