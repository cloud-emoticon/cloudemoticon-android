package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SearchResultListViewAdapter;
import org.ktachibana.cloudemoji.events.SearchFinishedEvent;
import org.ktachibana.cloudemoji.events.SearchInitiatedEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class SearchResultFragment extends Fragment {
    private static final String SEARCH_QUERY_KEY = "mSearchQuery";
    @InjectView(R.id.searchResultListView)
    ListView mSearchResultListView;
    @InjectView(R.id.emptyView)
    RelativeLayout mSearchResultEmptyView;
    @InjectView(R.id.emptyViewTextView)
    TextView mSearchResultEmptyViewTextView;
    private String mSearchQuery;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String searchQuery) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY_KEY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSearchQuery = getArguments().getString(SEARCH_QUERY_KEY);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mSearchResultListView.setEmptyView(mSearchResultEmptyView);
        mSearchResultEmptyViewTextView.setText(R.string.search_result_not_found);

        // Initiate search
        EventBus.getDefault().post(new SearchInitiatedEvent(mSearchQuery));

        return rootView;
    }

    public void onEvent(SearchFinishedEvent e) {
        mSearchResultListView.setAdapter(new SearchResultListViewAdapter(getActivity(), e.getResults()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
