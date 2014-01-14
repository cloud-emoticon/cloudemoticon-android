package org.ktachibana.cloudemoji;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Fires notification after boot up if user preference said so
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean test = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.PREF_SHOW_AFTER_BOOT_UP, true);
        Log.e("233", Boolean.toString(test));
        finish();
    }
}
