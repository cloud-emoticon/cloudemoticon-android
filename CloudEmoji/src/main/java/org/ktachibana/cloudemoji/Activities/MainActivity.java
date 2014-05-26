package org.ktachibana.cloudemoji.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orm.SugarApp;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.CategoryClickedEvent;
import org.ktachibana.cloudemoji.events.EmoticonCopiedEvent;
import org.ktachibana.cloudemoji.events.RepositoryClickedEvent;
import org.ktachibana.cloudemoji.events.RepositoryParsedEvent;
import org.ktachibana.cloudemoji.fragments.FavoriteFragment;
import org.ktachibana.cloudemoji.fragments.HistoryFragment;
import org.ktachibana.cloudemoji.fragments.LeftDrawerFragment;
import org.ktachibana.cloudemoji.fragments.SourceFragment;
import org.ktachibana.cloudemoji.helpers.NotificationHelper;
import org.ktachibana.cloudemoji.helpers.SourceXmlParser;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.models.Source;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements
        Constants,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Views
    @Optional // Optional because on split view it doesn't exist
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private SourceFragment mCurrentSourceFragment;

    // etc
    private SharedPreferences mPreferences;
    private boolean mIsDrawerStatic;
    private long mCurrentRepositoryId;
    private long mCurrentCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        EventBus.getDefault().register(this);

        // Choose layout to inflate
        setupLayout();

        // Setup views
        setupViews();

        // Toggle notification state
        switchNotificationState();

        // Check first time run
        firstTimeCheck();

    }

    private void setupLayout() {
        // Set up UI layout according to user preference for drawer and split view
        String uiPreference = mPreferences.getString(PREF_SPLIT_VIEW, "auto");
        int orientation = getResources().getConfiguration().orientation;

        // If auto, set up the default layout optimized for landscape and tablets
        if (uiPreference.equals("auto")) {
            setContentView(R.layout.activity_main);
        }

        // If split_in_port, only manually set up split view if detected portrait
        else if (uiPreference.equals("split_in_port")) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setContentView(R.layout.activity_main_manual_split_view);
            } else {
                setContentView(R.layout.activity_main_manual_navigation_drawer);
            }
        }

        // If split_in_land, only manually set up split view if detected landscape
        else if (uiPreference.equals("split_in_land")) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.activity_main_manual_split_view);
            } else {
                setContentView((R.layout.activity_main_manual_navigation_drawer));
            }
        }

        // Else split_in_both, manually set up split view for both orientations
        else {
            setContentView(R.layout.activity_main_manual_split_view);
        }
    }

    private void setupViews() {
        ButterKnife.inject(this);

        // If mDrawerLayout not found, then the drawer is static
        mIsDrawerStatic = (mDrawerLayout == null);

        // Set up toggle
        if (!mIsDrawerStatic) {
            toggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    R.drawable.ic_ab_navigation_drawer,
                    R.string.app_name,
                    R.string.app_name);
            mDrawerLayout.setDrawerListener(toggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = mPreferences.getBoolean(PREF_HAS_RUN_BEFORE, false);

        // If hasn't run before
        if (!hasRunBefore) {
            upgradeFavoriteDatabase();
            saveDefaultRepo();

            // It has run
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_HAS_RUN_BEFORE, true);
            editor.commit();
        }
    }

    private void saveDefaultRepo() {
        // TODO: read default xml from assets
    }

    private void upgradeFavoriteDatabase() {
        // TODO: upgrade favorite database if exists
    }

    private void setupLeftDrawer() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftDrawer, new LeftDrawerFragment())
                .commit();
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
        // Inflate the menu
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (!mIsDrawerStatic) {
            if (toggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        switch (item.getItemId()) {
            case R.id.action_repository_manager: {
                Intent intent = new Intent(this, RepositoryManagerActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_exit: {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancel(PERSISTENT_NOTIFICATION_ID);
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!mIsDrawerStatic) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mIsDrawerStatic) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Listens for any string copied and send it to clipboard
     *
     * @param event string copied event
     */
    @SuppressWarnings("deprecation")
    public void onEvent(EmoticonCopiedEvent event) {
        String copied = event.getEmoticon();

        int SDK = Build.VERSION.SDK_INT;
        // Below 3.0
        if (SDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard
                    = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copied);
        }

        // Above 3.0
        else {
            android.content.ClipboardManager clipboard
                    = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
            clipboard.setPrimaryClip(clip);
        }

        // Show toast
        Toast.makeText(MainActivity.this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        boolean isCloseAfterCopy = mPreferences.getBoolean(PREF_CLOSE_AFTER_COPY, true);

        // Close if you want
        if (isCloseAfterCopy) {
            finish();
        }
    }

    /**
     * Listens for any repository list item clicked (namely from left drawer fragment)
     * @param event repository list item clicked
     */
    public void onEvent(RepositoryClickedEvent event) {
        // Get id of the repository
        long id = event.getId();

        /**
         * If the id is special for favorite, i.e. -1
         * This is not possible for an id in database
         */
        if (id == LIST_ITEM_FAVORITE_ID) {
            replaceMainContainer(new FavoriteFragment());
            mDrawerLayout.closeDrawers();
        }

        // Same as above except for it is history
        else if (id == LIST_ITEM_HISTORY_ID) {
            replaceMainContainer(new HistoryFragment());
            mDrawerLayout.closeDrawers();
        }

        // Else it is an repository
        else {
            // Get repository file name
            String fileName = Repository.findById(Repository.class, id).getFileName();

            // Read the file from file system
            File file = new File(SugarApp.getSugarContext().getFilesDir(), fileName);

            // Read it
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                try {
                    // Parse source from file
                    Source source = new SourceXmlParser().parse(fileReader);

                    // Fill main container with this repository
                    mCurrentSourceFragment = SourceFragment.newInstance(source);
                    replaceMainContainer(mCurrentSourceFragment);

                    /**
                     * Tell anybody who cares about a repository being parsed
                     * Namely the anybody is left drawer who wants to display categories as well
                     */
                    EventBus.getDefault().post(new RepositoryParsedEvent(source));
                }

                // Parser error
                catch (XmlPullParserException e) {
                    Toast.makeText(this, getString(R.string.invalid_repo_format), Toast.LENGTH_SHORT)
                            .show();
                } catch (IOException e) {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                IOUtils.closeQuietly(fileReader);
            }
        }
    }

    /**
     * Listens for any category list item clicked (namely from left drawer fragment)
     * @param event category list item clicked
     */
    public void onEvent(CategoryClickedEvent event) {
        if (mCurrentSourceFragment != null) {
            mCurrentSourceFragment.getListView().setSelection(event.getIndex());
            mDrawerLayout.closeDrawers();
        }
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLeftDrawer();
    }
}