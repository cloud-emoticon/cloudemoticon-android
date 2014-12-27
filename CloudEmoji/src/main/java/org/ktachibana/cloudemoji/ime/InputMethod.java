package org.ktachibana.cloudemoji.ime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.ktachibana.cloudemoji.R;

public class InputMethod extends InputMethodService {

    private InputMethodManager mManager;
    private Keyboard mFavoritesKeyboard;
    private KeyboardView mKeyboardView;

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        CharSequence testChars = "qwertyuiopasdfghjklzxcvbnm";
        mFavoritesKeyboard = new Keyboard(this, R.xml.ime_layout_template, testChars, 3, 10);
    }

    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.view_ime, null);
        mKeyboardView.setKeyboard(mFavoritesKeyboard);

        return mKeyboardView;
    }
}
