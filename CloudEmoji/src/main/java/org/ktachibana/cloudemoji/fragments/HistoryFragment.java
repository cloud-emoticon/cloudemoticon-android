package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.ktachibana.cloudemoji.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HistoryFragment extends Fragment {
    @InjectView(R.id.historyListView)
    ListView mHistoryListView;

    @InjectView(R.id.historyEmptyView)
    RelativeLayout mHistoryEmptyView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, rootView);

        mHistoryListView.setEmptyView(mHistoryEmptyView);
        return rootView;
    }


}
