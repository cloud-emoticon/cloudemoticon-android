package org.ktachibana.cloudemoji;

import android.os.Environment;
import android.support.annotation.IntDef;

import java.io.File;

public interface Constants {
    // General
    String USER_DICTIONARY_APP_ID = "cloudemoji";

    // Notification
    int PERSISTENT_NOTIFICATION_ID = 0;
    String PERSISTENT_NOTIFICATION_DEFAULT_VISIBILITY = "both";

    // Preferences
    String PREF_CLOSE_AFTER_COPY = "pref_close_after_copy";
    String PREF_NOTIFICATION_VISIBILITY = "pref_notification_visibility";
    String PREF_SHOW_AFTER_BOOT_UP = "pref_show_after_boot_up";
    String PREF_VERSION = "pref_version";
    String PREF_GIT_HUB_RELEASE = "pref_git_hub_release";
    String PREF_GIT_HUB_REPO = "pref_git_hub_repo";
    String PREF_HAS_RUN_BEFORE = "pref_has_run_before";
    String PREF_NAVBAR_GESTURE = "pref_navbar_gesture";
    String PREF_BACKUP_FAV = "pref_backup_fav";
    String PREF_RESTORE_FAV = "pref_restore_fav";
    String PREF_IMPORT_IME = "pref_import_into_ime";
    String PREF_REVOKE_IME = "pref_revoke_from_ime";
    String PREF_BEHAVIORS = "pref_behaviors";
    String PREF_NOW_ON_TAP = "pref_now_on_tap";

    // Repository
    int FORMAT_TYPE_XML = 0;
    int FORMAT_TYPE_JSON = 1;

    @IntDef({FORMAT_TYPE_XML, FORMAT_TYPE_JSON})
    @interface FormatType {
    }

    // URLs
    String DEFAULT_REPOSITORY_URL = "http://ktachibana.party/cloudemoticon/default.json";
    String GIT_HUB_RELEASE_URL = "https://github.com/KTachibanaM/cloudemoji/releases";
    String GIT_HUB_REPO_URL = "https://github.com/KTachibanaM/cloudemoji";
    String STORE_URL = "http://emoticon.moe/store.json";
    String UPDATE_CHECKER_URL = "http://ktachibana.party/cloudemoticon/version.json";
    String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji";

    // Intent
    int REPOSITORY_MANAGER_REQUEST_CODE = 0;
    int PREFERENCE_REQUEST_CODE = 1;
    int REPOSITORY_STORE_REQUEST_CODE = 2;

    // File
    String FAVORITES_BACKUP_FILE_PATH
            = Environment.getExternalStorageDirectory().getPath() + File.separator + "ce.json";
    String EXPORT_FILE_PATH
            = Environment.getExternalStorageDirectory().getPath() + File.separator + "%s";

    // Hacking
    String PACKAGE_NAME_ANDROID_SETTINGS = "com.android.settings";
    String CLASS_NAME_MANAGE_ASSIST_ACTIVITY = "com.android.settings.Settings$ManageAssistActivity";

    // Debug
    String DEBUG_TAG = "CloudEmoticon";
}
