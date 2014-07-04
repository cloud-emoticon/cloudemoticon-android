package org.ktachibana.cloudemoji.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linearlistview.LinearListView;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListItem;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListViewAdapter;
import org.ktachibana.cloudemoji.events.CategoryClickedEvent;
import org.ktachibana.cloudemoji.events.LocalRepositoryClickedEvent;
import org.ktachibana.cloudemoji.events.RemoteRepositoryClickedEvent;
import org.ktachibana.cloudemoji.events.RemoteRepositoryParsedEvent;
import org.ktachibana.cloudemoji.events.SecondaryMenuItemClickedEvent;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.utils.IconifiedListItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class LeftDrawerFragment extends Fragment implements Constants {
    @InjectView(R.id.leftDrawerSourceListView)
    LinearListView mSourceListView;
    @InjectView(R.id.leftDrawerCategoryListView)
    LinearListView mCategoryListView;
    @InjectView(R.id.leftDrawerSecondaryMenu)
    LinearListView mSecondaryMenu;
    @InjectView(R.id.leftDrawerCategoryDivider)
    TextView mCategoryDivider;

    // State
    private long mCurrentRepositoryId;
    private Source mCurrentSource;
    private static final String REPOSITORY_ID_TAG = "firstTimeId";
    private static final String SOURCE_TAG = "firstTimeSource";

    public LeftDrawerFragment() {
        // Required public constructor
    }

    public static LeftDrawerFragment newInstance(long id, Source source) {
        LeftDrawerFragment fragment = new LeftDrawerFragment();
        Bundle args = new Bundle();
        args.putLong(REPOSITORY_ID_TAG, id);
        args.putParcelable(SOURCE_TAG, source);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentRepositoryId
                    = getArguments().getLong(REPOSITORY_ID_TAG);
            mCurrentSource
                    = getArguments().getParcelable(SOURCE_TAG);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_left_drawer, container, false);
        ButterKnife.inject(this, rootView);

        // Setup source list view
        final List<LeftDrawerListItem> sourceListItems = getSourceListItems();
        mSourceListView.setAdapter(
                new LeftDrawerListViewAdapter(
                        sourceListItems,
                        getActivity(),
                        IconifiedListItemView.Style.PRIMARY)
        );
        mSourceListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                mCurrentRepositoryId = sourceListItems.get(i).getId();

                /**
                 * If it is a local repository clicked
                 * Clean categories and notify anyone who cares
                 * Namely the anyone is main activity
                 */
                if (mCurrentRepositoryId < 0) {
                    EventBus.getDefault()
                            .post(new LocalRepositoryClickedEvent(mCurrentRepositoryId));
                }

                /**
                 * Else it is a remote repository clicked
                 * Notify anyone who cares
                 * Namely the anyone is main activity
                 * Main activity will send back parsed source
                 */
                else
                {
                    EventBus.getDefault()
                            .post(new RemoteRepositoryClickedEvent(mCurrentRepositoryId));
                }

                internalSwitchRepository();
            }
        });

        internalSwitchRepository();

        // Setup secondary menu
        final List<LeftDrawerListItem> secondaryMenuListItems = getSecondaryMenuListItems();
        mSecondaryMenu.setAdapter(
                new LeftDrawerListViewAdapter(
                        secondaryMenuListItems,
                        getActivity(),
                        IconifiedListItemView.Style.SECONDARY)
        );
        mSecondaryMenu.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                final LeftDrawerListItem item = secondaryMenuListItems.get(i);
                EventBus.getDefault()
                        .post(new SecondaryMenuItemClickedEvent(item.getId()));
            }
        });

        return rootView;
    }

    private Source setupCategoryListView(Source source) {
        if (source != null) {
            mCategoryListView.setAdapter(
                    new LeftDrawerListViewAdapter(
                            getCategoryListItems(source),
                            getActivity(),
                            IconifiedListItemView.Style.PRIMARY)
            );
            mCategoryListView.setOnItemClickListener(
                    new LinearListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                            EventBus.getDefault().post(new CategoryClickedEvent(i));
                        }
                    }
            );
        }
        return source;
    }

    private List<LeftDrawerListItem> getSourceListItems() {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        // Add local favorite and history
        items.add(
                new LeftDrawerListItem(
                        getString(R.string.fav),
                        R.drawable.ic_favorite,
                        LIST_ITEM_FAVORITE_ID)
        );
        items.add(
                new LeftDrawerListItem(
                        getString(R.string.history),
                        R.drawable.ic_history,
                        LIST_ITEM_HISTORY_ID)
        );

        // Add remote repositories
        List<Repository> repositories = Repository.listAll(Repository.class);
        for (Repository repository : repositories) {
            if (repository.isAvailable()) {
                items.add(
                        new LeftDrawerListItem(
                                repository.getAlias(),
                                R.drawable.ic_repository,
                                repository.getId())
                );
            }
        }

        return items;
    }

    private List<LeftDrawerListItem> getCategoryListItems(Source source) {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        for (Category category : source.getCategories()) {
            items.add(
                    new LeftDrawerListItem(
                            category.getName(),
                            R.drawable.ic_category)
            );
        }

        return items;
    }

    private List<LeftDrawerListItem> getSecondaryMenuListItems() {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        // Repository manager
        items.add(new LeftDrawerListItem(
                getString(R.string.repo_manager),
                R.drawable.ic_repository,
                LIST_ITEM_REPOSITORY_MANAGER_ID
        ));

        // Settings
        items.add(new LeftDrawerListItem(
                getString(R.string.settings),
                android.R.drawable.ic_menu_preferences,
                LIST_ITEM_SETTINGS_ID
        ));

        // Exit
        items.add(new LeftDrawerListItem(
                getString(R.string.exit),
                R.drawable.ic_exit,
                LIST_ITEM_EXIT_ID
        ));

        return items;
    }

    public void onEvent(RemoteRepositoryParsedEvent event) {
        setupCategoryListView(event.getSource());
    }

    private void internalSwitchRepository() {
        if (mCurrentRepositoryId < 0)
        {
            mCategoryListView.setAdapter(null);
            mCategoryDivider.setVisibility(View.GONE);
        }
        else
        {
            if (Repository.findById(Repository.class, mCurrentRepositoryId) != null) {
                setupCategoryListView(mCurrentSource);
                mCategoryDivider.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
