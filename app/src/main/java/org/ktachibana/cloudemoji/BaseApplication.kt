package org.ktachibana.cloudemoji

import android.arch.persistence.room.Room
import android.content.Context
import android.support.multidex.MultiDex

import com.facebook.stetho.Stetho
import com.orm.SugarApp
import org.ktachibana.cloudemoji.database.AppDatabase

class BaseApplication : SugarApp() {
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        mAppDatabase = Room.databaseBuilder(
        this,
            AppDatabase::class.java,
            "ce.db"
        ).build()

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                    .enableDumpapp(
                            Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(
                            Stetho.defaultInspectorModulesProvider(this))
                    .build())
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private var mContext: Context? = null
        private var mAppDatabase: AppDatabase? = null

        fun context(): Context? {
            return mContext
        }

        fun database(): AppDatabase? {
            return mAppDatabase
        }
    }
}
