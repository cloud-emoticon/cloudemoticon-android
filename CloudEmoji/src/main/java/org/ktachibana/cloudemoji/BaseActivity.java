package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

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
