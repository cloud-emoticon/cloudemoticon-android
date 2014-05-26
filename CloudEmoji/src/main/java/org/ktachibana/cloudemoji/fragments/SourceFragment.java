package org.ktachibana.cloudemoji.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SourceListViewAdapter;
import org.ktachibana.cloudemoji.models.Source;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SourceFragment extends ListFragment {
    private static final String ARG_SOURCE = "param1";
    private Source source;

    public SourceFragment() {
        // Required empty public constructor
    }

    public static SourceFragment newInstance(Source source) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SOURCE, source);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = getArguments().getParcelable(ARG_SOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup contents
        setListAdapter(new SourceListViewAdapter(getActivity(), source));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup style
        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        // Setup information footer
        for (String information : source.getInformation()) {
            TextView textView = new TextView(getActivity());
            textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
            textView.setText(information);
            getListView().addFooterView(textView);
        }
    }
}
