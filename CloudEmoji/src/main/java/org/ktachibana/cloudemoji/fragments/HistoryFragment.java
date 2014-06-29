package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.HistoryListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.History;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class HistoryFragment extends Fragment {
    @InjectView(R.id.historyListView)
    ListView mHistoryListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mHistoryEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    private HistoryListViewAdapter mAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mHistoryListView.setEmptyView(mHistoryEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_history_prompt));
        mAdapter = new HistoryListViewAdapter(getActivity());
        mHistoryListView.setAdapter(mAdapter);
        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                History history = (History) mAdapter.getItem(position);
                Entry entry = new Entry(history.getEmoticon(), history.getDescription());
                EventBus.getDefault().post(new EntryCopiedAndAddedToHistoryEvent(entry));
                mAdapter.updateHistory();
            }
        });
        return rootView;
    }


}
