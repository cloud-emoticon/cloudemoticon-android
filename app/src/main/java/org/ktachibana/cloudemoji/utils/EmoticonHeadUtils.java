package org.ktachibana.cloudemoji.utils;

import android.content.Context;
import android.content.Intent;

public class EmoticonHeadUtils {
    public static void setupEmoticonHeadWithPref(Context context) {
        context.startService(new Intent(context, EmoticonHeadService.class));
    }
}
