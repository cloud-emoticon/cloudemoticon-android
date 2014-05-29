package org.ktachibana.cloudemoji;

import com.orm.SugarApp;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formKey = "", // will not be used
        mailTo = "whj19931115@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.fail)
public class BaseApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
    }
}
