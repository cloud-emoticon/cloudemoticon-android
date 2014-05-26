package org.ktachibana.cloudemoji.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class SourceFragment extends Fragment {
    private static final String ARG_SOURCE = "param1";
    @InjectView(R.id.sourceListView)
    ListView mSourceListView;

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
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_source, container, false);
        ButterKnife.inject(this, rootView);

        // Set up contents
        mSourceListView.setAdapter(new SourceListViewAdapter(getActivity(), source));

        // Setup information footer
        for (String information : source.getInformation()) {
            TextView textView = new TextView(getActivity());
            textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
            textView.setText(information);
            mSourceListView.addFooterView(textView);
        }

        return rootView;
    }


}
