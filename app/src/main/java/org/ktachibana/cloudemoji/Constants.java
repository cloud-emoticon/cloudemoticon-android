package org.ktachibana.cloudemoji;

import androidx.annotation.IntDef;

public interface Constants {
    // Notification
    int QUICK_TRIGGER_NOTIFICATION_ID = 0;
    String QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID = "quick_trigger";

    // Preferences
    String PREF_CLOSE_AFTER_COPY = "pref_close_after_copy";
    String PREF_NOTIFICATION_LEGACY_VISIBILITY = "pref_notification_legacy_visibility";
    String QUICK_TRIGGER_NOTIFICATION_LEGACY_VISIBILITY_BOTH = "both";
    String QUICK_TRIGGER_NOTIFICATION_LEGACY_VISIBILITY_NO = "no";
    String QUICK_TRIGGER_NOTIFICATION_LEGACY_VISIBILITY_PANEL = "panel";
    String PREF_SHOW_AFTER_BOOT_UP = "pref_show_after_boot_up";
    String PREF_VERSION = "pref_version";
    String PREF_PRIVACY_POLICY = "pref_privacy_policy";
    String PREF_CHECK_FOR_UPDATE = "pref_check_for_update";
    String PREF_GIT_HUB_RELEASE = "pref_git_hub_release";
    String PREF_GIT_HUB_REPO = "pref_git_hub_repo";
    String PREF_HAS_RUN_BEFORE = "pref_has_run_before";
    String PREF_NAVBAR_GESTURE = "pref_navbar_gesture";
    String PREF_QUICK_ENTER_HELP = "pref_quick_enter_help";
    String PREF_QUICK_ENTER = "pref_quick_enter";
    String PREF_SET_UP_ACCESSIBILITY = "pref_set_up_accessibility";
    String PREF_BACKUP_FAV = "pref_backup_fav";
    String PREF_RESTORE_FAV = "pref_restore_fav";
    String PREF_IMPORT_PERSONAL_DICT = "pref_import_into_personal_dict";
    String PREF_REVOKE_PERSONAL_DICT = "pref_revoke_from_personal_dict";
    String PREF_NOW_ON_TAP = "pref_now_on_tap";
    String PREF_PERSONAL_DICTIONARY = "pref_ime";
    String PREF_SHOW_NOTIFICATION = "pref_show_notification";

    // Repository
    int FORMAT_TYPE_XML = 0;
    int FORMAT_TYPE_JSON = 1;

    @IntDef({FORMAT_TYPE_XML, FORMAT_TYPE_JSON})
    @interface FormatType { }

    // URLs
    String DEFAULT_REPOSITORY_URL = "http://ktachibana.party/cloudemoticon/default.json";
    String GITHUB_RELEASE_URL = "https://github.com/KTachibanaM/cloudemoji/releases";
    String GITHUB_REPO_URL = "https://github.com/KTachibanaM/cloudemoji";
    String STORE_URL = "http://emoticon.moe/store.json";
    String UPDATE_CHECKER_URL = "http://ktachibana.party/cloudemoticon/version.json";
    String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.ktachibana.cloudemoji";
    String REPO_STORE_CONTRIBUTION_URL = "https://github.com/cloud-emoticon/store-repos/blob/master/CONTRIBUTING.md";
    String PRIVACY_POLICY_URL = "https://emoticon.moe/android/privacy";
    String QUICK_ENTER_HELP_URL = "https://emoticon.moe/android/quick-enter";

    // Intent
    int REPOSITORY_MANAGER_REQUEST_CODE = 0;
    int PREFERENCE_REQUEST_CODE = 1;
    int REPOSITORY_STORE_REQUEST_CODE = 2;

    // Hacking
    String PACKAGE_NAME_ANDROID_SETTINGS = "com.android.settings";
    String CLASS_NAME_MANAGE_ASSIST_ACTIVITY = "com.android.settings.Settings$ManageAssistActivity";

    // Debug
    String DEBUG_TAG = "CloudEmoticon";
}
