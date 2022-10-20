package org.ktachibana.cloudemoji.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.BuildConfig;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.events.RepositoriesPagerItemSelectedEvent;
import org.ktachibana.cloudemoji.fragments.RepositoriesFragmentBuilder;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.net.VersionCodeCheckerClient;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.utils.NotificationUtils;
import org.ktachibana.cloudemoji.utils.SystemUtils;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SOURCE_CACHE_TAG = "sourceCache";
    private static final String CURRENT_ITEM_TAG = "currentItem";
    private LinkedHashMap<Long, Source> sourceCache;
    private int currentItem;
    private static final int RC_POST_NOTIFICATIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Check first time run
        firstTimeCheck();

        // If not starting from refresh new state
        // Else, initialize
        if (savedInstanceState != null) {
            sourceCache = Parcels.unwrap(savedInstanceState.getParcelable(SOURCE_CACHE_TAG));
            currentItem = savedInstanceState.getInt(CURRENT_ITEM_TAG);
        } else {
            sourceCache = initializeSourceCache();
            currentItem = 0;
        }

        // Show according to state
        render();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Setup notification
        setupNotification();
    }

    private void render() {
        replaceMainContainer(new RepositoriesFragmentBuilder(sourceCache, currentItem).build());
    }

    /**
     * Put every source into source cache
     */
    private LinkedHashMap<Long, Source> initializeSourceCache() {
        LinkedHashMap<Long, Source> sourceCache = new LinkedHashMap<>();

        List<Repository> allRepositories = Repository.listAll(Repository.class);
        for (Repository repository : allRepositories) {
            if (repository.isAvailable()) {
                try {
                    long id = repository.getId();
                    Source source =
                            new SourceReader().readSourceFromDatabaseId(repository.getAlias(), id);
                    sourceCache.put(id, source);
                } catch (SourceParsingException e) {
                    showSnackBar(getString(R.string.invalid_repo_format));
                } catch (Exception e) {
                    Log.e(Constants.DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }

        return sourceCache;
    }

    @AfterPermissionGranted(RC_POST_NOTIFICATIONS)
    private void setupNotification() {
        if (!SystemUtils.aboveTiramisu33()) {
            NotificationUtils.setupNotification(this);
            return;
        }
        String[] perms = {Manifest.permission.POST_NOTIFICATIONS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            NotificationUtils.setupNotification(this);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.notification_rationale), RC_POST_NOTIFICATIONS, perms);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Constants.PREF_NOTIFICATION_LEGACY_VISIBILITY.equals(key)) {
            setupNotification();
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
            intent.putExtra(SOURCE_CACHE_TAG, Parcels.wrap(sourceCache));
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
                    .cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
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

    @Subscribe
    public void handle(RepositoriesPagerItemSelectedEvent event) {
        currentItem = event.getItem();
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
            new MaterialDialog.Builder(MainActivity.this)
                    .title(String.format(getString(R.string.new_version_available), latestVersionCode))
                    .positiveText(R.string.go_to_play_store)
                    .negativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(Constants.PLAY_STORE_URL));
                            startActivity(intent);
                        }
                    })
                    .show();
        }
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

        // Coming back from repository manager
        // Repositories may be changed
        // Need to refresh source cache
        // Need to re-render all repositories
        if (requestCode == Constants.REPOSITORY_MANAGER_REQUEST_CODE) {
            sourceCache = initializeSourceCache();
            render();
        }

        // Coming back from preference
        // Favorites may be changed
        // Need to re-render favorites
        else if (requestCode == Constants.PREFERENCE_REQUEST_CODE) {
            render();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SOURCE_CACHE_TAG, Parcels.wrap(sourceCache));
        outState.putInt(CURRENT_ITEM_TAG, currentItem);
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = mPreferences.getBoolean(Constants.PREF_HAS_RUN_BEFORE, false);

        upgradeFavoriteDatabaseIfExists();
        setupDefaultRepoIfNecessary();

        // If hasn't run before
        if (!hasRunBefore) {
            // It has run
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(Constants.PREF_HAS_RUN_BEFORE, true);
            editor.apply();
        }
    }

    private void setupDefaultRepoIfNecessary() {
        if (Repository.listAll(Repository.class).size() != 0) {
            // If there are already repositories, ignore
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Save record to database
            Repository defaultRepository = new Repository(Constants.DEFAULT_REPOSITORY_URL, "KT's favorites");
            defaultRepository.save();

            // Load file from assets and save to file system
            inputStream = getAssets().open("default.json");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}