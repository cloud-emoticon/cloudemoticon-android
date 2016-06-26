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
import com.hannesdorfmann.fragmentargs.bundler.ParcelerArgsBundler;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SearchResultListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.memory.Entry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultFragment extends BaseFragment {
    @Bind(R.id.searchResultListView)
    ListView mSearchResultListView;
    @Bind(R.id.emptyView)
    RelativeLayout mSearchResultEmptyView;
    @Bind(R.id.emptyViewTextView)
    TextView mSearchResultEmptyViewTextView;
    @Arg
    String mSearchQuery;
    @Arg(bundler = ParcelerArgsBundler.class)
    List<Map.Entry<Entry, HashSet<String>>> mSearchResult;

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

        mSearchResultListView.setAdapter(new SearchResultListViewAdapter(getActivity(), mSearchResult));
        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getAdapter().getItem(position);
                mBus.post(new EntryCopiedAndAddedToHistoryEvent(entry));
            }
        });

        return rootView;
    }
}
