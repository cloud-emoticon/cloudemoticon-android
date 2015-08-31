package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.People;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.EmojiconsGridAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.memory.Entry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmojiconsGridFragment extends BaseFragment {
    private static final String EMOJICONS_KEY = "emojicons";
    @Bind(R.id.grid)
    GridView grid;
    @Arg
    Emojicon[] mEmojicons;

    public EmojiconsGridFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_emojicons_grid, container, false);
        ButterKnife.bind(this, rootView);

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
