package org.ktachibana.cloudemoji.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.NotificationHelper;

/**
 * Dummy activity that shows notification after boot up
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read whether to show up and what way to show up
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showAfterBootUp = preferences.getBoolean(Constants.PREF_SHOW_AFTER_BOOT_UP, true);
        String notificationVisibility = preferences.getString(Constants.PREF_NOTIFICATION_VISIBILITY, "both");

        // Do it
        if (showAfterBootUp) {
            NotificationHelper.switchNotificationState(this, notificationVisibility);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // OS way to kill this activity from background tasks
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
