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
    ListView mFavoriteListView;

    @InjectView(R.id.favoriteEmptyView)
    RelativeLayout mFavoriteEmptyView;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.inject(this, rootView);

        mFavoriteListView.setEmptyView(mFavoriteEmptyView);
        return rootView;
    }


}
