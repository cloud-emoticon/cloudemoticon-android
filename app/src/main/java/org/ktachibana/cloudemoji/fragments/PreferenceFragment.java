package org.ktachibana.cloudemoji.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.BuildConfig;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.ShowSnackBarOnBaseActivityEvent;
import org.ktachibana.cloudemoji.net.VersionCodeCheckerClient;
import org.ktachibana.cloudemoji.utils.BackupUtils;
import org.ktachibana.cloudemoji.utils.CapabilityUtils;
import org.ktachibana.cloudemoji.utils.NotificationUtils;
import org.ktachibana.cloudemoji.utils.PersonalDictionaryUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class PreferenceFragment extends PreferenceFragmentCompat {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    private static final String BACKUP_FILE_MIME_TYPE = "application/json";
    private static final String BACKUP_FILENAME = "ce.json";
    private static final int RC_BACKUP = 1;
    private static final int RC_RESTORE = 2;

    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;
    private Context mContext;
    private EventBus mBus;
    private ContentResolver mContentResolver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBus = EventBus.getDefault();
        mBus.register(this);
        mContext = context;
        mContentResolver = context.getContentResolver();
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

        // Setup notification settings
        syncShowAfterBootUpToNotificationVisibility(null);
        Preference notificationLegacyVisibilityPref = findPreference(Constants.PREF_NOTIFICATION_LEGACY_VISIBILITY);
        Preference showNotificationPref = findPreference(Constants.PREF_SHOW_NOTIFICATION);
        if (CapabilityUtils.notQuickTriggerNotificationLegacyVisibility()) {
            notificationLegacyVisibilityPref.setVisible(false);
            showNotificationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                syncShowAfterBootUpToNotificationVisibility(newValue);
                NotificationUtils.setupNotification(mContext, newValue);
                return true;
            });

        } else {
            showNotificationPref.setVisible(false);
            notificationLegacyVisibilityPref.setOnPreferenceChangeListener((preference, newValue) -> {
                syncShowAfterBootUpToNotificationVisibility(newValue);
                NotificationUtils.setupNotification(mContext, newValue);
                return true;
            });
        }

        // Now on Tap or Navbar Gesture
        Preference navbarGesturePref = findPreference(Constants.PREF_NAVBAR_GESTURE);
        Preference nowOnTapPref = findPreference(Constants.PREF_NOW_ON_TAP);
        if (CapabilityUtils.nowOnTapAvailable()) {
            navbarGesturePref.setVisible(false);

            // Now on Tap
            PackageManager packageManager = mContext.getPackageManager();
            ComponentName componentName = new ComponentName(getActivity(), CLS_ASSIST_ACTIVITY);
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            nowOnTapPref.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent();
                // Didn't found it in android.provider.Settings... Just hardcoded it.
                ComponentName componentName1 = new ComponentName(
                        Constants.PACKAGE_NAME_ANDROID_SETTINGS,
                        Constants.CLASS_NAME_MANAGE_ASSIST_ACTIVITY);
                intent.setComponent(componentName1);
                startActivity(intent);
                return true;
            });
        } else {
            nowOnTapPref.setVisible(false);

            // Navbar Gesture
            navbarGesturePref.setOnPreferenceChangeListener((preference, newValue) -> {
                PackageManager packageManager = mContext.getPackageManager();
                ComponentName componentName = new ComponentName(getActivity(), CLS_ASSIST_ACTIVITY);
                int componentState = newValue.equals(true) ?
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                packageManager.setComponentEnabledSetting(componentName, componentState,
                        PackageManager.DONT_KILL_APP);
                return true;
            });
        }

        PreferenceCategory personalDictionaryPref = (PreferenceCategory) findPreference(Constants.PREF_PERSONAL_DICTIONARY);
        if (CapabilityUtils.personalDictionaryUnavailable()) {
            personalDictionaryPref.setVisible(false);
        } else {
            // Import favorites into personal dictionary
            Preference importImePref = findPreference(Constants.PREF_IMPORT_PERSONAL_DICT);
            importImePref.setOnPreferenceClickListener(preference -> {
                int numberAdded = PersonalDictionaryUtils.importAllFavorites(mContext.getContentResolver());
                showSnackBar(String.format(getString(R.string.imported_into_personal_dict), numberAdded));
                return true;
            });

            // Revoke favorite from personal dictionary
            Preference revokeImePref = findPreference(Constants.PREF_REVOKE_PERSONAL_DICT);
            revokeImePref.setOnPreferenceClickListener(preference -> {
                int numberRevoked = PersonalDictionaryUtils.revokeAllFavorites(mContext.getContentResolver());
                showSnackBar(String.format(getString(R.string.revoked_from_personal_dict), numberRevoked));
                return true;
            });
        }

        // Backup favorites
        Preference backupPref = findPreference(Constants.PREF_BACKUP_FAV);
        backupPref.setOnPreferenceClickListener(preference -> {
            backupFavorites();
            return true;
        });

        // Restore favorites
        Preference restorePref = findPreference(Constants.PREF_RESTORE_FAV);
        restorePref.setOnPreferenceClickListener(preference -> {
            restoreFavorites();
            return true;
        });


        // Check for update
        Preference checkForUpdatePref = findPreference(Constants.PREF_CHECK_FOR_UPDATE);
        checkForUpdatePref.setOnPreferenceClickListener(preference -> {
            new VersionCodeCheckerClient().checkForLatestVersionCode(new BaseHttpClient.IntCallback() {
                @Override
                public void success(int result) {
                    checkVersionCode(true, result);
                }

                @Override
                public void fail(Throwable t) {
                    checkVersionCode(false, 0);
                }

                @Override
                public void finish() {

                }
            });
            return true;
        });

        // GitHub Release
        Preference gitHubReleasePref = findPreference(Constants.PREF_GIT_HUB_RELEASE);
        gitHubReleasePref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse(Constants.GIT_HUB_RELEASE_URL));
            startActivity(intent);
            return true;
        });

        // GitHub Repo
        Preference gitHubRepoPref = findPreference(Constants.PREF_GIT_HUB_REPO);
        gitHubRepoPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse(Constants.GIT_HUB_REPO_URL));
            startActivity(intent);
            return true;
        });

        // Version
        Preference versionPref = findPreference(Constants.PREF_VERSION);
        String version = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        versionPref.setTitle(getString(R.string.version) + " " + version);
        versionPref.setSummary(getString(R.string.version_code) + " " + versionCode);
    }

    /**
     * This method sync the state of "show it after boot-up" preference
     * to the state of "show notification"/"notification visibility" preference
     *
     * e.g. if "show notification"/"notification visibility" is false or no
     *      "show it after boot-up" cannot be true
     *      hence we programmatically set it to false, and disable it
     *      otherwise, we enable the "show it after boot-up" preference
     *
     * @param newVisibleOrVisibility
     */
    private void syncShowAfterBootUpToNotificationVisibility(Object newVisibleOrVisibility) {
        CheckBoxPreference showAfterBootUpPref = (CheckBoxPreference) findPreference(Constants.PREF_SHOW_AFTER_BOOT_UP);

        if (CapabilityUtils.notQuickTriggerNotificationLegacyVisibility()) {
            CheckBoxPreference showNotificationPref = (CheckBoxPreference) findPreference(Constants.PREF_SHOW_NOTIFICATION);
            boolean visible = showNotificationPref.isChecked();
            if (newVisibleOrVisibility != null) {
                visible = (boolean) newVisibleOrVisibility;
            }
            if (!visible) {
                SharedPreferences.Editor edit = mPreferences.edit();
                edit.putBoolean(Constants.PREF_SHOW_AFTER_BOOT_UP, false);
                edit.apply();
                showAfterBootUpPref.setChecked(false);
                showAfterBootUpPref.setEnabled(false);
            } else {
                showAfterBootUpPref.setEnabled(true);
            }
        } else {
            ListPreference notificationLegacyVisibilityPref = (ListPreference) findPreference(Constants.PREF_NOTIFICATION_LEGACY_VISIBILITY);
            String visibility = notificationLegacyVisibilityPref.getValue();
            if (newVisibleOrVisibility != null) {
                visibility = (String) newVisibleOrVisibility;
            }
            if (Constants.QUICK_TRIGGER_NOTIFICATION_LEGACY_VISIBILITY_NO.equals(visibility)) {
                SharedPreferences.Editor edit = mPreferences.edit();
                edit.putBoolean(Constants.PREF_PERSONAL_DICTIONARY, false);
                edit.apply();
                showAfterBootUpPref.setChecked(false);
                showAfterBootUpPref.setEnabled(false);
            } else {
                showAfterBootUpPref.setEnabled(true);
            }
        }
    }

    void backupFavorites() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(BACKUP_FILE_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TITLE, BACKUP_FILENAME);

        startActivityForResult(intent, RC_BACKUP);
    }

    void restoreFavorites() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(BACKUP_FILE_MIME_TYPE);

        startActivityForResult(intent, RC_RESTORE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OutputStream os = null;
        InputStream is = null;
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == RC_BACKUP) {
                    os = mContentResolver.openOutputStream(data.getData());
                    BackupUtils.writeFavorites(os);
                    showSnackBar(getString(R.string.backed_up_favorites));
                } else if (requestCode == RC_RESTORE) {
                    is = mContentResolver.openInputStream(data.getData());
                    BackupUtils.readFavorites(is);
                    showSnackBar(getString(R.string.restored_favorites));
                }
            } else {
                showSnackBar(getString(R.string.fail));
            }
        } catch (IOException e) {
            showSnackBar(getString(R.string.fail));
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
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
        mBus.post(new ShowSnackBarOnBaseActivityEvent(getString(resId)));
    }

    private void checkVersionCode(boolean success, int latestVersionCode) {
        // If failed
        if (!success) {
            showSnackBar(R.string.update_checker_failed);
            return;
        }

        // Get current version and compare
        int versionCode = BuildConfig.VERSION_CODE;

        if (latestVersionCode == versionCode) {
            // Already latest
            showSnackBar(R.string.already_latest_version);
        } else if (latestVersionCode < versionCode) {
            // More latest than latest
            showSnackBar(R.string.cool_kid);
        } else {
            // New version available, show dialog
            new MaterialDialog.Builder(mContext)
                    .title(String.format(getString(R.string.new_version_available), latestVersionCode))
                    .positiveText(R.string.go_to_play_store)
                    .negativeText(android.R.string.cancel)
                    .onPositive((dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(Constants.PLAY_STORE_URL));
                        startActivity(intent);
                    })
                    .show();
        }
    }
}
