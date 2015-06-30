package org.ktachibana.cloudemoji.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.fragments.EmojiconsFragment;
import org.ktachibana.cloudemoji.fragments.FavoriteFragment;
import org.ktachibana.cloudemoji.fragments.HistoryFragment;
import org.ktachibana.cloudemoji.fragments.RepositoriesFragment;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.net.VersionCodeCheckerClient;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.utils.NotificationHelper;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;
import org.ktachibana.cloudemoji.utils.UncheckableSecondaryDrawerItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.Subscribe;

public class MainActivity extends BaseActivity implements
        Constants,
        SharedPreferences.OnSharedPreferenceChangeListener, Drawer.OnDrawerItemClickListener {

    private static final String STATE_TAG = "state";
    public static final String SOURCE_CACHE_TAG = "source_cache";
    private Drawer mDrawer;
    private MainActivityState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Setup drawer
        setupDrawer();
        setupAccountHeader();

        // Setup notification state
        setupNotificationState();

        // Check first time run
        firstTimeCheck();

        // If not starting from refresh new, get state
        if (savedInstanceState != null) {
            mState = savedInstanceState.getParcelable(STATE_TAG);
        }

        // Else, initialize
        else {
            mState = new MainActivityState(initializeCache());
        }

        // Refresh UI with current state
        refreshUiWithCurrentState();
    }

    private void setupDrawer() {
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .build();
        setupLeftDrawer();
    }

    private void setupAccountHeader() {
        mDrawer.removeHeader();
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_place_holder)
                .build();
        if (ParseUserState.isLoggedIn()) {
            String username = ParseUserState.getLoggedInUser().getUsername();
            String email = ParseUserState.getLoggedInUser().getEmail();
            accountHeader.addProfile(
                    new ProfileDrawerItem().withName(username).withEmail(email), 0
            );
        }
        mDrawer.setHeader(accountHeader.getView());
    }

    /**
     * Put every source, including favorites, into cache
     */
    private SourceInMemoryCache initializeCache() {
        SourceInMemoryCache cache = new SourceInMemoryCache();

        /**
         // Put favorites
         Source favoritesSource = FavoritesHelper.getFavoritesAsSource();
         cache.put(-1, favoritesSource);
         **/

        // Put all available repositories
        List<Repository> allRepositories = Repository.listAll(Repository.class);
        for (Repository repository : allRepositories) {
            if (repository.isAvailable()) {
                try {
                    long id = repository.getId();
                    Source source =
                            new SourceReader().readSourceFromDatabaseId(repository.getAlias(), id);
                    cache.put(id, source);
                } catch (SourceParsingException e) {
                    showSnackBar(getString(R.string.invalid_repo_format) + e.getFormatType().toString());
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }

        return cache;
    }

    private void setupLeftDrawer() {
        // Add favorite
        mDrawer.addItem(
                new PrimaryDrawerItem()
                        .withName(R.string.fav)
                        .withIcon(R.drawable.ic_favorite)
                        .withIdentifier(LIST_ITEM_FAVORITE_ID)
        );

        // Add history
        mDrawer.addItem(
                new PrimaryDrawerItem()
                        .withName(R.string.history)
                        .withIcon(R.drawable.ic_history)
                        .withIdentifier(LIST_ITEM_HISTORY_ID)
        );

        // Add built in emoji
        mDrawer.addItem(
                new PrimaryDrawerItem()
                        .withName(R.string.built_in_emoji)
                        .withIcon(R.drawable.ic_built_in_emoji)
                        .withIdentifier(LIST_ITEM_BUILT_IN_EMOJI_ID)
        );

        // Add repositories
        mDrawer.addItem(
                new PrimaryDrawerItem()
                        .withName(R.string.repositories)
                        .withIcon(R.drawable.ic_repository)
                        .withIdentifier(LIST_ITEM_REPOSITORIES)
        );

        // Divider
        mDrawer.addItem(new DividerDrawerItem());

        // Add account
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.account)
                        .withIcon(R.drawable.ic_account)
                        .withIdentifier(LIST_ITEM_ACCOUNT_ID)
        );

        // Add repo manager
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.repo_manager)
                        .withIcon(R.drawable.ic_repository_manager)
                        .withIdentifier(LIST_ITEM_REPO_MANAGER_ID)
        );

        // Add repo store
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.repository_store)
                        .withIcon(R.drawable.ic_store)
                        .withIdentifier(LIST_ITEM_REPO_STORE_ID)
        );

        // Add update checker
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.update_checker)
                        .withIcon(R.drawable.ic_update_checker)
                        .withIdentifier(LIST_ITEM_UPDATE_CHECKER_ID)
        );

        // Add settings
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.settings)
                        .withIcon(R.drawable.ic_settings)
                        .withIdentifier(LIST_ITEM_SETTINGS_ID)
        );

        // Add exit
        mDrawer.addItem(
                new UncheckableSecondaryDrawerItem()
                        .withName(R.string.exit)
                        .withIcon(R.drawable.ic_exit)
                        .withIdentifier(LIST_ITEM_EXIT_ID)
        );

        // On click
        mDrawer.setOnDrawerItemClickListener(this);
    }

    private void setupNotificationState() {
        NotificationHelper
                .switchNotificationState(this,
                        mPreferences.getString(PREF_NOTIFICATION_VISIBILITY, "both"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (PREF_NOTIFICATION_VISIBILITY.equals(key)) {
            setupNotificationState();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra(SOURCE_CACHE_TAG, mState.getSourceCache());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void handle(FavoriteAddedEvent event) {
        showSnackBar(event.getEmoticon() + "\n" + getString(R.string.added_to_fav));
    }

    @Subscribe
    public void handle(FavoriteDeletedEvent event) {
        showSnackBar(event.getEmoticon() + "\n" + getString(R.string.removed_from_fav));
    }

    private void checkVersionCode(boolean success, int latestVersionCode) {
        // If failed
        if (!success) {
            showSnackBar(R.string.update_checker_failed);
            return;
        }

        // Get current version and compare
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionCode = pInfo.versionCode;

            // Already latest
            if (latestVersionCode == versionCode) {
                showSnackBar(R.string.already_latest_version);
                return;
            }

            // New version available, show dialog
            new MaterialDialog.Builder(MainActivity.this)
                    .title(getString(R.string.new_version_available) + String.format(" (%d)", latestVersionCode))
                    .positiveText(R.string.go_to_play_store)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(PLAY_STORE_URL));
                            startActivity(intent);
                        }
                    })
                    .show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private void closeDrawers() {
        mDrawer.closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Coming back from repository manager or repository store, repositories may be changed
        if (requestCode == REPOSITORY_MANAGER_REQUEST_CODE ||
                requestCode == REPOSITORY_STORE_REQUEST_CODE) {

            // If currently showing repositories, refresh
            if (mState.getItemId() == LIST_ITEM_REPOSITORIES) {
                mState.setSourceCache(initializeCache());
                refreshUiWithCurrentState();
            }
        }

        // Coming back from preference, favorites may be changed
        if (requestCode == PREFERENCE_REQUEST_CODE) {

            // If currently showing favorites, refresh
            if (mState.getItemId() == LIST_ITEM_FAVORITE_ID) {
                refreshUiWithCurrentState();
            }
        }

        // Coming back from account, user state may be changed
        if (requestCode == ACCOUNT_REQUEST_CODE) {
            setupAccountHeader();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save current state
        outState.putParcelable(STATE_TAG, mState);
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = mPreferences.getBoolean(PREF_HAS_RUN_BEFORE, false);

        upgradeFavoriteDatabaseIfExists();
        setupDefaultRepoIfNotExists();

        // If hasn't run before
        if (!hasRunBefore) {
            // It has run
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_HAS_RUN_BEFORE, true);
            editor.apply();
        }
    }

    @SuppressWarnings("unchecked")
    private void setupDefaultRepoIfNotExists() {
        // Find repository with default url
        List<Repository> kt = Select
                .from(Repository.class)
                .where(Condition.prop("url")
                        .eq(DEFAULT_REPOSITORY_URL))
                .list();
        if (kt.size() != 0) {
            // If found, ignore below
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Save record to database
            Repository defaultRepository = new Repository(DEFAULT_REPOSITORY_URL, "KT");
            defaultRepository.save();

            // Load file from assets and save to file system
            inputStream = getAssets().open("test.xml");
            File file = new File(BaseApplication.context().getFilesDir(), defaultRepository.getFileName());
            outputStream = new FileOutputStream(file);

            // Copying
            IOUtils.copy(inputStream, outputStream);

            // Set available to true and SAVE
            defaultRepository.setAvailable(true);
            defaultRepository.save();
        } catch (FileNotFoundException e) {
            Log.e(DEBUG_TAG, e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(DEBUG_TAG, e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void upgradeFavoriteDatabaseIfExists() {
        // Find old database file
        File oldDatabaseFile = getDatabasePath("mydb.db");
        if (!oldDatabaseFile.exists()) {
            // If file does not exist, ignore below
            return;
        }

        try {
            // Read the old favorite database and table cursor
            SQLiteDatabase oldDatabase
                    = SQLiteDatabase.openDatabase(oldDatabaseFile.getPath(), null, 0);
            Cursor cursor = oldDatabase.query(
                    "favorites",                    // table name
                    new String[]{"string", "note"}, // columns
                    null, null, null, null, null
            );

            // Read all favorites
            List<Favorite> favorites = new ArrayList<Favorite>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String emoticon = cursor.getString(0);
                String description = cursor.getString(1);
                favorites.add(new Favorite(emoticon, description, ""));
                cursor.moveToNext();
            }
            cursor.close();

            // SAVE
            for (Favorite favorite : favorites) {
                favorite.save();
            }

            // Remove the database
            if (oldDatabaseFile.delete()) {
                showSnackBar(R.string.old_favorites_merged);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void refreshUiWithCurrentState() {
        int listItemId = mState.getItemId();

        // Primary items

        if (listItemId == LIST_ITEM_FAVORITE_ID) {
            mToolbar.setTitle(R.string.fav);
            replaceMainContainer(new FavoriteFragment());
            closeDrawers();
        }

        if (listItemId == LIST_ITEM_HISTORY_ID) {
            mToolbar.setTitle(R.string.history);
            replaceMainContainer(new HistoryFragment());
            closeDrawers();
        }

        if (listItemId == LIST_ITEM_BUILT_IN_EMOJI_ID) {
            mToolbar.setTitle(R.string.built_in_emoji);
            replaceMainContainer(new EmojiconsFragment());
            closeDrawers();
        }

        if (listItemId == LIST_ITEM_REPOSITORIES) {
            mToolbar.setTitle(R.string.repositories);
            replaceMainContainer(RepositoriesFragment.newInstance(mState.getSourceCache()));
            closeDrawers();
        }

        // Secondary items

        if (listItemId == LIST_ITEM_REPO_MANAGER_ID) {
            Intent intent = new Intent(this, RepositoryManagerActivity.class);
            startActivityForResult(intent, REPOSITORY_MANAGER_REQUEST_CODE);
            mState.revertToPreviousId();
        }

        if (listItemId == LIST_ITEM_SETTINGS_ID) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivityForResult(intent, PREFERENCE_REQUEST_CODE);
            mState.revertToPreviousId();
        }

        if (listItemId == LIST_ITEM_EXIT_ID) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancel(PERSISTENT_NOTIFICATION_ID);
            finish();
            mState.revertToPreviousId();
        }

        if (listItemId == LIST_ITEM_ACCOUNT_ID) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivityForResult(intent, ACCOUNT_REQUEST_CODE);
            mState.revertToPreviousId();
        }

        if (listItemId == LIST_ITEM_REPO_STORE_ID) {
            Intent intent = new Intent(this, RepositoryStoreActivity.class);
            startActivityForResult(intent, REPOSITORY_STORE_REQUEST_CODE);
            mState.revertToPreviousId();
        }

        if (listItemId == LIST_ITEM_UPDATE_CHECKER_ID) {
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
            mState.revertToPreviousId();
        }
    }

    @Override
    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
        mState.setItemId(iDrawerItem.getIdentifier());
        refreshUiWithCurrentState();
        return true;
    }
}