package org.ktachibana.cloudemoji.activities;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.events.UpdateCheckedEvent;
import org.ktachibana.cloudemoji.fragments.EmojiconsFragment;
import org.ktachibana.cloudemoji.fragments.FavoriteFragment;
import org.ktachibana.cloudemoji.fragments.HistoryFragment;
import org.ktachibana.cloudemoji.fragments.SourceFragment;
import org.ktachibana.cloudemoji.models.Favorite;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.parsing.FavoritesHelper;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.utils.NotificationHelper;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements
        Constants,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final long DEFAULT_REPOSITORY_ID = LIST_ITEM_FAVORITE_ID;
    private static final String STATE_TAG = "state";
    public static final String SOURCE_CACHE_TAG = "source_cache";
    // Views
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private Drawer.Result mDrawer;
    // State
    private MainActivityState mState;
    private SourceFragment mCurrentSourceFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // Setup views
        setupViews();

        // Toggle notification state
        switchNotificationState();

        // Check first time run
        firstTimeCheck();

        // If not starting from refresh new, get state
        if (savedInstanceState != null) {
            mState = savedInstanceState.getParcelable(STATE_TAG);
        }

        // Else, set it to display default
        else {
            mState = new MainActivityState(DEFAULT_REPOSITORY_ID, null, initializeCache());
        }

        // Setup left drawer with repository id and source
        setupLeftDrawer(mState);

        // Switch to the repository
        internalSwitchRepository();
    }

    private void setupViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AccountHeader.Result accountHeader = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_place_holder)
                .build();

        mDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(accountHeader)
                .build();
    }


    /**
     * Put every source in cache
     */
    private SourceInMemoryCache initializeCache() {
        SourceInMemoryCache cache = new SourceInMemoryCache();

        // Put favorites
        Source favoritesSource = FavoritesHelper.getFavoritesAsSource();
        cache.put(LIST_ITEM_FAVORITE_ID, favoritesSource);

        // Put all available repositories
        List<Repository> allRepositories = Repository.listAll(Repository.class);
        for (Repository repository : allRepositories) {
            if (repository.isAvailable()) {
                try {
                    long id = repository.getId();
                    Source source = new SourceReader().readSourceFromDatabaseId(id);
                    cache.put(id, source);
                } catch (SourceParsingException e) {
                    showSnackBar(getString(R.string.invalid_repo_format) + e.getFormatType().toString());
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }

        return cache;
    }

    private void setupLeftDrawer(MainActivityState state) {
        // TODO
        mDrawer.addItems(
                new PrimaryDrawerItem().withName(R.string.fav).withIcon(R.drawable.ic_favorite),
                new PrimaryDrawerItem().withName(R.string.history).withIcon(R.drawable.ic_history),
                new PrimaryDrawerItem().withName(R.string.built_in_emoji).withIcon(R.drawable.ic_built_in_emoji)
        );
        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

            }
        });
    }

    private void switchNotificationState() {
        NotificationHelper
                .switchNotificationState(this,
                        mPreferences.getString(PREF_NOTIFICATION_VISIBILITY, "both"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (PREF_NOTIFICATION_VISIBILITY.equals(key)) {
            switchNotificationState();
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

    public void onEvent(FavoriteAddedEvent event) {
        showSnackBar(event.getEmoticon() + "\n" + getString(R.string.added_to_fav));
    }

    public void onEvent(FavoriteDeletedEvent event) {
        showSnackBar(event.getEmoticon() + "\n" + getString(R.string.removed_from_fav));
    }

    public void onEvent(UpdateCheckedEvent event) {
        int latestVersionCode = event.getVercode();

        // If failed
        if (latestVersionCode == 0) {
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

    private void internalSwitchRepository() {
        // If it is a local repository
        if (mState.getRepositoryId() < 0) {
            // Nullify source fragment
            mCurrentSourceFragment = null;

            // Switch to correct fragment
            if (mState.getRepositoryId() == LIST_ITEM_FAVORITE_ID)
                replaceMainContainer(new FavoriteFragment());
            if (mState.getRepositoryId() == LIST_ITEM_HISTORY_ID)
                replaceMainContainer(new HistoryFragment());
            if (mState.getRepositoryId() == LIST_ITEM_BUILT_IN_EMOJI_ID)
                replaceMainContainer(new EmojiconsFragment());

            // Close drawers
            closeDrawers();
        }

        // Else it is a remote repository with a parsed source
        else {
            // Create source fragment and switch, notify left drawer
            if (mState.getSource() != null) {
                mCurrentSourceFragment = SourceFragment.newInstance(mState.getSource());
                replaceMainContainer(mCurrentSourceFragment);
            }

            // Do not close drawers
        }
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private void closeDrawers() {
        // TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Coming back from repository manager, repositories may be changed
        if (requestCode == REPOSITORY_MANAGER_REQUEST_CODE) {
            // TODO
        }

        // Coming back from preference, favorites may be changed
        else if (requestCode == PREFERENCE_REQUEST_CODE) {
            // TODO
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
            editor.commit();
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
            File file = new File(
                    SugarApp.getSugarContext().getFilesDir(), defaultRepository.getFileName());
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
}