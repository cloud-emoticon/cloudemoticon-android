package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.ktachibana.cloudemoji.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HistoryFragment extends Fragment {
    @InjectView(R.id.historyListView)
    ListView historyListView;

    @InjectView(R.id.historyEmptyView)
    RelativeLayout historyEmptyView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, rootView);

        historyListView.setEmptyView(historyEmptyView);
        return rootView;
    }


}
