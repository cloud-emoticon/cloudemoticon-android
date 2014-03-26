package org.ktachibana.cloudemoji.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SectionedMenuAdapter;
import org.ktachibana.cloudemoji.databases.FavoritesDataSource;
import org.ktachibana.cloudemoji.fragments.CategoryListFragment;
import org.ktachibana.cloudemoji.fragments.FavoritesFragment;
import org.ktachibana.cloudemoji.helpers.MyMenuItem;
import org.ktachibana.cloudemoji.helpers.NotificationHelper;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser.Emoji;
import org.ktachibana.cloudemoji.interfaces.OnCopyToClipBoardListener;
import org.ktachibana.cloudemoji.interfaces.OnExceptionListener;
import org.ktachibana.cloudemoji.interfaces.OnFavoritesDatabaseOperationsListener;
import org.xmlpull.v1.XmlPullParserException;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        CategoryListFragment.OnRefreshStartedListener,
        OnExceptionListener,
        OnCopyToClipBoardListener,
        OnFavoritesDatabaseOperationsListener {

    // Constants
    public static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String XML_FILE_NAME = "emoji.xml";

    // Preferences
    private SharedPreferences preferences;
    private String notificationVisibility;
    private String url;
    private boolean overrideSystemFont;

    // UI components
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private ActionBarDrawerToggle toggle;
    private boolean isDrawerStatic;
    private PullToRefreshLayout refreshingPullToRefreshLayout;

    // Databases
    private FavoritesDataSource favoritesDataSource = new FavoritesDataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializations
        setupPreferences();
        setupUI();
        setupComponents();

        // Switch states
        switchNotificationState();

        firstTimeCheck();
        fillNavigationDrawer();

        // If not coming from previous sessions
        if (savedInstanceState == null) {
            updateMainContainerAndMenuItems(new MyMenuItem(getString(R.string.my_fav), MyMenuItem.FAV_TYPE));
        }

    }

    private void setupPreferences() {
        // Set up preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationVisibility = preferences.getString(SettingsActivity.PREF_NOTIFICATION_VISIBILITY, "both");
        url = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));
        overrideSystemFont = preferences.getBoolean(SettingsActivity.PREF_OVERRIDE_SYSTEM_FONT, true);
    }

    private void setupUI() {
        // Set up UI layout according to user preference for drawer and split view
        String uiPreference = preferences.getString(SettingsActivity.PREF_SPLIT_VIEW, "auto");
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

        // If split_in_both, manually set up split view for both orientations
        else if (uiPreference.equals("split_in_both")) {
            setContentView(R.layout.activity_main_manual_split_view);
        } else {
            promptException(new Exception("setupUI() bug"));
        }
    }

    private void setupComponents() {
        // Find views
        leftDrawer = (ListView) findViewById(R.id.leftDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // If drawerLayout not found, then the drawer is static
        isDrawerStatic = (drawerLayout == null);

        // Set up toggle
        if (!isDrawerStatic) {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_navigation_drawer, R.string.app_name, R.string.app_name);
            drawerLayout.setDrawerListener(toggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = preferences.getBoolean(SettingsActivity.PREF_HAS_RUN_BEFORE, false);
        // Hasn't run before
        if (!hasRunBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SettingsActivity.PREF_HAS_RUN_BEFORE, true);
            editor.commit();
        }
    }

    /**
     * Download XML file from user's preferred URL and replace the local one
     */
    private void update() {
        new UpdateRepoTask().execute(url);
    }

    /**
     * AsyncTask that fetches an XML file given a URL and replace the local one
     * This module uses Apache Commons IO from http://commons.apache.org/proper/commons-io/
     */
    private class UpdateRepoTask extends AsyncTask<String, Void, Void> {

        private List<Exception> taskExceptions;

        @Override
        protected void onPreExecute() {
            taskExceptions = new ArrayList<Exception>();
        }

        @Override
        protected Void doInBackground(String... stringUrl) {
            HttpURLConnection conn = null;
            Reader reader = null;
            OutputStream fileOut = null;
            try {
                // Establish connection
                URL url = new URL(stringUrl[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.connect();

                // Over-write existing file
                reader = new InputStreamReader(conn.getInputStream());
                fileOut = openFileOutput(XML_FILE_NAME, Context.MODE_PRIVATE);
                IOUtils.copy(reader, fileOut);
            } catch (IOException e) {
                taskExceptions.add(e);
            } catch (Exception e) {
                taskExceptions.add(e);
            } finally {
                IOUtils.close(conn);
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(fileOut);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Stop the refreshing pull to refresh layout
            if (refreshingPullToRefreshLayout != null) {
                refreshingPullToRefreshLayout.setRefreshComplete();
            }

            // If update finishes without exceptions
            if (taskExceptions.isEmpty()) {
                fillNavigationDrawer();
                if (!isDrawerStatic) {
                    drawerLayout.openDrawer(leftDrawer);
                }
                Toast.makeText(MainActivity.this, getString(R.string.updated), Toast.LENGTH_SHORT).show();
            } else {
                promptException(taskExceptions.get(0));
            }
        }
    }

    /**
     * Read emoji from a given file and return it
     * Handle exceptions by its own so that XmlPullParserException is not covered by IOException
     * This module uses Apache Commons IO from http://commons.apache.org/proper/commons-io/
     *
     * @param file File object
     * @return Emoji object
     */
    private Emoji readEmoji(File file) {
        FileInputStream fileIn = null;
        Reader reader = null;
        Emoji emoji = null;
        try {
            fileIn = new FileInputStream(file);
            reader = new InputStreamReader(fileIn);
            emoji = new RepoXmlParser().parse(reader);
        } catch (FileNotFoundException e) {
            promptException(e);
        } catch (XmlPullParserException e) {
            promptException(e);
        } catch (IOException e) {
            promptException(e);
        } finally {
            IOUtils.closeQuietly(fileIn);
            IOUtils.closeQuietly(reader);
        }
        return emoji;
    }

    /**
     * Fill the navigation drawer with categories read from local XML file
     */
    private void fillNavigationDrawer() {
        // Read file from local storage
        File file = new File(getFilesDir(), XML_FILE_NAME);
        if (!file.exists()) {
            update();
        }
        Emoji emoji = readEmoji(file);
        if (emoji != null) {
            // Fill leftDrawer
            leftDrawer.setAdapter(new SectionedMenuAdapter(this, emoji));
            leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyMenuItem menuItem = (MyMenuItem) parent.getAdapter().getItem(position);
                    updateMainContainerAndMenuItems(menuItem);
                    if (!isDrawerStatic) {
                        drawerLayout.closeDrawers();
                    }
                }
            });
        }
    }

    /**
     * Replace the main container with a fragment and correct actionbar
     *
     * @param menuItem Menu item being pressed on
     */
    private void updateMainContainerAndMenuItems(MyMenuItem menuItem) {
        int type = menuItem.getType();
        if (type != MyMenuItem.SECTION_HEADER_TYPE) {
            getSupportActionBar().setTitle(menuItem.getItemName());
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (type == MyMenuItem.FAV_TYPE) {
                fragmentManager.beginTransaction().replace(R.id.mainContainer, new FavoritesFragment()).commit();
            } else if (type == MyMenuItem.CATEGORY_TYPE) {
                fragmentManager.beginTransaction().replace(R.id.mainContainer, CategoryListFragment.newInstance(menuItem.getCategory())).commit();
            }
            supportInvalidateOptionsMenu();
        }
    }

    private void switchNotificationState() {
        NotificationHelper.switchNotificationState(this, notificationVisibility);
    }

    /**
     * Show a toast given a type of exception
     *
     * @param e exception
     */
    private void promptException(Exception e) {
        String prompt;
        if (e instanceof XmlPullParserException) {
            prompt = getString(R.string.wrong_xml);
        } else if (e instanceof FileNotFoundException) {
            prompt = getString(R.string.file_not_found);
        } else if (e instanceof IOException) {
            prompt = getString(R.string.bad_conn);
        } else {
            prompt = getString(R.string.fail) + e.toString();
        }
        Toast.makeText(MainActivity.this, prompt, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (key.equals(SettingsActivity.PREF_NOTIFICATION_VISIBILITY)) {
            notificationVisibility = preferences.getString(key, "both");
            switchNotificationState();
        } else if (key.equals(SettingsActivity.PREF_TEST_MY_REPO)) {
            url = preferences.getString(key, getString(R.string.default_url));
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
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_exit: {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(PERSISTENT_NOTIFICATION_ID);
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

    /**
     * Implemented from OnExceptionListener
     *
     * @param e Exception handled
     */
    @Override
    public void onException(Exception e) {
        promptException(e);
    }

    /**
     * Implemented from CategoryListFragment.OnRefreshStartedListener
     *
     * @param layout Layout being pulled
     */
    @Override
    public void onRefreshStarted(PullToRefreshLayout layout) {
        refreshingPullToRefreshLayout = layout;
        update();
    }

    /**
     * Implemented from OnCopyToClipBoardListener
     *
     * @param copied String copied
     */
    @Override
    public void onCopyToClipBoard(String copied) {
        // Below 3.0
        int SDK = Build.VERSION.SDK_INT;
        if (SDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copied);
        }

        // Above 3.0
        else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(MainActivity.this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        boolean isCloseAfterCopy = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
        if (isCloseAfterCopy) {
            finish();
        }
    }

    /**
     * Implemented from OnFavoritesDatabaseOperationsListener
     *
     * @param newEntry new entry to be added
     */
    @Override
    public void onAddEntry(RepoXmlParser.Entry newEntry) {
        try {
            favoritesDataSource.open();
            if (favoritesDataSource.addEntry(newEntry)) {
                Toast.makeText(this, R.string.added_to_fav, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.already_added_to_fav, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            promptException(e);
        }
    }

    /**
     * Implemented from OnFavoritesDatabaseOperationsListener
     *
     * @param string string
     * @return Entry found by string
     */
    @Override
    public RepoXmlParser.Entry onGetEntryByString(String string) {
        RepoXmlParser.Entry entry = null;
        try {
            favoritesDataSource.open();
            entry = favoritesDataSource.getEntryByString(string);
            favoritesDataSource.close();
        } catch (SQLException e) {
            promptException(e);
        }
        return entry;
    }

    /**
     * Implemented from OnFavoritesDatabaseOperationsListener
     *
     * @return All entries
     */
    @Override
    public List<RepoXmlParser.Entry> onGetAllEntries() {
        List<RepoXmlParser.Entry> entries = null;
        try {
            favoritesDataSource.open();
            entries = favoritesDataSource.getAllEntries();
            favoritesDataSource.close();
        } catch (SQLException e) {
            promptException(e);
        }
        return entries;
    }

    /**
     * Implemented from OnFavoritesDatabaseOperationsListener
     *
     * @param string   string of the old entry
     * @param newEntry new entry to be replace
     */
    @Override
    public void onUpdateEntryByString(String string, RepoXmlParser.Entry newEntry) {
        try {
            favoritesDataSource.open();
            favoritesDataSource.updateEntryByString(string, newEntry);
            Toast.makeText(this, R.string.entry_updated, Toast.LENGTH_SHORT).show();
            favoritesDataSource.close();
        } catch (SQLException e) {
            promptException(e);
        }
    }

    /**
     * Implemented from OnFavoritesDatabaseOperationsListener
     *
     * @param string an entry with the string (unique)
     */
    @Override
    public void onRemoveEntryByString(String string) {
        try {
            favoritesDataSource.open();
            favoritesDataSource.removeEntryByString(string);
            Toast.makeText(this, R.string.removed_from_fav, Toast.LENGTH_SHORT).show();
            favoritesDataSource.close();
        } catch (SQLException e) {
            promptException(e);
        }
    }
}
