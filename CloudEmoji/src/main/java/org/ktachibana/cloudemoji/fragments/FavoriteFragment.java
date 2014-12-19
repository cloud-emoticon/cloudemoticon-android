package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.FavoriteBeginEditingEvent;
import org.ktachibana.cloudemoji.events.FavoriteEditedEvent;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Favorite;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class FavoriteFragment extends Fragment {
    @InjectView(R.id.favoriteListView)
    DragSortListView mFavoriteListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mFavoriteEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    FavoriteListViewAdapter mAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mFavoriteListView.setEmptyView(mFavoriteEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_favorite_prompt));
        this.mAdapter = new FavoriteListViewAdapter(getActivity());
        mFavoriteListView
                .setAdapter(mAdapter);
        mFavoriteListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Favorite favorite = (Favorite) mAdapter.getItem(i);
                        Entry entry = new Entry(favorite.getEmoticon(), favorite.getDescription());
                        EventBus.getDefault().post(new EntryCopiedAndAddedToHistoryEvent(entry));
                    }
                });
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_create));
        mFab.attachToListView(mFavoriteListView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddFavoriteDialog();
            }
        });
        return rootView;
    }

    public void notifyFavoriteAdded() {
        mAdapter.updateFavorites();
    }

    private void popupAddFavoriteDialog() {
        AddFavoriteDialogFragment fragment = new AddFavoriteDialogFragment();
        fragment.setTargetFragment(FavoriteFragment.this, 0);
        fragment.show(getFragmentManager(), "add_favorite");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Listens for favorite being edited, namely from favorite list view adapter
     *
     * @param event favorite being edited event
     */
    public void onEvent(FavoriteBeginEditingEvent event) {
        EditFavoriteDialogFragment fragment
                = EditFavoriteDialogFragment.newInstance(event.getFavorite());
        fragment.show(getFragmentManager(), "edit_favorite");
    }

    /**
     * Listens for repository edited, namely from repository list view adapter
     *
     * @param event repository edited event
     */
    public void onEvent(FavoriteEditedEvent event) {
        mAdapter.updateFavorites();
    }
}
