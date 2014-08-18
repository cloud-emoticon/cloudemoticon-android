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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchResultFragment extends Fragment {
    private static final String SEARCH_QUERY_KEY = "mSearchQuery";
    private String mSearchQuery;

    @InjectView(R.id.searchResultListView)
    ListView mSearchResultListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mSearchResultEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mSearchResultEmptyViewTextView;

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

        return rootView;
    }

}
