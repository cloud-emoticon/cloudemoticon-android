package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SearchResultListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.SearchFinishedEvent;
import org.ktachibana.cloudemoji.events.SearchInitiatedEvent;
import org.ktachibana.cloudemoji.models.memory.Entry;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.Subscribe;

public class SearchResultFragment extends BaseFragment {
    @Bind(R.id.searchResultListView)
    ListView mSearchResultListView;
    @Bind(R.id.emptyView)
    RelativeLayout mSearchResultEmptyView;
    @Bind(R.id.emptyViewTextView)
    TextView mSearchResultEmptyViewTextView;
    @Arg
    String mSearchQuery;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, rootView);

        // Setup contents
        mSearchResultListView.setEmptyView(mSearchResultEmptyView);
        mSearchResultEmptyViewTextView.setText(mSearchQuery
                + "\n"
                + this.getResources().getString(R.string.search_result_not_found));

        // Initiate search
        BUS.post(new SearchInitiatedEvent(mSearchQuery));

        return rootView;
    }

    @Subscribe
    public void handle(SearchFinishedEvent e) {
        mSearchResultListView.setAdapter(new SearchResultListViewAdapter(getActivity(), e.getResults()));
        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getAdapter().getItem(position);
                BUS.post(new EntryCopiedAndAddedToHistoryEvent(entry));
            }
        });
    }
}
