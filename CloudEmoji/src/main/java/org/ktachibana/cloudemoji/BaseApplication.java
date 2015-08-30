package org.ktachibana.cloudemoji;

import android.content.Context;

import com.orm.SugarApp;

public class BaseApplication extends SugarApp implements PrivateConstants {

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
