package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * The notorious trash can
 */
public class Utils {
    public static boolean networkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static boolean networkUnavailable(Context context) {
        return !networkAvailable(context);
    }

    public static boolean belowHoneycomb() {
        return Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean aboveHoneycomb() {
        return !belowHoneycomb();
    }

    public static boolean belowJellybean() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean aboveJellybean() {
        return !belowJellybean();
    }
}
