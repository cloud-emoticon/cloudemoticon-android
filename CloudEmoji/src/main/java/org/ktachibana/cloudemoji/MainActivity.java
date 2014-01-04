package org.ktachibana.cloudemoji;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import net.simonvt.menudrawer.MenuDrawer;
import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;
import org.xmlpull.v1.XmlPullParserException;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * This module uses menudrawer from https://github.com/SimonVT/android-menudrawer
 * Test from master branch
 */
public class MainActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        DoubleItemListFragment.OnRefreshStartedListener,
        OnExceptionListener,
        OnCopyToClipBoardListener {

    private static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String ACTIONBAR_TITLE_TAG = "menudrawerTitle";
    private static final String CONTAINER_FRAGMENT_TAG = "fragment";
    private static final String XML_FILE_NAME = "emoji.xml";

    private SharedPreferences preferences;
    private NotificationManager notificationManager;
    private PullToRefreshLayout refreshingPullToRefreshLayout;
    private MenuDrawer menuDrawer;

    private String notificationVisibility;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializations
        init();
        switchNotificationState();
        firstTimeCheck();

        // Fill drawer
        fillMenuDrawer();

        // If not coming from previous sessions
        if (savedInstanceState == null) {
            replaceFragment(new FavFragment());
        }

    }

    private void init() {
        // Set up preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationVisibility = preferences.getString(SettingsActivity.PREF_NOTIFICATION_VISIBILITY, "both");
        url = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));

        // Set up menu drawer
        if (!preferences.getBoolean(SettingsActivity.PREF_SPLIT_VIEW, false)) {
            menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
        } else {
            menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.STATIC);
        }
        menuDrawer.setContentView(R.layout.main_activity_layout);
        menuDrawer.setMenuView(R.layout.menu_drawer_layout);
        menuDrawer.setDropShadowEnabled(false);
    }

    /**
     * Build notification with a given priority
     * @param priority priority from Notification.priority
     * @return a Notification object
     */
    private Notification buildNotification(int priority) {
        String title = getString(R.string.app_name);
        String text = getString(R.string.touch_to_launch);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return new NotificationCompat.Builder(this)
                .setContentTitle(title)                     // Title
                .setContentText(text)                       // Text
                .setSmallIcon(R.drawable.ic_notification)   // Icon
                .setContentIntent(pIntent)                  // Intent to launch this app
                .setWhen(0)                                 // No time to display
                .setPriority(priority)                      // Given priority
                .build();
    }

    private void firstTimeCheck() {
        boolean hasRunBefore = preferences.getBoolean(SettingsActivity.PREF_HAS_RUN_BEFORE, false);
        // Hasn't run before
        if (!hasRunBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SettingsActivity.PREF_HAS_RUN_BEFORE, true);
            editor.commit();
            menuDrawer.openMenu(true);
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
                fillMenuDrawer();
                menuDrawer.openMenu(true);
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
     * Fill the menu drawer with categories read from local XML file
     */
    private void fillMenuDrawer() {

        // Set up local list view
        ListView localListView = (ListView) menuDrawer.getMenuView().findViewById(R.id.menuDrawerLocalView);
        String[] localOptions = new String[]{getString(R.string.my_fav)};
        ArrayAdapter<String> localAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, localOptions);
        localListView.setAdapter(localAdapter);
        localListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    replaceFragment(new FavFragment());
                    menuDrawer.closeMenu(true);
                    menuDrawer.closeMenu(true);
                }
            }
        });

        // Set up repository list view
        // Read saved XML from local storage
        File file = new File(getFilesDir(), XML_FILE_NAME);

        // If file does not exist
        if (!file.exists()) {
            update();
        }

        // If emoji is read correctly
        Emoji emoji = readEmoji(file);
        if (emoji != null) {
            // Retrieve categories from it
            List<RepoXmlParser.Category> categories = emoji.categories;

            // Get the "repository" list view
            ListView repoListView = (ListView) menuDrawer.getMenuView().findViewById(R.id.menuDrawerListView);

            // Set up this list view
            ArrayAdapter<RepoXmlParser.Category> repoAdapter = new ArrayAdapter<RepoXmlParser.Category>(this, android.R.layout.simple_list_item_1);
            for (RepoXmlParser.Category cat : categories) {
                repoAdapter.add(cat);
            }
            repoListView.setAdapter(repoAdapter);

            // What happens when an item in the list view is clicked
            repoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Retrieve one category
                    Adapter adapter = parent.getAdapter();
                    RepoXmlParser.Category cat = (RepoXmlParser.Category) adapter.getItem(position);

                    // Create the fragment
                    DoubleItemListFragment fragment = DoubleItemListFragment.newInstance(cat);
                    replaceFragment(fragment);
                    menuDrawer.closeMenu(true);
                }
            });
        }
    }

    /**
     * Replace the main container with a fragment
     *
     * @param fragment Fragment to be displayed
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, CONTAINER_FRAGMENT_TAG).commit();
        if (fragment instanceof FavFragment) {
            getSupportActionBar().setTitle(getString(R.string.local) + ": " + getString(R.string.my_fav));
        } else if (fragment instanceof DoubleItemListFragment) {
            String categoryName = ((RepoXmlParser.Category) fragment.getArguments().getSerializable(DoubleItemListFragment.CAT_KEY)).name;
            getSupportActionBar().setTitle(getString(R.string.repositories) + ": " + categoryName);
        }
    }


    /**
     * Switch notification state to according to current user preference
     */
    private void switchNotificationState() {
        // Cancel current notification
        notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
        if (notificationVisibility.equals("no")) {
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
        } else if (notificationVisibility.equals("panel")) {
            Notification notification = buildNotification(Notification.PRIORITY_MIN);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        } else if (notificationVisibility.equals("both")) {
            Notification notification = buildNotification(Notification.PRIORITY_DEFAULT);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        }
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
        Log.e("CloudEmoji", e.toString());
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIONBAR_TITLE_TAG, getSupportActionBar().getTitle().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        getSupportActionBar().setTitle(inState.getString(ACTIONBAR_TITLE_TAG));
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
}
