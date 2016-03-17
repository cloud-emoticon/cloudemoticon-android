package org.ktachibana.cloudemoji.ui;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class NonCancelableProgressMaterialDialogBuilder extends MaterialDialog.Builder {
    public NonCancelableProgressMaterialDialogBuilder(Context context) {
        super(context);
        progress(true, 0);
        cancelable(false);
    }
}
