package org.ktachibana.cloudemoji;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Base activity for all activities with UI to extend
 * It includes preferences, toolbar, snack bar, event bus, and copy to clipboard
 */
public class BaseActivity extends AppCompatActivity implements Constants {
    protected SharedPreferences mPreferences;
    protected Toolbar mToolbar;
    protected EventBus mBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mBus = EventBus.getDefault();
        mBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);

        // Find toolbar and main view
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout mainView = (FrameLayout) findViewById(R.id.mainView);

        // Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add subclass view to main view
        View subclassView = getLayoutInflater().inflate(layoutResID, null);
        mainView.removeAllViews();
        mainView.addView(subclassView);
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
    @TargetApi(11)
    public void onEvent(EntryCopiedAndAddedToHistoryEvent event) {
        String copied = event.getEntry().getEmoticon();

        if (Utils.belowHoneycomb()) {
            android.text.ClipboardManager clipboard
                    = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copied);
        } else {
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
