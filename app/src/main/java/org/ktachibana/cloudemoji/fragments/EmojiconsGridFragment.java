package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.People;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.EmojiconsGridAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.memory.Entry;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmojiconsGridFragment extends BaseFragment {
    private static final String EMOJICONS_KEY = "emojicons";
    @InjectView(R.id.grid)
    GridView grid;
    private Emojicon[] mEmojicons;

    public EmojiconsGridFragment() {
        // Required empty public constructor
    }

    public static EmojiconsGridFragment newInstance(Emojicon[] emojicons) {
        EmojiconsGridFragment fragment = new EmojiconsGridFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(EMOJICONS_KEY, emojicons);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmojicons = (Emojicon[]) getArguments().getParcelableArray(EMOJICONS_KEY);
        } else {
            mEmojicons = new People().offer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emojicons_grid, container, false);
        ButterKnife.inject(this, rootView);

        grid.setAdapter(new EmojiconsGridAdapter(getActivity(), mEmojicons));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String emojicon = mEmojicons[position].getEmoji();
                BUS.post(new EntryCopiedAndAddedToHistoryEvent(new Entry(emojicon, "")));
            }
        });

        return rootView;
    }
}
