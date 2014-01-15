package org.ktachibana.cloudemoji;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

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
    public void onDestroy() {
        super.onDestroy();
        // Hard way to kill the activity completely from background
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
