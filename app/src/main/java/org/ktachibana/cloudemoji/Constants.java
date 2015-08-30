package org.ktachibana.cloudemoji;

import android.os.Environment;

import java.io.File;

public interface Constants {
    // General
    String USER_DICTIONARY_APP_ID = "cloudemoji";

    // Left drawer list item
    int LIST_ITEM_FAVORITE_ID = 0;
    int LIST_ITEM_HISTORY_ID = 1;
    int LIST_ITEM_BUILT_IN_EMOJI_ID = 2;
    int LIST_ITEM_REPOSITORIES = 3;
    int LIST_ITEM_REPO_MANAGER_ID = 4;
    int LIST_ITEM_REPO_STORE_ID = 5;
    int LIST_ITEM_UPDATE_CHECKER_ID = 6;
    int LIST_ITEM_SETTINGS_ID = 7;
    int LIST_ITEM_EXIT_ID = 8;
    int LIST_ITEM_ACCOUNT_ID = 9;
    int DEFAULT_LIST_ITEM_ID = LIST_ITEM_FAVORITE_ID;

    // Notification
    int PERSISTENT_NOTIFICATION_ID = 0;

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

    // Repository
    int FORMAT_TYPE_XML = 0;
    int FORMAT_TYPE_JSON = 1;

    // URLs
    String DEFAULT_REPOSITORY_URL = "https://dl.dropboxusercontent.com/u/120725807/test.xml";
    String GIT_HUB_RELEASE_URL = "https://github.com/KTachibanaM/cloudemoji/releases";
    String GIT_HUB_REPO_URL = "https://github.com/KTachibanaM/cloudemoji";
    String CLOUD_API_HOST = "http://ce.catofes.com";
    String STORE_URL = "http://emoticon.moe/store.json";
    String UPDATE_CHECKER_URL = "https://cloudemoticonbackend.herokuapp.com/version";
    String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji";

    // Intent
    int REPOSITORY_MANAGER_REQUEST_CODE = 0;
    int PREFERENCE_REQUEST_CODE = 1;
    int REPOSITORY_STORE_REQUEST_CODE = 2;
    int ACCOUNT_REQUEST_CODE = 3;

    // File
    String FAVORITES_BACKUP_FILE_PATH
            = Environment.getExternalStorageDirectory().getPath() + File.separator + "ce.json";
    String EXPORT_FILE_PATH
            = Environment.getExternalStorageDirectory().getPath() + File.separator + "%s";

    // Debug
    String DEBUG_TAG = "CloudEmoticon";
}
