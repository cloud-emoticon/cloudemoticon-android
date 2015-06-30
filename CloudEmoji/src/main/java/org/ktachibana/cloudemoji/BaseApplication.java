package org.ktachibana.cloudemoji;

import android.content.Context;

import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseObject;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;

@ReportsCrashes(
        formKey = "", // will not be used
        mailTo = "whj19931115@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.fail)
public class BaseApplication extends SugarApp implements PrivateConstants {

    private static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();

        Parse.initialize(this, applicationId, clientKey);
        ParseObject.registerSubclass(ParseBookmark.class);
    }

    public static Context context() {
        return CONTEXT;
    }
}
