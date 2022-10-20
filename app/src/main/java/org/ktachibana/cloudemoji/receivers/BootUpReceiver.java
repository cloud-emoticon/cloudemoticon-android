package org.ktachibana.cloudemoji.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.NotificationUtils;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Shows notification when booted up
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Show notification according to prefs
        boolean showAfterBootUp = preferences.getBoolean(Constants.PREF_SHOW_AFTER_BOOT_UP, true);
        if (!showAfterBootUp) {
            return;
        }

        if (SystemUtils.belowTiramisu33()) {
            NotificationUtils.setupNotification(context, null);
            return;
        }
        String[] perms = {Manifest.permission.POST_NOTIFICATIONS};
        if (EasyPermissions.hasPermissions(context, perms)) {
            NotificationUtils.setupNotification(context, null);
        }
    }
}
