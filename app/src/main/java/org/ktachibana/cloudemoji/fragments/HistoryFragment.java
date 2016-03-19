package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.HistoryListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.disk.History;
import org.ktachibana.cloudemoji.models.memory.Entry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryFragment extends BaseFragment {
    @Bind(R.id.historyListView)
    ListView mHistoryListView;

    @Bind(R.id.emptyView)
    RelativeLayout mHistoryEmptyView;

    @Bind(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private HistoryListViewAdapter mAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);

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
                mBus.post(new EntryCopiedAndAddedToHistoryEvent(entry));
                mAdapter.updateHistory();
            }
        });
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_discard));
        mFab.attachToListView(mHistoryListView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                History.deleteAll(History.class);
                mAdapter.updateHistory();
            }
        });
        return rootView;
    }

    @Subscribe
    public void handle(EntryCopiedAndAddedToHistoryEvent e) {
        mAdapter.updateHistory();
    }
}
