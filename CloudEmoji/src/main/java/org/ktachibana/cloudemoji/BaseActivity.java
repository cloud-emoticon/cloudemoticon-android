package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Base activity for all activities with UI to extend
 * Sets system status bar to translucent blue and navigation bar to translucent for Android 4.4+
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
