package org.ktachibana.cloudemoji.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;
import org.ktachibana.cloudemoji.R;

public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    public static final String PREF_CLOSE_AFTER_COPY = "pref_close_after_copy";
    public static final String PREF_NOTIFICATION_VISIBILITY = "pref_notification_visibility";
    public static final String PREF_SHOW_AFTER_BOOT_UP = "pref_show_after_boot_up";
    public static final String PREF_SPLIT_VIEW = "pref_split_view_new";
    public static final String PREF_OVERRIDE_SYSTEM_FONT = "pref_override_system_font";
    public static final String PREF_TEST_MY_REPO = "pref_test_my_repository";
    public static final String PREF_RESTORE_DEFAULT = "pref_restore_default";
    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_HAS_RUN_BEFORE = "pref_has_run_before";

    private SharedPreferences myPreferences;
    private EditTextPreference editRepositoryPref;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences.registerOnSharedPreferenceChangeListener(this);

        // Editable repository URL preference
        editRepositoryPref = (EditTextPreference) findPreference(PREF_TEST_MY_REPO);
        editRepositoryPref.setSummary(editRepositoryPref.getText());

        // Restore default preference
        Preference restorePref = findPreference(PREF_RESTORE_DEFAULT);
        restorePref.setOnPreferenceClickListener(this);

        // Version
        Preference versionPref = findPreference(PREF_VERSION);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionPref.setTitle(getString(R.string.version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                myPreferences.unregisterOnSharedPreferenceChangeListener(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (key.equals(SettingsActivity.PREF_TEST_MY_REPO)) {
            String newUrl = editRepositoryPref.getText();
            editRepositoryPref.setSummary(newUrl);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (PREF_RESTORE_DEFAULT.equals(key)) {
            editRepositoryPref.setText(getString(R.string.default_url));
            editRepositoryPref.setSummary(getString(R.string.default_url));
            Toast.makeText(this, getString(R.string.restored_default),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}