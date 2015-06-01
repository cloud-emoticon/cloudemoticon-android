package org.ktachibana.cloudemoji;

import android.content.Context;

import com.orm.SugarApp;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formKey = "", // will not be used
        mailTo = "whj19931115@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.fail)
public class BaseApplication extends SugarApp {

    private static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();
    }

    public static Context context() {
        return CONTEXT;
    }
}
