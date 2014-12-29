package org.ktachibana.cloudemoji.ime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Favorite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputMethod extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private InputMethodManager mManager;
    private Keyboard mKeyboard;
    private KeyboardView mKeyboardView;
    private Map<Character, ArrayList<String>> mCandidatesMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        mCandidatesMap = generateCandidatesMap();
        String initials = "";
        for (Character initial : mCandidatesMap.keySet()) {
            initials += initial;
        }

        mKeyboard = new Keyboard(this, R.xml.ime_layout_template, initials, 5, 1);
    }

    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.view_ime, null);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);
        return mKeyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    private Map<Character, ArrayList<String>> generateCandidatesMap() {
        Map<Character, ArrayList<String>> map = new HashMap<Character, ArrayList<String>>();

        List<Favorite> favorites = Favorite.listAll(Favorite.class);
        for (Favorite favorite : favorites) {
            Character initial = favorite.getEmoticon().charAt(0);

            // If map already contains initial, add to existing list
            if (map.containsKey(initial)) {
                map.get(initial).add(favorite.getEmoticon());
            }

            // Otherwise add string with new initial
            else {
                ArrayList<String> newList = new ArrayList<String>();
                newList.add(favorite.getEmoticon());
                map.put(initial, newList);
            }
        }

        return map;
    }
}
