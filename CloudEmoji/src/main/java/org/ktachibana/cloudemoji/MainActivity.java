package org.ktachibana.cloudemoji;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import net.simonvt.menudrawer.MenuDrawer;
import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;
import org.xmlpull.v1.XmlPullParserException;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * This module uses menudrawer from https://github.com/SimonVT/android-menudrawer
 */
public class MainActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String CONTAINER_FRAGMENT_TAG = "fragment";
    private static final String XML_FILE_NAME = "emoji.xml";

    private SharedPreferences preferences;
    private NotificationManager notificationManager;
    private Notification notification;
    private PullToRefreshLayout refreshingPullToRefreshLayout;
    private MenuDrawer menuDrawer;

    private FavDataSource favDataSource;

    private String notificationVisibility;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializations
        init();
        buildNotification();
        switchNotificationState();
        firstTimeCheck();

        // Fill drawer without displaying the first fragment
        fillMenuDrawer();
        replaceFragment(new FavFragment());

    }

    private void init() {
        // Set up favorites data source
        favDataSource = new FavDataSource(this);

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

    private void buildNotification() {
        String title = getString(R.string.app_name);
        String text = getString(R.string.touch_to_launch);
        int icon = R.drawable.ic_launcher;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification = new NotificationCompat.Builder(this)
                .setContentTitle(title).setContentText(text).setSmallIcon(icon)
                .setContentIntent(pIntent).setWhen(0).build();
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
                    DoubleItemListFragment fragment = new DoubleItemListFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(DoubleItemListFragment.CAT_KEY, cat);
                    fragment.setArguments(args);
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
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, CONTAINER_FRAGMENT_TAG).commit();
        if (fragment instanceof FavFragment) {
            getSupportActionBar().setTitle(getString(R.string.local) + ": " + getString(R.string.my_fav));
        }
        else if (fragment instanceof DoubleItemListFragment) {
            String categoryName = ((RepoXmlParser.Category) fragment.getArguments().getSerializable(DoubleItemListFragment.CAT_KEY)).name;
            getSupportActionBar().setTitle(getString(R.string.repositories) + ": " + categoryName);
        }
    }

    /**
     * Fragment that holds a list of string in my favorites database
     */
    private class FavFragment extends Fragment {

        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate rootView
            View rootView = inflater.inflate(R.layout.my_fav_fragment_layout, container, false);

            // Find listView and update
            listView = (ListView) rootView.findViewById(R.id.favListView);
            listView.setEmptyView(rootView.findViewById(R.id.emptyView));
            registerForContextMenu(listView);
            updateFavList();

            return rootView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, view, menuInfo);
            menu.add(0, 0, 0, R.string.remove_from_fav);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            if (item.getItemId() == 0) {
                // Get string and note from view
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                View rootView = info.targetView;
                String string = ((TextView) rootView.findViewById(android.R.id.text1)).getText().toString();

                // Add to database
                try {
                    favDataSource.open();
                    favDataSource.removeEntryByString(string);
                    favDataSource.close();
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                    updateFavList();
                } catch (SQLException e) {
                    promptException(e);
                }
            }
            return true;
        }

        /**
         * Get a mocked Category from database
         *
         * @return mocked Category
         */
        private RepoXmlParser.Category mockCategory() {
            RepoXmlParser.Category category = null;
            try {
                favDataSource.open();
                category = new RepoXmlParser.Category("fav", favDataSource.getAllEntries());
                favDataSource.close();
            } catch (SQLException e) {
                promptException(e);
            }
            return category;
        }

        /**
         * Update the favorite list view
         */
        private void updateFavList() {
            RepoXmlParser.Category mockedCategory = mockCategory();
            if (mockedCategory != null) {
                listView.setAdapter(new DoubleItemListAdapter(MainActivity.this, mockedCategory));
                listView.setOnItemClickListener(new OnItemClickCopyToClipBoardListener());
            }
        }
    }

    /**
     * Fragment that holds a list of strings for one category
     * This module uses ActionBar-PullToRefresh from https://github.com/chrisbanes/ActionBar-PullToRefresh
     */
    private class DoubleItemListFragment extends Fragment {

        private static final String CAT_KEY = "category";
        private RepoXmlParser.Category cat;

        public DoubleItemListFragment() {
            // Required constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                cat = (RepoXmlParser.Category) getArguments().getSerializable(CAT_KEY);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate rootView
            View rootView = inflater.inflate(R.layout.pull_to_refresh_layout, container, false);

            // Setup pullToRefreshLayout
            final PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.pullToRefreshLayout);
            ActionBarPullToRefresh.from(MainActivity.this).allChildrenArePullable().listener(new OnRefreshListener() {
                @Override
                public void onRefreshStarted(View view) {
                    refreshingPullToRefreshLayout = pullToRefreshLayout;
                    update();
                }
            }).setup(pullToRefreshLayout);

            // Setup listView
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(new DoubleItemListAdapter(MainActivity.this, cat));
            listView.setOnItemClickListener(new OnItemClickCopyToClipBoardListener());
            registerForContextMenu(listView);

            return rootView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, view, menuInfo);
            menu.add(0, 0, 0, R.string.add_to_fav);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            if (item.getItemId() == 0) {
                // Get string and note from view
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                View rootView = info.targetView;
                String string = ((TextView) rootView.findViewById(android.R.id.text1)).getText().toString();
                String note = ((TextView) rootView.findViewById(android.R.id.text2)).getText().toString();

                // Add to db
                try {
                    favDataSource.open();
                    if (favDataSource.addEntry(favDataSource.createEntry(string, note))) {
                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.already_added_to_fav), Toast.LENGTH_SHORT).show();
                    }
                    favDataSource.close();
                } catch (SQLException e) {
                    promptException(e);
                }
            }
            return true;
        }

    }

    /**
     * Adapter that holds a list of simple_list_item_2 text views
     */
    private class DoubleItemListAdapter implements ListAdapter {

        private LayoutInflater inflater;
        private RepoXmlParser.Category cat;

        public DoubleItemListAdapter(Context context, RepoXmlParser.Category cat) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.cat = cat;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return cat.entries.size();
        }

        @Override
        public Object getItem(int position) {
            return cat.entries.get(position);
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
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView lineOne = (TextView) view.findViewById(android.R.id.text1);
            TextView lineTwo = (TextView) view.findViewById(android.R.id.text2);
            RepoXmlParser.Entry entry = cat.entries.get(position);
            lineOne.setText(entry.string);
            lineTwo.setText(entry.note);
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return cat.entries.isEmpty();
        }
    }

    /**
     * Copy string to clip board when clicked
     */
    private class OnItemClickCopyToClipBoardListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RepoXmlParser.Entry entry = (RepoXmlParser.Entry) parent.getAdapter().getItem(position);
            String copied = entry.string;
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

    /**
     * Switch notification state to according to current user preference
     */
    private void switchNotificationState() {
        if (notificationVisibility.equals("no")) {
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
        }
        else if (notificationVisibility.equals("panel")) {
            notification.flags = Notification.FLAG_NO_CLEAR;
            notification.priority = Notification.PRIORITY_MIN;
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        }
        else if (notificationVisibility.equals("both")) {
            notification.flags = Notification.FLAG_NO_CLEAR;
            notification.priority = Notification.PRIORITY_DEFAULT;
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


}
