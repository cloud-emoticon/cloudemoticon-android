package org.ktachibana.cloudemoji.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.ktachibana.cloudemoji.helpers.NotificationHelper;

/**
 * Shows notification after boot up according to user preference
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean showAfterBootUp = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.PREF_SHOW_AFTER_BOOT_UP, true);
        if (showAfterBootUp) {
            String notificationVisibility = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.PREF_NOTIFICATION_VISIBILITY, "both");
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
