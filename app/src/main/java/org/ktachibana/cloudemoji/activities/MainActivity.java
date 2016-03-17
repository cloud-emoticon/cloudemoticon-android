package org.ktachibana.cloudemoji.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.BuildConfig;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.fragments.RepositoriesFragmentBuilder;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.net.VersionCodeCheckerClient;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.utils.NotificationHelper;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.Subscribe;

public class MainActivity extends BaseActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String STATE_TAG = "state";
    public static final String SOURCE_CACHE_TAG = "source_cache";
    private MainActivityState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Setup notification state
        setupNotificationState();

        // Check first time run
        firstTimeCheck();

        // If not starting from refresh new, get state
        // Else, initialize
        if (savedInstanceState != null) {
            mState = savedInstanceState.getParcelable(STATE_TAG);
        } else {
            mState = new MainActivityState(initializeCache());
        }

        // Show
        mToolbar.setTitle(R.string.app_name);
        replaceMainContainer(new RepositoriesFragmentBuilder(mState.getSourceCache()).build());
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
                    showSnackBar(getString(R.string.invalid_repo_format));
                } catch (Exception e) {
                    Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }

        return cache;
    }

    private void setupNotificationState() {
        NotificationHelper
                .switchNotificationState(this,
                        mPreferences.getString(Constants.PREF_NOTIFICATION_VISIBILITY, "both"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (Constants.PREF_NOTIFICATION_VISIBILITY.equals(key)) {
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
            intent.putExtra(SOURCE_CACHE_TAG, Parcels.wrap(mState.getSourceCache()));
            startActivity(intent);
            return true;
        } else if (id == R.id.repo_manager) {
            Intent intent = new Intent(this, RepositoryManagerActivity.class);
            startActivityForResult(intent, Constants.REPOSITORY_MANAGER_REQUEST_CODE);
            return true;
        } else if (id == R.id.repo_store) {
            Intent intent = new Intent(this, RepositoryStoreActivity.class);
            startActivityForResult(intent, Constants.REPOSITORY_STORE_REQUEST_CODE);
            return true;
        } else if (id == R.id.update_checker) {
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
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivityForResult(intent, Constants.PREFERENCE_REQUEST_CODE);
            return true;
        } else if (id == R.id.exit) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancel(Constants.PERSISTENT_NOTIFICATION_ID);
            finish();
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
        int versionCode = BuildConfig.VERSION_CODE;

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
                        intent.setData(Uri.parse(Constants.PLAY_STORE_URL));
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Coming back from repository manager or repository store, repositories may be changed
        if (requestCode == Constants.REPOSITORY_MANAGER_REQUEST_CODE ||
                requestCode == Constants.REPOSITORY_STORE_REQUEST_CODE) {

            // If currently showing repositories, refresh
            if (mState.getItemId() == Constants.LIST_ITEM_REPOSITORIES) {
                mState.setSourceCache(initializeCache());
                // TODO: 3/17/16
            }
        }

        // Coming back from preference, favorites may be changed
        if (requestCode == Constants.PREFERENCE_REQUEST_CODE) {

            // If currently showing favorites, refresh
            if (mState.getItemId() == Constants.LIST_ITEM_FAVORITE_ID) {
                // TODO: 3/17/16
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save current state
        outState.putParcelable(STATE_TAG, Parcels.wrap(mState));
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = mPreferences.getBoolean(Constants.PREF_HAS_RUN_BEFORE, false);

        upgradeFavoriteDatabaseIfExists();
        setupDefaultRepoIfNotExists();

        // If hasn't run before
        if (!hasRunBefore) {
            // It has run
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(Constants.PREF_HAS_RUN_BEFORE, true);
            editor.apply();
        }
    }

    @SuppressWarnings("unchecked")
    private void setupDefaultRepoIfNotExists() {
        // Find repository with default url
        List<Repository> kt = Select
                .from(Repository.class)
                .where(Condition.prop("url")
                        .eq(Constants.DEFAULT_REPOSITORY_URL))
                .list();
        if (kt.size() != 0) {
            // If found, ignore below
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Save record to database
            Repository defaultRepository = new Repository(Constants.DEFAULT_REPOSITORY_URL, "KT");
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
        } catch (IOException e) {
            Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage());
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
            List<Favorite> favorites = new ArrayList<>();
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
}