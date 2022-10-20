package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import org.ktachibana.cloudemoji.BaseApplication;

/**
 * The notorious trash can
 */
public class SystemUtils {
    public static boolean networkAvailable() {
        Context context = BaseApplication.context();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static boolean aboveMarshmallow23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean aboveNougat24() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean aboveOreo26() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean aboveS31() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    public static boolean belowTiramisu33() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU;
    }
}
