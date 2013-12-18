package org.ktachibana.cloudemoji;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int sdk = android.os.Build.VERSION.SDK_INT;
        
    private SharedPreferences preferences;
    private NotificationManager nManager;
    private Notification notification;

    private boolean isInNotification;
    private boolean isCloseAfterCopy;
    private String url;
    private boolean mocked;

    public static final int PERSISTENT_NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initializations();
        setupNotification();
        fireupNotification();

        // Begin to download parse and render repository
        process(url);
    }

    private void initializations() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        isInNotification
        	= preferences.getBoolean(SettingsActivity.PREF_STAY_IN_NOTIFICATION, true);
        isCloseAfterCopy
        	= preferences.getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
        url
        	= preferences.getString(SettingsActivity.PREF_TEST_MY_REPO, getString(R.string.default_url));
        mocked 
        	= preferences.getBoolean(SettingsActivity.PREF_MOCK_DATA, false);
    }

    private void setupNotification() {
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
        // Inflate the menu
    	// Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                // Refresh is the same task as initial process
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

    private void process(String url) {
        new ProcessRepoTask().execute(url);
    }

    /**
     * Render a given repository to the screen
     */
    private void render(RepoXmlParser.Emoji emoji) {
        List<String> infoos = emoji.infoos.infoos;
        List<RepoXmlParser.Category> categories = emoji.categories;
        
        PinnedHeaderListView listView = new PinnedHeaderListView(this);
        renderInfoos(listView, infoos);
        MySectionedBaseAdapter adapter = new MySectionedBaseAdapter(this, categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener(adapter));
        setContentView(listView);
    }
    
    private void renderInfoos(PinnedHeaderListView listView, List<String> infoos) {
    	for (String infoo : infoos) {
    		TextView view = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
    		view.setText(infoo);
    		listView.addHeaderView(view);
    	}
    }

    /**
     * Fetch a URL of XML file, parse it and make it show on a list
     */
    private class ProcessRepoTask extends AsyncTask<String, Void, RepoXmlParser.Emoji> {
        private ProgressDialog pd;
        private List<Exception> taskExceptions; //Hold all exceptions

        protected void onPreExecute() {
            // Show a processing dialog
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.processing));
            pd.show();
        }

        protected RepoXmlParser.Emoji doInBackground(String... stringUrl) {
            Reader reader = null;
            RepoXmlParser.Emoji repo = null;
            taskExceptions = new ArrayList<Exception>();
            if (!mocked)
            {
	            // Try to download and parse
	            try {
	                // Download
	                URL url = new URL(stringUrl[0]);
	                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setConnectTimeout(15000);
	                conn.setReadTimeout(10000);
	                conn.setRequestMethod("GET");
	                conn.connect();
	                reader = new InputStreamReader(conn.getInputStream());
	                // Parse
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
            	repo = MockData.generateMock();
            }
            return repo;
        }

        protected void onPostExecute(RepoXmlParser.Emoji emoji) {
            if ((taskExceptions.isEmpty()) && (emoji != null)) {
                render(emoji);
            } else {
                Exception firstException = taskExceptions.get(0);
                if (firstException instanceof IOException) {
                    Toast.makeText(MainActivity.this, getString(R.string.bad_conn), Toast.LENGTH_SHORT).show();
                } else if (firstException instanceof XmlPullParserException) {
                    Toast.makeText(MainActivity.this, getString(R.string.wrong_xml), Toast.LENGTH_SHORT).show();
                } else {
                	Log.e("CloudEmoji", "Unexpcted exception", firstException);
                	Toast.makeText(MainActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
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
    
    /**
     * Listens on child item click and copy to clip board
     */
    private class MyOnItemClickListener extends PinnedHeaderListView.OnItemClickListener {
    	
    	private MySectionedBaseAdapter adapter;
    	
    	public MyOnItemClickListener(MySectionedBaseAdapter adapter) {
    		this.adapter = adapter;
    	}
    	
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
			String copied = ((RepoXmlParser.Entry) adapter.getItem(section, position)).string;
			
            // Copy to clip board
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
    
            Toast.makeText(MainActivity.this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
            if (isCloseAfterCopy) {
                moveTaskToBack (true);
            }			
		}

		@Override
		public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
			return;
		}
    }
    
}
