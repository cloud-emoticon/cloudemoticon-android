package org.ktachibana.cloudemoji.utils;

import android.content.Context;

public class CopyUtils {
    public static void copyToClipboard(Context context, String copied) {
        android.content.ClipboardManager clipboard
                = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
        clipboard.setPrimaryClip(clip);
    }
}
