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

public class FavoriteFragment extends Fragment {
    @InjectView(R.id.favoriteListView)
    ListView favoriteListView;

    @InjectView(R.id.favoriteEmptyView)
    RelativeLayout favoriteEmptyView;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.inject(this, rootView);

        favoriteListView.setEmptyView(favoriteEmptyView);
        return rootView;
    }


}
