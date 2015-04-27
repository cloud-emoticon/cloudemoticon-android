package org.ktachibana.cloudemoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;

/**
 * Base activity for all activities with UI to extend
 */
public class BaseActivity extends AppCompatActivity implements Constants {
    protected SharedPreferences mPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    protected void showSnackBar(String message) {
        new SnackBar.Builder(this)
                .withMessage(message)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    protected void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }

    /**
     * Listens for any string copied and send it to clipboard
     *
     * @param event string copied event
     */
    @SuppressWarnings("deprecation")
    public void onEvent(EntryCopiedAndAddedToHistoryEvent event) {
        String copied = event.getEntry().getEmoticon();

        int SDK = Build.VERSION.SDK_INT;
        // Below 3.0
        if (SDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard
                    = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copied);
        }

        // Above 3.0
        else {
            android.content.ClipboardManager clipboard
                    = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
            clipboard.setPrimaryClip(clip);
        }

        // Show toast
        boolean isCloseAfterCopy = mPreferences.getBoolean(PREF_CLOSE_AFTER_COPY, true);

        // Show toast if close after copy, otherwise snackbar
        String message = copied + "\n" + getString(R.string.copied);
        if (isCloseAfterCopy) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showSnackBar(message);
        }
    }
}
