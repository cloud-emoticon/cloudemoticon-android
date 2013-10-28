package org.ktachibana.cloudemoji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    int sdk;

    private ExpandableListView expandable;
    private ExpandableListViewAdapter adapter;
    private SharedPreferences preferences;
    private Notification notification;
    private NotificationManager nManager;

    private boolean isInNotification;
    private boolean isCloseAfterCopy;
    private String url;

    public static final int PERSISTENT_NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializations();
        setupPreferences();
        setupNotification();
        fireupNotification();

        //Begin to download and parse repository
        process(url);
    }

    private void initializations() {
        sdk = android.os.Build.VERSION.SDK_INT;
        expandable = (ExpandableListView) findViewById(R.id.expandableListView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    private void setupPreferences() {
        isInNotification
                = preferences.getBoolean(SettingsActivity.PREF_STAY_IN_NOTIFICATION, true);
        isCloseAfterCopy
                = preferences.getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
        url
                = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));
    }

    private void setupNotification() {
        String title = getString(R.string.app_name);
        String text = getString(R.string.touch_to_launch);
        int icon = R.drawable.ic_notification;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        
        //ways to remove time stamp using NotificationCompat?
        notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setContentIntent(pIntent)
                    .build();
    }

    private void fireupNotification() {
        if (isInNotification) {
            notification.flags = Notification.FLAG_NO_CLEAR;
            nManager.notify(PERSISTENT_NOTIFICATION_ID, notification);
        }
        else
        {
            nManager.cancel(PERSISTENT_NOTIFICATION_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                //Refresh: same task
                process(url);
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

    /**
     * Make it easier to show a short toast
     */
    private void fireupToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(MainActivity.this, message, duration);
        toast.show();
    }

    private void process(String url) {
        new ProcessRepoTask().execute(url);
    }

    /**
     * Render a given repository to the screen
     */
    private void render(RepoXmlParser.Emoji emoji) {
        //TODO: Display repository information
        List<String> infoos = emoji.infoos.infoos;
        List<RepoXmlParser.Category> categories = emoji.categories;

        adapter = new ExpandableListViewAdapter(this, categories);
        expandable.setAdapter(adapter);
        expandable.setOnChildClickListener(new ChildClickListener());
    }

    /**
     * Fetch a URL of xml file, parse it and make it show on a list
     */
    private class ProcessRepoTask extends AsyncTask<String, Void, RepoXmlParser.Emoji> {
        private ProgressDialog pd;
        private List<Exception> taskExceptions; //Hold all exceptions during runtime

        protected void onPreExecute() {
            //Show a processing dialog
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.processing));
            pd.show();
        }

        protected RepoXmlParser.Emoji doInBackground(String... stringUrl) {
            Reader reader = null;
            RepoXmlParser.Emoji repo = null;
            taskExceptions = new ArrayList<Exception>();
            //Try to download and parse
            try {
                //Downloading
                URL url = new URL(stringUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.connect();

                //Parsing
                reader = new InputStreamReader(conn.getInputStream());
                repo = new RepoXmlParser().parse(reader);

                //Bad connection
            } catch (IOException e) {
                taskExceptions.add(e);
                //Wrong XML
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
            return repo;
        }

        protected void onPostExecute(RepoXmlParser.Emoji emoji) {
            if ((taskExceptions.isEmpty()) && (emoji != null)) {
                render(emoji);
            } else {
                Exception firstException = taskExceptions.get(0);
                //If bad connection
                if (firstException instanceof IOException) {
                    fireupToast(getString(R.string.bad_conn));
                    //If wrong XML
                } else if (firstException instanceof XmlPullParserException) {
                    fireupToast(getString(R.string.wrong_xml));
                    //Please contact me
                } else {
                    fireupToast(getString(R.string.fail));
                }
                taskExceptions.clear();
            }
            pd.dismiss();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(SettingsActivity.PREF_STAY_IN_NOTIFICATION)) {
            isInNotification = preferences.getBoolean(key, true);
            fireupNotification();
        }
        else if (key.equals(SettingsActivity.PREF_CLOSE_AFTER_COPY)) {
            isCloseAfterCopy = preferences.getBoolean(key, true);
        }
        else if (key.equals(SettingsActivity.PREF_TEST_MY_REPO)) {
            url = preferences.getString(key, getString(R.string.default_url));
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

    /**
     * Listens on child item click and copy to clipboard
     */
    private class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @SuppressLint("NewApi")
		@Override
        public boolean onChildClick(ExpandableListView parent, View view,
                                    int groupPosition, int childPosition, long id) {
            RepoXmlParser.Entry entry = (RepoXmlParser.Entry) adapter.getChild(groupPosition, childPosition);
            //TODO: not refer to entry but adapter
            String copied = entry.string;

            //Copy to clip board
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard
                        = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copied);
            } else {
                android.content.ClipboardManager clipboard
                        = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
                clipboard.setPrimaryClip(clip);
            }

            fireupToast(getString(R.string.copied));
            if (isCloseAfterCopy) {
                moveTaskToBack (true);
            }
            return false;
        }
    }
}
