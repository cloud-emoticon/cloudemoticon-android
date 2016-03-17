package org.ktachibana.cloudemoji;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.orm.SugarApp;
import com.squareup.leakcanary.LeakCanary;

public class BaseApplication extends SugarApp {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(
                                    Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(
                                    Stetho.defaultInspectorModulesProvider(this))
                            .build());
            LeakCanary.install(this);
        }
    }

    public static Context context() {
        return mContext;
    }
}
