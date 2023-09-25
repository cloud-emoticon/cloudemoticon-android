package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.bundler.ParcelerArgsBundler;
import com.hjy.pinnedheaderlistview.PinnedHeaderListView;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SourceListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.ui.ScrollableEmoticonMaterialDialogBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SourceFragment extends BaseFragment {
    @Bind(R.id.list)
    PinnedHeaderListView mList;
    @Arg(bundler = ParcelerArgsBundler.class)
    Source mSource;
    private SourceListViewAdapter mAdapter;

    public SourceFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_source, container, false);
        ButterKnife.bind(this, rootView);

        // Setup contents
        mAdapter = new SourceListViewAdapter(getActivity(), mSource);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new PinnedHeaderListView.OnPinnedHeaderListViewItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, int section, int positionInSection, long id) {
                Entry entry = mAdapter.getItem(section, positionInSection);
                mBus.post(new EntryAddedToHistoryEvent(entry));
            }

            @Override
            public void onSectionHeaderClick(AdapterView<?> adapterView, int section, long id) { }
        });

        mList.setOnItemLongClickListener(new PinnedHeaderListView.OnPinnedHeaderListViewLongItemClickListener() {
            @Override
            public void onItemLongClick(AdapterView<?> adapterView, int section, int positionInSection, long id) {
                Entry entry = mAdapter.getItem(section, positionInSection);
                new ScrollableEmoticonMaterialDialogBuilder(getActivity())
                        .setEmoticon(entry.getEmoticon())
                        .build()
                        .show();
            }

            @Override
            public void onSectionHeaderLongClick(AdapterView<?> adapterView, int section, long id) {}
        });

        return rootView;
    }
}
