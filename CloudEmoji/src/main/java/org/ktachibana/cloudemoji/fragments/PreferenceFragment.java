package org.ktachibana.cloudemoji.fragments;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;

/**
 * This class uses android-support-v4-preferencefragment from https://github.com/kolavar/android-support-v4-preferencefragment
 */
public class PreferenceFragment extends android.support.v4.preference.PreferenceFragment implements Constants {
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

        // Editable repository URL preference
        final EditTextPreference editRepositoryPref = (EditTextPreference) findPreference(PREF_TEST_MY_REPO);
        editRepositoryPref.setSummary(editRepositoryPref.getText());

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (PREF_TEST_MY_REPO.equals(key)) {
                    editRepositoryPref.setSummary(editRepositoryPref.getText());
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        // Restore default preference
        Preference restorePref = findPreference(PREF_RESTORE_DEFAULT);
        restorePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String key = preference.getKey();
                if (PREF_RESTORE_DEFAULT.equals(key)) {
                    String defaultUrl = getActivity().getString(R.string.default_url);
                    editRepositoryPref.setText(defaultUrl);
                    editRepositoryPref.setSummary(defaultUrl);
                    Toast.makeText(getActivity(), getString(R.string.restored_default), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Version
        Preference versionPref = findPreference(PREF_VERSION);
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            String versionCode = Integer.toString(pInfo.versionCode);
            versionPref.setTitle(getString(R.string.version) + " " + version);
            versionPref.setSummary(getString(R.string.version_code) + " " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}
