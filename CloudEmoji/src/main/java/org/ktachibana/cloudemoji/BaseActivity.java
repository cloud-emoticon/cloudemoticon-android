package org.ktachibana.cloudemoji;
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
        tintManager.setTintColor(getResources().getColor(R.color.holo_blue_light));
    }

    @Override
    protected void onStart() {
        super.onStart();
        View rootView = findViewById(android.R.id.content);

        // Get height of status bar
        int statusBarHeight = getResources()
                .getDimensionPixelSize(getResources()
                        .getIdentifier("status_bar_height", "dimen", "android"));

        // Get height of action bar
        int actionbarHeight = getActionBarHeight();

        // Pad the root view on the top to circumvent
        // part of views on the top being cut out by status bar and action bar
        int top = statusBarHeight + actionbarHeight;
        rootView.setPadding(0, top, 0, 0);
    }

    /**
     * Get the height of action bar in pixels
     *
     * @return height of action bar
     */
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue value = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, value,true))
            {
                actionBarHeight= TypedValue
                        .complexToDimensionPixelSize(value.data, getResources().getDisplayMetrics());
            }
        }
        else
        {
            actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(value.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
