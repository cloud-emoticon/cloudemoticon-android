package org.ktachibana.cloudemoji.activities;

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
import android.widget.Toast;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.parsing.BackupHelper;
import org.ktachibana.cloudemoji.parsing.ImeHelper;

public class PreferenceActivity extends android.preference.PreferenceActivity implements Constants {
    private static final String CLS_ASSIST_ACTIVITY = "org.ktachibana.cloudemoji.activities.AssistActivity";
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the mPreferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        // Navbar Gesture
        Preference navbarGesturePref = findPreference(PREF_NAVBAR_GESTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            navbarGesturePref.setEnabled(false);
        navbarGesturePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PackageManager packageManager = getPackageManager();
                ComponentName componentName = new ComponentName(PreferenceActivity.this, CLS_ASSIST_ACTIVITY);
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
                int numberAdded = ImeHelper.importAllFavoritesIntoIme(getContentResolver());
                Toast.makeText(
                        PreferenceActivity.this,
                        String.format(getString(R.string.imported_into_ime), numberAdded),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Revoke favorite from IME
        /**
        Preference revokeImePref = findPreference(PREF_REVOKE_IME);
        revokeImePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
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
                    Toast.makeText(PreferenceActivity.this, FAVORITES_BACKUP_FILE_PATH, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PreferenceActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PreferenceActivity.this, getString(android.R.string.ok), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PreferenceActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
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
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
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