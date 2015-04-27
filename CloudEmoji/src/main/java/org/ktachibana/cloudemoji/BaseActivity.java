package org.ktachibana.cloudemoji;

import android.support.v7.app.AppCompatActivity;

import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Base activity for all activities with UI to extend
 */
public class BaseActivity extends AppCompatActivity {
    protected void showSnackBar(String message) {
        new SnackBar.Builder(this)
                .withMessage(message)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    protected void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }
}
