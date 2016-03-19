package org.ktachibana.cloudemoji.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.mrengineer13.snackbar.SnackBar;

import org.ktachibana.cloudemoji.BuildConfig;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.ShowSnackBarOnBaseActivityEvent;
import org.ktachibana.cloudemoji.parsing.BackupHelper;
import org.ktachibana.cloudemoji.parsing.ImeHelper;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;
    private EventBus mBus;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBus = EventBus.getDefault();
        mBus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBus.unregister(this);
    }

    @Subscribe
    public void handle(EmptyEvent e) {}

    @Override
    public void onCreatePreferences(Bundle paramBundle, String rootKey) {
        // Load the mPreferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Navbar Gesture
        Preference navbarGesturePref = findPreference(Constants.PREF_NAVBAR_GESTURE);
        Preference nowOnTapPref = findPreference(Constants.PREF_NOW_ON_TAP);
        PreferenceCategory behaviorsPref = (PreferenceCategory) findPreference(Constants.PREF_BEHAVIORS);
        if (SystemUtils.belowJellybean())
            navbarGesturePref.setEnabled(false);
        if (SystemUtils.belowMarshmallow())
            behaviorsPref.removePreference(nowOnTapPref);
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

        // Now on Tap
        if (SystemUtils.aboveMarshmallow()) {
            behaviorsPref.removePreference(navbarGesturePref);
            PackageManager packageManager = getActivity().getPackageManager();
            ComponentName componentName = new ComponentName(getActivity(), CLS_ASSIST_ACTIVITY);
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
        nowOnTapPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                // Didn't found it in android.provider.Settings... Just hardcoded it.
                ComponentName componentName = new ComponentName(
                        Constants.PACKAGE_NAME_ANDROID_SETTINGS,
                        Constants.CLASS_NAME_MANAGE_ASSIST_ACTIVITY);
                intent.setComponent(componentName);
                startActivity(intent);
                return true;
            }
        });

        // Import favorites into IME
        Preference importImePref = findPreference(Constants.PREF_IMPORT_IME);
        importImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (SystemUtils.aboveMarshmallow()) {
                    showSnackBar(R.string.import_into_ime_m_below);
                } else {
                    int numberAdded = ImeHelper.importAllFavoritesIntoIme(getActivity().getContentResolver());
                    showSnackBar(String.format(getString(R.string.imported_into_ime), numberAdded));
                }
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
        Preference backupPref = findPreference(Constants.PREF_BACKUP_FAV);
        backupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean success = BackupHelper.backupFavorites();
                if (success) {
                    showSnackBar(Constants.FAVORITES_BACKUP_FILE_PATH);
                } else {
                    showSnackBar(R.string.fail);
                }
                return true;
            }
        });

        // Restore favorites
        Preference restorePref = findPreference(Constants.PREF_RESTORE_FAV);
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
        Preference gitHubReleasePref = findPreference(Constants.PREF_GIT_HUB_RELEASE);
        gitHubReleasePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(Constants.GIT_HUB_RELEASE_URL));
                startActivity(intent);
                return true;
            }
        });

        // GitHub Repo
        Preference gitHubRepoPref = findPreference(Constants.PREF_GIT_HUB_REPO);
        gitHubRepoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(Constants.GIT_HUB_REPO_URL));
                startActivity(intent);
                return true;
            }
        });

        // Version
        Preference versionPref = findPreference(Constants.PREF_VERSION);
        String version = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        versionPref.setTitle(getString(R.string.version) + " " + version);
        versionPref.setSummary(getString(R.string.version_code) + " " + versionCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    private void showSnackBar(String message) {
        mBus.post(new ShowSnackBarOnBaseActivityEvent(message));
    }

    private void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }
}
