package org.ktachibana.cloudemoji;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.EntryAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.ShowSnackBarOnBaseActivityEvent;
import org.ktachibana.cloudemoji.utils.CopyUtils;


/**
 * Base activity for all activities with UI to extend
 * It includes preferences, toolbar, snack bar, event bus, and copy to clipboard
 */
public class BaseActivity extends AppCompatActivity {
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

        // Add subclass view to main view
        View subclassView = getLayoutInflater().inflate(layoutResID, null);
        mainView.removeAllViews();
        mainView.addView(subclassView);
    }

    protected void showSnackBar(String message) {
        Snackbar.make(
            findViewById(R.id.coordinator),
            message,
            Snackbar.LENGTH_SHORT
        ).show();
    }

    protected void showSnackBar(@StringRes int resId) {
        showSnackBar(getString(resId));
    }

    @Subscribe
    public void handle(ShowSnackBarOnBaseActivityEvent event) {
        showSnackBar(event.getMessage());
    }

    @Subscribe
    public void handle(EntryAddedToHistoryEvent event) {
        // Copy to clipboard
        String copied = event.getEntry().getEmoticon();
        CopyUtils.copyToClipboard(this, copied);

        // Show toast
        boolean isCloseAfterCopy = mPreferences.getBoolean(Constants.PREF_CLOSE_AFTER_COPY, true);

        // Show toast if close after copy, otherwise snackbar
        String message = copied + "\n" + getString(R.string.copied);
        if (isCloseAfterCopy) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showSnackBar(message);
        }
    }

    @Subscribe
    public void handle(EmptyEvent e) {

    }
}
