package org.ktachibana.cloudemoji;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int sdk = android.os.Build.VERSION.SDK_INT;
    
    private static final int PERSISTENT_NOTIFICATION_ID = 0;
    private static final String FRAGMENT_TAG = "fragment";
    
    private SharedPreferences preferences;
    private NotificationManager notificationManager;
    private Notification notification;

    private boolean isInNotification;
    private boolean isCloseAfterCopy;
    private String url;
    private boolean mocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buildNotification();
        setNotificationState();
        if (savedInstanceState == null) {
        	process();
        }
    }
    
    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        isInNotification
        	= preferences.getBoolean(SettingsActivity.PREF_STAY_IN_NOTIFICATION, true);
        isCloseAfterCopy
        	= preferences.getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
        url
        	= preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));
        mocked 
        	= preferences.getBoolean(SettingsActivity.PREF_MOCK_DATA, false);
    }

    private void buildNotification() {
        String title = getString(R.string.app_name);
        String text = getString(R.string.touch_to_launch);
        int icon = R.drawable.ic_launcher;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setContentIntent(pIntent)
                    .setWhen(0)
                    .build();
    }
    
    /**
     * Set spinning progress bar
     * and start loading & displaying emoji from the Internet
     */
    private void process() {
        new ProcessRepoTask().execute(url);
    }
    
    /**
     * Fetch an XML file from the user preference URL and display on a list
     */
    private class ProcessRepoTask extends AsyncTask<String, Void, RepoXmlParser.Emoji> {
    	
        private List<Exception> taskExceptions; // Hold all exceptions during execution

        protected RepoXmlParser.Emoji doInBackground(String... stringUrl) {
            Reader reader = null;
            RepoXmlParser.Emoji repo = null;
            taskExceptions = new ArrayList<Exception>();
            // If user preference is not to debug with mocked data
            if (!mocked)
            {
	            try {
	                URL url = new URL(stringUrl[0]);
	                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setConnectTimeout(15000);
	                conn.setReadTimeout(10000);
	                conn.setRequestMethod("GET");
	                conn.connect();
	                reader = new InputStreamReader(conn.getInputStream());
	                repo = new RepoXmlParser().parse(reader);
	            } catch (IOException e) {
	                taskExceptions.add(e);
	            } catch (XmlPullParserException e) {
	                taskExceptions.add(e);
	            } catch (Exception e) {
	                taskExceptions.add(e);
	            } finally {
	                try {
	                    reader.close();
	                } catch (Exception e) {
	                    taskExceptions.add(e);
	                }
	            }
            }
            else
            {
            	repo = RepoXmlParser.generateMock();
            }
            return repo;
        }

        protected void onPostExecute(RepoXmlParser.Emoji emoji) {
        	// If no exception caught and emoji is loaded
            if ((taskExceptions.isEmpty()) && (emoji != null)) {
            	render(emoji);
            } else {
            	// Get the first exception caught
                Exception firstException = taskExceptions.get(0);
                if (firstException instanceof IOException) {
                    Toast.makeText(MainActivity.this, getString(R.string.bad_conn), Toast.LENGTH_SHORT).show();
                } else if (firstException instanceof XmlPullParserException) {
                	Log.e("CloudEmoji", "XmlPullParserException", firstException);
                    Toast.makeText(MainActivity.this, getString(R.string.wrong_xml), Toast.LENGTH_SHORT).show();
                } else {
                	Log.e("CloudEmoji", "Unexpcted exception", firstException);
                	Toast.makeText(MainActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
                }
                taskExceptions.clear();
            }
        }
    }
    
    /**
     * Display a emoji with a list held within a fragment
     * @param emoji emoji being displayed
     */
    private void render(RepoXmlParser.Emoji emoji) {
    	MyListFragment fragment = MyListFragment.newInstance(emoji);
    	getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment, FRAGMENT_TAG).commit();
    }
    
    /**
     * Show or dismiss notification according to user preference
     */
    private void setNotificationState() {
        if (isInNotification) {
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        }
        else
        {
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
        }
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(SettingsActivity.PREF_STAY_IN_NOTIFICATION)) {
            isInNotification = preferences.getBoolean(key, true);
            setNotificationState();
        }
        else if (key.equals(SettingsActivity.PREF_CLOSE_AFTER_COPY)) {
            isCloseAfterCopy = preferences.getBoolean(key, true);
        }
        else if (key.equals(SettingsActivity.PREF_TEST_MY_REPO)) {
            url = preferences.getString(key, getString(R.string.default_url));
        }
        else if (key.equals(SettingsActivity.PREF_MOCK_DATA)) {
        	mocked = preferences.getBoolean(key, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                // Refresh is the same task as initial process
                process();
                return true;
            }

            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }

            case R.id.action_exit: {
                finish();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
    
}
