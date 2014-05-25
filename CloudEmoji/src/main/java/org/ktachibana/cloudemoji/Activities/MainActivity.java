package org.ktachibana.cloudemoji.activities;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.SugarApp;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryClickedEvent;
import org.ktachibana.cloudemoji.events.StringCopiedEvent;
import org.ktachibana.cloudemoji.fragments.FavoriteFragment;
import org.ktachibana.cloudemoji.fragments.HistoryFragment;
import org.ktachibana.cloudemoji.fragments.LeftDrawerFragment;
import org.ktachibana.cloudemoji.fragments.SourceFragment;
import org.ktachibana.cloudemoji.helpers.NotificationHelper;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;
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
    @Optional
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    // etc
    private SharedPreferences preferences;
    private boolean isDrawerStatic;
    private long currentRepositoryId;
    private long currentCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        EventBus.getDefault().register(this);

        // Initialize layout
        setupLayout();

        // Initialize views
        setupViews();

        // Toggle notification state
        switchNotificationState();

        // Check first time run
        firstTimeCheck();

        if (savedInstanceState == null) setupDrawer();
    }

    private void setupLayout() {
        // Set up UI layout according to user preference for drawer and split view
        String uiPreference = preferences.getString(PREF_SPLIT_VIEW, "auto");
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

        // If drawerLayout not found, then the drawer is static
        isDrawerStatic = (drawerLayout == null);

        // Set up toggle
        if (!isDrawerStatic) {
            toggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.drawable.ic_ab_navigation_drawer,
                    R.string.app_name,
                    R.string.app_name);
            drawerLayout.setDrawerListener(toggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = preferences.getBoolean(PREF_HAS_RUN_BEFORE, false);
        // If hasn't run before
        if (!hasRunBefore) {
            upgradeFavoriteDatabase();
            downloadAndSaveDefaultRepo();

            // Change has run before to true
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PREF_HAS_RUN_BEFORE, true);
            editor.commit();
        }
    }

    private void downloadAndSaveDefaultRepo() {
        final Repository kt = new Repository(this, DEFAULT_REPOSITORY_URL, "KT");
        kt.save();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.downloading);
        dialog.setMessage(kt.getUrl());
        dialog.show();
        Ion.with(SugarApp.getSugarContext())
                .load(kt.getUrl())
                .write(new File(SugarApp.getSugarContext().getFilesDir(), kt.getFileName()))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e == null) {
                            kt.setAvailable(true);
                            kt.save();
                        }
                        dialog.dismiss();
                        setupDrawer();
                    }
                });
    }

    private void upgradeFavoriteDatabase() {

    }

    private void setupDrawer() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftDrawer, new LeftDrawerFragment())
                .commit();
    }

    private void switchNotificationState() {
        NotificationHelper
                .switchNotificationState(this,
                        preferences.getString(PREF_NOTIFICATION_VISIBILITY, "both"));
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
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.registerOnSharedPreferenceChangeListener(this);
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
        if (!isDrawerStatic) {
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
        if (!isDrawerStatic) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isDrawerStatic) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    @SuppressWarnings("deprecation")
    public void onEvent(StringCopiedEvent event) {
        String copied = event.getString();

        // Below 3.0
        int SDK = Build.VERSION.SDK_INT;
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
        boolean isCloseAfterCopy = preferences.getBoolean(PREF_CLOSE_AFTER_COPY, true);
        if (isCloseAfterCopy) {
            finish();
        }
    }

    public void onEvent(RepositoryClickedEvent event) {
        long id = event.getId();
        if (id == LIST_ITEM_FAVORITE_ID) {
            replaceMainContainer(new FavoriteFragment());
            drawerLayout.closeDrawers();
        } else if (id == LIST_ITEM_HISTORY_ID) {
            replaceMainContainer(new HistoryFragment());
            drawerLayout.closeDrawers();
        } else {
            // Get repository file
            String fileName = Repository.findById(Repository.class, id).getFileName();
            File file = new File(SugarApp.getSugarContext().getFilesDir(), fileName);
            FileReader fileReader = null;

            // Read it
            try {
                fileReader = new FileReader(file);
                try {
                    Source source = new RepoXmlParser().parse(fileReader);
                    replaceMainContainer(SourceFragment.newInstance(source));
                } catch (XmlPullParserException e) {
                    Toast.makeText(this, getString(R.string.invalid_repo_format), Toast.LENGTH_SHORT).show();
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
        setupDrawer();
    }
}