package org.ktachibana.cloudemoji.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class EmoticonHeadUtils {
    public static void setupEmoticonHeadWithPref(Context context, boolean emoticonHeadVisibility) {
        if (emoticonHeadVisibility) {
            context.startService(new Intent(context, EmoticonHeadService.class));
        } else {
            context.stopService(new Intent(context, EmoticonHeadService.class));
        }
    }

    @TargetApi(23)
    public static boolean isOverlayAllowed(Context context) {
        return !SystemUtils.aboveMarshmallow() || Settings.canDrawOverlays(context);
    }
}
