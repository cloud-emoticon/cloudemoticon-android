package org.ktachibana.cloudemoji.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BuildConfig;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.ShowSnackBarOnBaseActivityEvent;
import org.ktachibana.cloudemoji.utils.BackupUtils;
import org.ktachibana.cloudemoji.utils.PersonalDictionaryUtils;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;
    private Context mContext;
    private EventBus mBus;
    private static final int RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_BACKUP = 125;
    private static final int RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_RESTORE = 126;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBus = EventBus.getDefault();
        mBus.register(this);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBus.unregister(this);
    }

    @Subscribe
    public void handle(EmptyEvent e) {
    }

    @Override
    public void onCreatePreferences(Bundle paramBundle, String rootKey) {
        // Load the mPreferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Navbar Gesture
        Preference navbarGesturePref = findPreference(Constants.PREF_NAVBAR_GESTURE);
        Preference nowOnTapPref = findPreference(Constants.PREF_NOW_ON_TAP);
        PreferenceCategory behaviorsPref = (PreferenceCategory) findPreference(Constants.PREF_BEHAVIORS);
        navbarGesturePref.setEnabled(false);
        if (!SystemUtils.aboveMarshmallow23()) {
            behaviorsPref.removePreference(nowOnTapPref);
        }
        navbarGesturePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PackageManager packageManager = mContext.getPackageManager();
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
        if (SystemUtils.aboveMarshmallow23()) {
            behaviorsPref.removePreference(navbarGesturePref);
            PackageManager packageManager = mContext.getPackageManager();
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

        // Import favorites into personal dictionary
        Preference importImePref = findPreference(Constants.PREF_IMPORT_PERSONAL_DICT);
        importImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int numberAdded = PersonalDictionaryUtils.importAllFavorites(mContext.getContentResolver());
                showSnackBar(String.format(getString(R.string.imported_into_personal_dict), numberAdded));
                return true;
            }
        });

        // Revoke favorite from personal dictionary
        Preference revokeImePref = findPreference(Constants.PREF_REVOKE_PERSONAL_DICT);
        revokeImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int numberRevoked = PersonalDictionaryUtils.revokeAllFavorites(mContext.getContentResolver());
                showSnackBar(String.format(getString(R.string.revoked_from_personal_dict), numberRevoked));
                return true;
            }
        });

        // Backup favorites
        Preference backupPref = findPreference(Constants.PREF_BACKUP_FAV);
        backupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupFavorites();
                return true;
            }
        });

        // Restore favorites
        Preference restorePref = findPreference(Constants.PREF_RESTORE_FAV);
        restorePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                restoreFavorites();
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

    @AfterPermissionGranted(RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_BACKUP)
    void backupFavorites() {
        if (!SystemUtils.aboveMarshmallow23()) {
            _backupFavorites();
            return;
        }
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            _backupFavorites();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, mContext.getString(R.string.storage_rationale),
                    RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_BACKUP, perms);
        }
    }

    void _backupFavorites() {
        boolean success = BackupUtils.backupFavorites();
        if (success) {
            showSnackBar(getString(R.string.backed_up_favorites) + ": " + Constants.FAVORITES_BACKUP_FILE_PATH);
        } else {
            showSnackBar(R.string.fail);
        }
    }

    @AfterPermissionGranted(RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_RESTORE)
    void restoreFavorites() {
        if (!SystemUtils.aboveMarshmallow23()) {
            _restoreFavorites();
            return;
        }
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            _restoreFavorites();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, mContext.getString(R.string.storage_rationale),
                    RC_READ_AND_WRITE_EXTERNAL_STORAGE_FOR_RESTORE, perms);
        }
    }

    void _restoreFavorites() {
        boolean success = BackupUtils.restoreFavorites();
        if (success) {
            showSnackBar(R.string.restored_favorites);
        } else {
            showSnackBar(R.string.fail);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
