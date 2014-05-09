package org.ktachibana.cloudemoji.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;

import java.net.URI;

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

        // GitHub Release
        Preference gitHubReleasePref = findPreference(PREF_GIT_HUB_RELEASE);
        gitHubReleasePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GIT_HUB_RELEASE_URL));
                startActivity(intent);
                return true;
            }
        });

        // GitHub Repo
        Preference gitHubRepoPref = findPreference(PREF_GIT_HUB_REPO);
        gitHubRepoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GIT_HUB_REPO_URL));
                startActivity(intent);
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
