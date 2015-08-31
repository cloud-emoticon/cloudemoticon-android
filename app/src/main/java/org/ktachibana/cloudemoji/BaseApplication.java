package org.ktachibana.cloudemoji;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.orm.SugarApp;

public class BaseApplication extends SugarApp {

    private static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                    .enableDumpapp(
                            Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(
                            Stetho.defaultInspectorModulesProvider(this))
                    .build());
        }
    }

    public static Context context() {
        return CONTEXT;
    }
}
