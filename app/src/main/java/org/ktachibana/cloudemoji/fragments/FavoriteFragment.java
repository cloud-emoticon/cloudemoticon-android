package org.ktachibana.cloudemoji.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;

import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteBeginEditingEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.ui.MultiInputMaterialDialogBuilder;
import org.ktachibana.cloudemoji.ui.ScrollableEmoticonMaterialDialogBuilder;
import org.ktachibana.cloudemoji.utils.CapabilityUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteFragment extends BaseFragment {
    @Bind(R.id.favoriteListView)
    DragSortListView mFavoriteListView;

    @Bind(R.id.emptyView)
    RelativeLayout mFavoriteEmptyView;

    @Bind(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    FavoriteListViewAdapter mAdapter;
    private Context mContext;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, rootView);

        // Setup contents
        mFavoriteListView.setEmptyView(mFavoriteEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_favorite_prompt));
        this.mAdapter = new FavoriteListViewAdapter(getActivity());
        mFavoriteListView
                .setAdapter(mAdapter);
        mFavoriteListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Favorite favorite = (Favorite) mAdapter.getItem(position);
            Entry entry = new Entry(favorite.getEmoticon(), favorite.getDescription());
            mBus.post(new EntryAddedToHistoryEvent(entry));
        });
        mFavoriteListView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            Favorite favorite = (Favorite) mAdapter.getItem(position);
            new ScrollableEmoticonMaterialDialogBuilder(mContext)
                .setEmoticon(favorite.getEmoticon())
                .build()
                .show();
            return true;
        });
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_create));
        mFab.attachToListView(mFavoriteListView);
        mFab.setOnClickListener(v -> popupAddFavoriteDialog());
        return rootView;
    }

    private void popupAddFavoriteDialog() {
        new MultiInputMaterialDialogBuilder(mContext)
                .addInput(null, getString(R.string.emoticon), input -> {
                    if (TextUtils.isEmpty(input)) {
                        return getString(R.string.empty_emoticon);
                    } else {
                        List<Favorite> favorites = Favorite.listAll(Favorite.class);
                        for (Favorite favorite : favorites) {
                            if (TextUtils.equals(favorite.getEmoticon(), input)) {
                                return getString(R.string.duplicate_emoticon);
                            }
                        }
                        return null;
                    }
                })
                .addInput(null, getString(R.string.description))
                .addInput(null, getString(R.string.shortcut), CapabilityUtils.disableFavoriteShortcutFeature())
                .inputs((dialog, inputs, allInputsValidated) -> {
                    if (allInputsValidated) {
                        String emoticon = inputs.get(0).toString();
                        String description = inputs.get(1).toString();
                        String shortcut = CapabilityUtils.disableFavoriteShortcutFeature() ? "" : inputs.get(2).toString();

                        Favorite favorite = new Favorite(emoticon, description, shortcut);
                        favorite.save();

                        mAdapter.updateFavorites();
                        showSnackBar(emoticon + "\n" + getString(R.string.added_to_fav));
                    }
                })
                .title(R.string.add_to_fav)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }

    @Subscribe
    public void handle(FavoriteBeginEditingEvent event) {
        final Favorite favorite = event.getFavorite();
        new MultiInputMaterialDialogBuilder(mContext)
                .addInput(favorite.getDescription(), getString(R.string.description))
                .addInput(favorite.getShortcut(), getString(R.string.shortcut), CapabilityUtils.disableFavoriteShortcutFeature())
                .inputs((dialog, inputs, allInputsValidated) -> {
                    String description = inputs.get(0).toString();
                    String shortcut = CapabilityUtils.disableFavoriteShortcutFeature() ? "" : inputs.get(1).toString();

                    // Get the new favorite and SAVE
                    favorite.setDescription(description);
                    favorite.setShortcut(shortcut);
                    favorite.save();

                    mAdapter.updateFavorites();
                })
                .title(R.string.edit_favorite)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }

    @Subscribe
    public void handle(FavoriteAddedEvent e) {
        mAdapter.updateFavorites();
    }

    @Subscribe
    public void handle(FavoriteDeletedEvent e) {
        mAdapter.updateFavorites();
    }
}
