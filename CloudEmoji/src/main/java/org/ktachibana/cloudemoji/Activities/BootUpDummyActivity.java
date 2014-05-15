package org.ktachibana.cloudemoji.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.helpers.NotificationHelper;

/**
 * Dummy activity that shows notification after boot up
 */
public class BootUpDummyActivity extends Activity implements Constants {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showAfterBootUp = preferences.getBoolean(PREF_SHOW_AFTER_BOOT_UP, true);
        String notificationVisibility = preferences.getString(PREF_NOTIFICATION_VISIBILITY, "both");
        if (showAfterBootUp) {
            NotificationHelper.switchNotificationState(this, notificationVisibility);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The hard way to remove this activity from background tasks
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
