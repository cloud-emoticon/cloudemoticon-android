package org.ktachibana.cloudemoji;

import android.preference.PreferenceManager;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;
import org.ktachibana.cloudemoji.RepoXmlParser.Emoji;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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
		ExpandableListView listView = new ExpandableListView(context);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(context, emoji.categories);
		listView.setAdapter(adapter);
        listView.setOnChildClickListener(new MyOnChildClickListener(adapter));
		return listView;
	}

    private class MyOnChildClickListener implements ExpandableListView.OnChildClickListener {

        MyExpandableListAdapter adapter;

        public MyOnChildClickListener(MyExpandableListAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            RepoXmlParser.Entry entry = (RepoXmlParser.Entry) adapter.getChild(groupPosition, childPosition);
            String copied = entry.string;
            // Below 3.0 support
            if (MainActivity.SDK < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copied);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("emoji", copied);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(getActivity().getBaseContext(),
                    getString(R.string.copied), Toast.LENGTH_SHORT).show();
            boolean isCloseAfterCopy = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getBoolean(SettingsActivity.PREF_CLOSE_AFTER_COPY, true);
            if (isCloseAfterCopy) {
                getActivity().finish();
            }
            return true;
        }
    }

}
