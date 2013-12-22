package org.ktachibana.cloudemoji;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

public class MainActivity extends FragmentActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	public static final int sdk = android.os.Build.VERSION.SDK_INT;

	private static final int PERSISTENT_NOTIFICATION_ID = 0;
	private static final String FRAGMENT_TAG = "fragment";
	private static final String XML_FILE_NAME = "emoji.xml";

	private SharedPreferences preferences;
	private NotificationManager notificationManager;
	private Notification notification;

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
		// If not coming from existing session
		if (savedInstanceState == null) {
			// Read saved XML from local storage
			File file = new File(getFilesDir(), XML_FILE_NAME);
			// If file does not exist
			if (!file.exists()) {
				update();
			}
			// Else render the existing
			else
			{
				Emoji emoji = readEmoji(file);
				render(emoji);
			}
		}
	}

	private void init() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		isInNotification = preferences.getBoolean(
				SettingsActivity.PREF_STAY_IN_NOTIFICATION, true);
		url = preferences.getString(SettingsActivity.PREF_TEST_MY_REPO,
				getString(R.string.default_url));
		mocked = preferences.getBoolean(SettingsActivity.PREF_MOCK_DATA, false);
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
	 * Set spinning progress bar and start loading & displaying emoji from the
	 * Internet
	 */
	private void update() {
		new UpdateRepoTask().execute(url);
	}

	/**
	 * Fetch an XML file from the user preference URL and display on a list
	 */
	private class UpdateRepoTask extends AsyncTask<String, Void, Emoji> {
		
		private List<Exception> taskExceptions;
		
		protected void onPreExecute() {
			taskExceptions = new ArrayList<Exception>();
		}
		
		@Override
		protected Emoji doInBackground(String... stringUrl) {
			Emoji emoji = null;
			HttpURLConnection conn = null;
			Reader reader = null;
			OutputStream fileOut = null;
			if (!mocked)
			{
				try
				{
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
			}
			else
			{
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
	 * @param file
	 * @return emoji
	 */
	private Emoji readEmoji(File file) {
		FileInputStream fileIn = null;
		Reader reader = null;
		Emoji emoji = null;
		try 
		{
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
	 * @param emoji
	 */
	private void render(Emoji emoji) {
		if (emoji != null)
		{
			MyListFragment fragment = MyListFragment.newInstance(emoji);
			getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment, FRAGMENT_TAG).commit();
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
		} else if (e instanceof FileNotFoundException){
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
