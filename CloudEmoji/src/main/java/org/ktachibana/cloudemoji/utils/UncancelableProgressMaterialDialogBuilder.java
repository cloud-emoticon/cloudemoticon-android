package org.ktachibana.cloudemoji.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class UncancelableProgressMaterialDialogBuilder extends MaterialDialog.Builder {
    public UncancelableProgressMaterialDialogBuilder(Context context) {
        super(context);
        progress(true, 0);
        cancelable(false);
    }
}
