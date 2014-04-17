package org.ktachibana.cloudemoji;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Base activity for all activities with UI to extend
 * Sets system status bar to translucent blue and navigation bar to translucent for Android 4.4+
 * This class uses SystemBarTint from https://github.com/jgilfelt/SystemBarTint
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.holo_blue_light));
        tintManager.setNavigationBarTintColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If version is higher than 19, then use padding oppset
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View rootView = findViewById(android.R.id.content);

            // Get height of status bar
            int statusBarHeight = getResources()
                    .getDimensionPixelSize(getResources()
                            .getIdentifier("status_bar_height", "dimen", "android"));

            // Get height of action bar
            TypedValue value = new TypedValue();
            int actionBarHeight = 0;
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(value.data, getResources().getDisplayMetrics());
            }

            // Pad the root view on the top to circumvent
            // part of views on the top being cut out by status bar and action bar
            int top = statusBarHeight + actionBarHeight;
            rootView.setPadding(0, top, 0, 0);
        }
    }
}
