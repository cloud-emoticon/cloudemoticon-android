package org.ktachibana.cloudemoji;

import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class MyListFragment extends Fragment {

	private static final String EMOJI_FLAG = "emoji";

	private RepoXmlParser.Emoji emoji;

	public static MyListFragment newInstance(RepoXmlParser.Emoji emoji) {
		MyListFragment fragment = new MyListFragment();
		Bundle args = new Bundle();
		args.putSerializable(EMOJI_FLAG, emoji);
		fragment.setArguments(args);
		return fragment;
	}

	public MyListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			emoji = (Emoji) getArguments().getSerializable(EMOJI_FLAG);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Context context = getActivity().getBaseContext();
		PinnedHeaderListView listView = new PinnedHeaderListView(context);
		MySectionedBaseAdapter adapter = new MySectionedBaseAdapter(context,
				emoji.categories);
		for (String s : emoji.infoos.infoos) {
			TextView textView = new TextView(context);
			textView.setText(s);
			textView.setBackgroundColor(context.getResources().getColor(
					R.color.holo_gray_light));
			listView.addFooterView(textView);
		}
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener(adapter));
		return listView;
	}

	/**
	 * Listen to item click and copy string to clip board
	 */
	private class MyOnItemClickListener extends
			PinnedHeaderListView.OnItemClickListener {

		private MySectionedBaseAdapter adapter;

		public MyOnItemClickListener(MySectionedBaseAdapter adapter) {
			this.adapter = adapter;
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int section, int position, long id) {
			String copied = ((RepoXmlParser.Entry) adapter.getItem(section,
					position)).string;
			// Below 3.0 support
			if (MainActivity.sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(copied);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("emoji", copied);
				clipboard.setPrimaryClip(clip);
			}

			Toast.makeText(getActivity().getBaseContext(),
					getString(R.string.copied), Toast.LENGTH_SHORT).show();
			boolean isCloseAfterCopy = PreferenceManager
					.getDefaultSharedPreferences(getActivity().getBaseContext())
					.getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
			if (isCloseAfterCopy) {
				getActivity().finish();
			}
		}

		@Override
		public void onSectionClick(AdapterView<?> adapterView, View view,
				int section, long id) {

		}
	}

}
