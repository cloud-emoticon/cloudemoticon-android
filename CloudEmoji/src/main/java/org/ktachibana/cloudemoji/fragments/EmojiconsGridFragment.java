package org.ktachibana.cloudemoji.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.EmojiconsGridAdapter;
import org.ktachibana.cloudemoji.utils.emojicon.Emojicon;
import org.ktachibana.cloudemoji.utils.emojicon.People;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EmojiconsGridFragment extends Fragment {
    private Emojicon[] mEmojicons;
    private static final String EMOJICONS_KEY = "emojicons";

    @InjectView(R.id.grid)
    GridView grid;

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
        }
        else
        {
            mEmojicons = new People().offer();
        }
    }

    public EmojiconsGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emojicons_grid, container, false);
        ButterKnife.inject(this, rootView);

        grid.setAdapter(new EmojiconsGridAdapter(getActivity(), mEmojicons));

        return rootView;
    }


}
