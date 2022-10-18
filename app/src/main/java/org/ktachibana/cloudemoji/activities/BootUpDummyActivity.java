package org.ktachibana.cloudemoji.activities;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.NotificationUtils;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Dummy activity that shows notification after boot up
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Show notification according to prefs
        boolean showAfterBootUp = preferences.getBoolean(Constants.PREF_SHOW_AFTER_BOOT_UP, true);
        String notificationVisibility = preferences.getString(Constants.PREF_NOTIFICATION_VISIBILITY, Constants.QUICK_TRIGGER_NOTIFICATION_DEFAULT_VISIBILITY);
        if (showAfterBootUp) {
            if (SystemUtils.aboveTiramisu()) {
                String[] perms = {Manifest.permission.POST_NOTIFICATIONS};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    NotificationUtils.setupNotificationWithPref(this, notificationVisibility);
                }
            } else {
                NotificationUtils.setupNotificationWithPref(this, notificationVisibility);
            }
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
