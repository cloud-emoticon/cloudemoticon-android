package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.FavoriteBeginEditingEvent;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.utils.MultiInputMaterialDialogBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

public class FavoriteFragment extends BaseFragment {
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
                        BUS.post(new EntryCopiedAndAddedToHistoryEvent(entry));
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
        new MultiInputMaterialDialogBuilder(getActivity())
                .addInput(null, getString(R.string.emoticon), new MultiInputMaterialDialogBuilder.InputValidator() {
                    @Override
                    public CharSequence validate(CharSequence input) {
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
                    }
                })
                .addInput(null, getString(R.string.description))
                .addInput(null, getString(R.string.shortcut))
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        if (allInputsValidated) {
                            String emoticon = inputs.get(0).toString();
                            String description = inputs.get(1).toString();
                            String shortcut = inputs.get(2).toString();

                            Favorite favorite = new Favorite(emoticon, description, shortcut);
                            favorite.save();

                            notifyFavoriteAdded();
                            showSnackBar(emoticon + "\n" + getString(R.string.added_to_fav));
                        }
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
        new MultiInputMaterialDialogBuilder(getActivity())
                .addInput(favorite.getDescription(), getString(R.string.description))
                .addInput(favorite.getShortcut(), getString(R.string.shortcut))
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        String description = inputs.get(0).toString();
                        String shortcut = inputs.get(1).toString();

                        // Get the new favorite and SAVE
                        favorite.setDescription(description);
                        favorite.setShortcut(shortcut);
                        favorite.save();

                        mAdapter.updateFavorites();
                    }
                })
                .title(R.string.edit_favorite)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }
}
