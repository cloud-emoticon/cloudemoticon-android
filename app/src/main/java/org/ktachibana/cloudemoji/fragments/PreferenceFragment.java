package org.ktachibana.cloudemoji.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.mrengineer13.snackbar.SnackBar;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.parsing.BackupHelper;
import org.ktachibana.cloudemoji.parsing.ImeHelper;
import org.ktachibana.cloudemoji.utils.SystemUtils;

public class PreferenceFragment extends android.support.v4.preference.PreferenceFragment implements Constants {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        // Load the mPreferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Navbar Gesture
        Preference navbarGesturePref = findPreference(PREF_NAVBAR_GESTURE);
        if (SystemUtils.belowJellybean())
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

        // Import favorites into IME
        Preference importImePref = findPreference(PREF_IMPORT_IME);
        importImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int numberAdded = ImeHelper.importAllFavoritesIntoIme(getActivity().getContentResolver());
                showSnackBar(String.format(getString(R.string.imported_into_ime), numberAdded));
                return true;
            }
        });

        // Revoke favorite from IME
        /**
         Preference revokeImePref = findPreference(PREF_REVOKE_IME);
         revokeImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override public boolean onPreferenceClick(Preference preference) {
        int numberRevoked = ImeHelper.revokeAllFavoritesFromIme(getContentResolver());
        Toast.makeText(
        PreferenceActivity.this,
        String.format(getString(R.string.revoked_from_ime), numberRevoked),
        Toast.LENGTH_SHORT).show();
        return true;
        }
        });
         **/

        // Backup favorites
        Preference backupPref = findPreference(PREF_BACKUP_FAV);
        backupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean success = BackupHelper.backupFavorites();
                if (success) {
                    showSnackBar(FAVORITES_BACKUP_FILE_PATH);
                } else {
                    showSnackBar(R.string.fail);
                }
                return true;
            }
        });

        // Restore favorites
        Preference restorePref = findPreference(PREF_RESTORE_FAV);
        restorePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean success = BackupHelper.restoreFavorites();
                if (success) {
                    showSnackBar(android.R.string.ok);
                } else {
                    showSnackBar(R.string.fail);
                }
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

    private void showSnackBar(String message) {
        new SnackBar.Builder(getActivity().getApplicationContext(), getView())
                .withMessage(message)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    private void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }
}
