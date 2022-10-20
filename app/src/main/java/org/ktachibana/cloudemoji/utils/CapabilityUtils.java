package org.ktachibana.cloudemoji.utils;

import android.os.Build;

public class CapabilityUtils {
    public static boolean personalDictionaryUnavailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean cannotGetActiveNotifications() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    public static boolean nowOnTapAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
