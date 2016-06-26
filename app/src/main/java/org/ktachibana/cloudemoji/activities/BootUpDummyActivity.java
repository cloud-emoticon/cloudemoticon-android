package org.ktachibana.cloudemoji.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.EmoticonHeadUtils;
import org.ktachibana.cloudemoji.utils.NotificationUtils;

/**
 * Dummy activity that shows notification after boot up
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Show notification according to prefs
        Boolean showAfterBootUp = preferences.getBoolean(Constants.PREF_SHOW_AFTER_BOOT_UP, true);
        String notificationVisibility = preferences.getString(Constants.PREF_NOTIFICATION_VISIBILITY, Constants.PERSISTENT_NOTIFICATION_DEFAULT_VISIBILITY);
        if (showAfterBootUp) {
            NotificationUtils.setupNotificationWithPref(this, notificationVisibility);
        }

        // Show emoticon head according to prefs
        boolean overlayGranted = EmoticonHeadUtils.isOverlayAllowed(this);
        boolean emoticonHeadVisibility = preferences.getBoolean(Constants.PREF_EMOTICON_HEAD_VISIBILITY, Constants.EMOTICON_HEAD_DEFAULT_VISIBILITY);
        boolean showEmoticonHeadAfterBootUp = preferences.getBoolean(Constants.PREF_SHOW_EMOTICON_HEAD_AFTER_BOOT_UP, true);
        if (overlayGranted && showEmoticonHeadAfterBootUp) {
            EmoticonHeadUtils.setupEmoticonHeadWithPref(this, emoticonHeadVisibility);
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
