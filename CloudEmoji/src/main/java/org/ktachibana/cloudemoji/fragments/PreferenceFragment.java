package org.ktachibana.cloudemoji.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;

/**
 * This class uses android-support-v4-preferencefragment from https://github.com/kolavar/android-support-v4-preferencefragment
 */
public class PreferenceFragment extends android.support.v4.preference.PreferenceFragment implements Constants {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the mPreferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

        // Navbar Gesture
        Preference navbarGesturePref = findPreference(PREF_NAVBAR_GESTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            navbarGesturePref.setEnabled(false);
        navbarGesturePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PackageManager packageManager = getActivity().getPackageManager();
                ComponentName componentName = new ComponentName(getActivity(), CLS_ASSIST_ACTIVITY);
                int componentState = newValue.equals(true) ?
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                packageManager.setComponentEnabledSetting(componentName, componentState,
                        PackageManager.DONT_KILL_APP);
                return true;
            }
        });

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
        mPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }
}
