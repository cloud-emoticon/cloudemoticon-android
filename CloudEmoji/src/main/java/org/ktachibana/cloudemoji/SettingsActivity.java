package org.ktachibana.cloudemoji;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    public static final String PREF_CLOSE_AFTER_COPY = "pref_close_after_copy";
    public static final String PREF_STAY_IN_NOTIFICATION = "pref_stay_in_notification";
    public static final String PREF_TEST_MY_REPO = "pref_test_my_repository";
    public static final String PREF_RESTORE_DEFAULT = "pref_restore_default";
    public static final String PREF_GITHUB_RELEASE = "pref_github_release";
    public static final String PREF_GITHUB_REPO = "pref_github_repo";
    public static final String PREF_VERSION = "pref_version";

    private SharedPreferences myPreferences;
    private EditTextPreference editRepositoryPref;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPreferences.registerOnSharedPreferenceChangeListener(this);

        editRepositoryPref = (EditTextPreference) findPreference(PREF_TEST_MY_REPO);
        editRepositoryPref.setSummary(editRepositoryPref.getText());

        Preference restorePref = findPreference(PREF_RESTORE_DEFAULT);
        restorePref.setOnPreferenceClickListener(this);

        Preference githubReleasePref = findPreference(PREF_GITHUB_RELEASE);
        githubReleasePref.setOnPreferenceClickListener(this);

        Preference githubRepoPref = findPreference(PREF_GITHUB_REPO);
        githubRepoPref.setOnPreferenceClickListener(this);

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
        } else if (PREF_GITHUB_RELEASE.equals(key)) {
            Uri url = Uri.parse(getResources().getString(R.string.github_release_url));
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intent);
        } else if (PREF_GITHUB_REPO.equals(key)) {
            Uri url = Uri.parse(getResources().getString(R.string.github_url));
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intent);
        }
        return true;
    }

}