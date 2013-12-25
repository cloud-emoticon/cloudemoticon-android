package org.ktachibana.cloudemoji;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String FRAG_TAG = "fragment";
    private static final String XML_FILE_NAME = "emoji.xml";

    private SharedPreferences preferences;
    private NotificationManager notificationManager;
    private Notification notification;
    private ViewPager viewPager;
    private ActionBar actionBar;

    private boolean isInNotification;
    private String url;
    private boolean mocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buildNotification();
        setNotificationState();
        // Read saved XML from local storage
        File file = new File(getFilesDir(), XML_FILE_NAME);
        // If file does not exist
        if (!file.exists()) {
            update();
        }
        // Else render the existing
        else {
            Emoji emoji = readEmoji(file);
            render(emoji);
        }
    }

    private void init() {
        // Set up preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        isInNotification = preferences.getBoolean(
                SettingsActivity.PREF_STAY_IN_NOTIFICATION, true);
        url = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO,
                getString(R.string.default_url));
        mocked = preferences.getBoolean(SettingsActivity.PREF_MOCK_DATA, false);

        // Set up ActionBar and viewPager
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
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

    /**
     * Download file from the user preference URL and display
     */
    private void update() {
        new UpdateRepoTask().execute(url);
    }

    /**
     * AsyncTask that fetches an XML file from the user preference URL and displays
     */
    private class UpdateRepoTask extends AsyncTask<String, Void, Emoji> {

        private List<Exception> taskExceptions;

        protected void onPreExecute() {
            taskExceptions = new ArrayList<Exception>();
            Toast.makeText(MainActivity.this, getString(R.string.updating), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Emoji doInBackground(String... stringUrl) {
            Emoji emoji = null;
            HttpURLConnection conn = null;
            Reader reader = null;
            OutputStream fileOut = null;
            if (!mocked) {
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

                    // Read emoji from existing file
                    emoji = readEmoji(new File(getFilesDir(), XML_FILE_NAME));
                } catch (IOException e) {
                    taskExceptions.add(e);
                } catch (Exception e) {
                    taskExceptions.add(e);
                } finally {
                    IOUtils.close(conn);
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(fileOut);
                }
            } else {
                emoji = RepoXmlParser.generateMock();
            }
            return emoji;
        }

        @Override
        protected void onPostExecute(Emoji emoji) {
            if (taskExceptions.isEmpty()) {
                render(emoji);
                Toast.makeText(MainActivity.this, getString(R.string.updated), Toast.LENGTH_SHORT).show();
            } else {
                promptException(taskExceptions.get(0));
            }
        }
    }

    /**
     * Read emoji from a file
     *
     * @param file File object
     * @return emoji
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
     * Display a emoji with a list held within a fragment
     *
     * @param emoji Emoji object
     */
    private void render(Emoji emoji) {
        if (emoji != null)
        {
            // Set adapter for pages
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), emoji);
            viewPager.setAdapter(adapter);

            // Set when page is changed, actionBar is also changed
            viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });

            // Add all tabs to actionBar
            for (int i = 0; i < adapter.getCount(); ++i) {
                actionBar.addTab(actionBar.newTab()
                        .setText(adapter.getPageTitle(i))
                        .setTabListener(new ActionBar.TabListener() {
                            @Override
                            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                                viewPager.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                            }

                            @Override
                            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                            }
                        }));
            }

            // Set viewPager to display the first panel
            viewPager.setCurrentItem(0);
            actionBar.setSelectedNavigationItem(0);
        }
    }

    /**
     * Show or dismiss notification according to user preference
     */
    private void setNotificationState() {
        if (isInNotification) {
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        } else {
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
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
        if (key.equals(SettingsActivity.PREF_STAY_IN_NOTIFICATION)) {
            isInNotification = preferences.getBoolean(key, true);
            setNotificationState();
        } else if (key.equals(SettingsActivity.PREF_TEST_MY_REPO)) {
            url = preferences.getString(key, getString(R.string.default_url));
        } else if (key.equals(SettingsActivity.PREF_MOCK_DATA)) {
            mocked = preferences.getBoolean(key, false);
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
     * Adapter that holds pages on the pager view
     */
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Emoji emoji;
        private FragmentManager fm;

        public SectionsPagerAdapter(FragmentManager fm, Emoji emoji) {
            super(fm);
            this.fm = fm;
            this.emoji = emoji;
        }

        @Override
        public Fragment getItem(int position) {
            DoubleItemListFragment fragment = new DoubleItemListFragment();
            Bundle args = new Bundle();
            args.putSerializable(DoubleItemListFragment.CAT_KEY, emoji.categories.get(position));
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public int getCount() {
            return emoji.categories.size();
        }

        @Override
        public String getPageTitle(int position) {
            return emoji.categories.get(position).name;
        }
    }

    /**
     * Fragment that holds a list for one category
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
            ListView listView = new ListView(MainActivity.this);
            listView.setAdapter(new DoubleItemListAdapter(MainActivity.this, cat));
            return listView;
        }
    }

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
}
