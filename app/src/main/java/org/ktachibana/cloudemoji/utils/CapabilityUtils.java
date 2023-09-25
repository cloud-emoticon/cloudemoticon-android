package org.ktachibana.cloudemoji.utils;

import android.os.Build;

public class CapabilityUtils {
    public static boolean personalDictionaryUnavailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean accessibilitySetTextAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean cannotGetActiveNotifications() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    public static boolean nowOnTapAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean notQuickTriggerNotificationLegacyVisibility() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean needNotificationChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean needPendingIntentMutability() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    public static boolean doNotNeedRuntimeNotificationPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU;
    }
}
