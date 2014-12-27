package org.ktachibana.cloudemoji.ime;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.InputMethodManager;

public class InputMethod extends InputMethodService {

    private InputMethodManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        // Initialize favorites keyboard

    }
}
