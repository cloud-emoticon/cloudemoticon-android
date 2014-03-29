package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Base activity for all activities with UI to extend
 * Sets system status bar to translucent blue
 * Sets system navigation bar (if exists) to translucent for Android 4.4+
 * This class uses SystemBarTint from https://github.com/jgilfelt/SystemBarTint
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.holo_blue_light));
    }
}
