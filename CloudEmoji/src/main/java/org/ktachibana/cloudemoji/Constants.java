package org.ktachibana.cloudemoji;

public interface Constants {
    // Left drawer list item
    public static final long LIST_ITEM_FAVORITE_ID = -1;
    public static final long LIST_ITEM_HISTORY_ID = -2;

    // Notification
    public static final int PERSISTENT_NOTIFICATION_ID = 0;

    // Preferences
    public static final String PREF_CLOSE_AFTER_COPY = "pref_close_after_copy";
    public static final String PREF_NOTIFICATION_VISIBILITY = "pref_notification_visibility";
    public static final String PREF_SHOW_AFTER_BOOT_UP = "pref_show_after_boot_up";
    public static final String PREF_SPLIT_VIEW = "pref_split_view_new";
    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_GIT_HUB_RELEASE = "pref_git_hub_release";
    public static final String PREF_GIT_HUB_REPO = "pref_git_hub_repo";
    public static final String PREF_HAS_RUN_BEFORE = "pref_has_run_before";
    public static final String PREF_NAVBAR_GESTURE = "pref_navbar_gesture";

    // Repository
    public static final int FORMAT_TYPE_XML = 0;
    public static final int FORMAT_TYPE_JSON = 1;

    // URL
    public static final String DEFAULT_REPOSITORY_URL = "https://dl.dropboxusercontent.com/u/120725807/test.xml";
    public static final String GIT_HUB_RELEASE_URL = "https://github.com/KTachibanaM/cloudemoji/releases";
    public static final String GIT_HUB_REPO_URL = "https://github.com/KTachibanaM/cloudemoji";
}
