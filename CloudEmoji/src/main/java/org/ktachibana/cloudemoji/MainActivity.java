package org.ktachibana.cloudemoji;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;
import org.xmlpull.v1.XmlPullParserException;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        DoubleItemListFragment.OnRefreshStartedListener,
        OnExceptionListener,
        OnCopyToClipBoardListener {

    // Constants
    public static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String XML_FILE_NAME = "emoji.xml";

    // Preferences
    private SharedPreferences preferences;
    private String notificationVisibility;
    private String url;

    // UI components
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private ActionBarDrawerToggle toggle;
    private boolean isDrawerStatic;
    private PullToRefreshLayout refreshingPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Initializations
        setupPreferences();
        setupUI();
        switchNotificationState();
        firstTimeCheck();
        fillNavigationDrawer();

        // If not coming from previous sessions
        if (savedInstanceState == null) {
            updateMainContainer(new MyMenuItem(getString(R.string.my_fav), MyMenuItem.FAV_TYPE));
        }

    }

    private void setupPreferences() {
        // Set up preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationVisibility = preferences.getString(SettingsActivity.PREF_NOTIFICATION_VISIBILITY, "both");
        url = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));
    }

    private void setupUI() {
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
            leftDrawer.setAdapter(new SectionedMenuAdapter(emoji));
            leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyMenuItem menuItem = (MyMenuItem) parent.getAdapter().getItem(position);
                    updateMainContainer(menuItem);
                    if (!isDrawerStatic) {
                        drawerLayout.closeDrawers();
                    }
                }
            });
        }
    }

    /**
     * Replace the main container with a fragment and change actionbar title
     *
     * @param menuItem Menu item being pressed on
     */
    private void updateMainContainer(MyMenuItem menuItem) {
        int type = menuItem.getType();
        if (type != MyMenuItem.SECTION_HEADER_TYPE) {
            getSupportActionBar().setTitle(menuItem.getItemName());
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (type == MyMenuItem.FAV_TYPE) {
                fragmentManager.beginTransaction().replace(R.id.mainContainer, new FavFragment()).commit();
            } else if (type == MyMenuItem.CATEGORY_TYPE) {
                fragmentManager.beginTransaction().replace(R.id.mainContainer, DoubleItemListFragment.newInstance(menuItem.getCategory())).commit();
            }
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
            prompt = getString(R.string.fail);
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
            case R.id.action_refresh: {
                update();
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_exit: {
                finish();
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
     * Implements from OnExceptionListener
     *
     * @param e Exception handled
     */
    public void onException(Exception e) {
        promptException(e);
    }

    /**
     * Implements from DoubleItemListFragment.OnRefreshStartedListener
     *
     * @param layout Layout being pulled
     */
    public void onRefreshStarted(PullToRefreshLayout layout) {
        refreshingPullToRefreshLayout = layout;
        update();
    }

    /**
     * Implements from OnCopyToClipBoardListener
     *
     * @param copied String copied
     */
    public void copyToClipBoard(String copied) {
        // Below 3.0 support
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
     * A class for a menu item in menu drawer
     * Holding it's item name, its corresponding type and Category it's holding if the type is CATEGORY
     */
    private class MyMenuItem {
        public static final int SECTION_HEADER_TYPE = 0;
        public static final int CATEGORY_TYPE = 1;
        public static final int FAV_TYPE = 2;

        private String itemName;
        private int type;
        private RepoXmlParser.Category category;

        public MyMenuItem(String itemName, int type) {
            this.itemName = itemName;
            this.type = type;
        }

        public MyMenuItem(String itemName, int type, RepoXmlParser.Category category) {
            this.itemName = itemName;
            this.type = type;
            this.category = category;
        }

        public String getItemName() {
            return itemName;
        }

        public int getType() {
            return type;
        }

        public RepoXmlParser.Category getCategory() {
            return category;
        }
    }

    private class SectionedMenuAdapter implements ListAdapter {
        private List<MyMenuItem> menuItemMap;

        public SectionedMenuAdapter(Emoji data) {
            menuItemMap = new ArrayList<MyMenuItem>();
            // Put section header for "local"
            menuItemMap.add(new MyMenuItem(getResources().getString(R.string.local), MyMenuItem.SECTION_HEADER_TYPE));
            // Put my fav
            menuItemMap.add(new MyMenuItem(getResources().getString(R.string.my_fav), MyMenuItem.FAV_TYPE));
            // Put section header for "repository"
            menuItemMap.add(new MyMenuItem(getResources().getString(R.string.repositories), MyMenuItem.SECTION_HEADER_TYPE));
            // Put all other categories
            for (RepoXmlParser.Category category : data.categories) {
                menuItemMap.add(new MyMenuItem(category.name, MyMenuItem.CATEGORY_TYPE, category));
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return menuItemMap.get(position).getType() != MyMenuItem.SECTION_HEADER_TYPE;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return menuItemMap.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItemMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView) convertView;
            // Determine if the menu item is section header or list item
            MyMenuItem menuItem = menuItemMap.get(position);
            // If it is a section header
            if (menuItem.getType() == MyMenuItem.SECTION_HEADER_TYPE) {
                if (textView == null) {
                    textView = (TextView) inflater.inflate(R.layout.text_separator_style, parent, false);
                }
                String sectionName = menuItem.getItemName();
                textView.setText(sectionName);
            }
            // Else it is a list item
            else {
                if (textView == null) {
                    textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                String itemName = menuItem.getItemName();
                textView.setText(itemName);
            }
            return textView;
        }

        @Override
        public int getItemViewType(int position) {
            return (menuItemMap.get(position).getType() == MyMenuItem.SECTION_HEADER_TYPE) ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return menuItemMap.isEmpty();
        }
    }

}
