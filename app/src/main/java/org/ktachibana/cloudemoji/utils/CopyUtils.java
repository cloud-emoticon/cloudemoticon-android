package org.ktachibana.cloudemoji.utils;

import android.annotation.TargetApi;
import android.content.Context;

public class CopyUtils {
    @TargetApi(11)
    public static void copyToClipboard(Context context, String copied) {
        if (SystemUtils.belowHoneycomb()) {
            android.text.ClipboardManager clipboard
                    = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copied);
        } else {
            android.content.ClipboardManager clipboard
                    = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
            clipboard.setPrimaryClip(clip);
        }
    }
}
