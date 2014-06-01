package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.events.EmoticonCopiedEvent;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.models.Favorite;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class FavoriteFragment extends Fragment {
    @InjectView(R.id.favoriteListView)
    ListView mFavoriteListView;

    @InjectView(R.id.favoriteEmptyView)
    RelativeLayout mFavoriteEmptyView;

    FavoriteListViewAdapter mAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mFavoriteListView.setEmptyView(mFavoriteEmptyView);
        List<Favorite> favorites = Favorite.listAll(Favorite.class);
        final List<String> strings = new ArrayList<String>();
        for (Favorite favorite : favorites) {
            strings.add(favorite.getEmoticon() + " " + favorite.getDescription());
        }

        this.mAdapter = new FavoriteListViewAdapter(getActivity());
        mFavoriteListView
                .setAdapter(mAdapter);
        mFavoriteListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        EventBus.getDefault().post(new EmoticonCopiedEvent(strings.get(i)));
                    }
                });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favorite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_favorite: {
                AddFavoriteDialogFragment fragment = new AddFavoriteDialogFragment();
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), "add_favorite");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyFavoriteAdded() {
        mAdapter.updateFavorites();
    }
}
