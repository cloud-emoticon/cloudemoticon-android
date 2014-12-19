package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryListViewAdapter;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.events.RepositoryEditedEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryFragment extends Fragment implements Constants {
    @InjectView(R.id.repositoryListView)
    ListView mRepositoryListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mRepositoryEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    private RepositoryListViewAdapter mAdapter;

    public RepositoryFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_repository, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mRepositoryListView.setEmptyView(mRepositoryEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_repo_prompt));
        this.mAdapter = new RepositoryListViewAdapter(getActivity());
        mRepositoryListView.setAdapter(mAdapter);
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_create));
        mFab.attachToListView(mRepositoryListView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddRepositoryDialog("");
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void popupAddRepositoryDialog(String passedInUrl) {
        AddRepositoryDialogFragment fragment = AddRepositoryDialogFragment.newInstance(passedInUrl);
        fragment.show(getFragmentManager(), "add_repository");
    }

    /**
     * Listens for repository downloaded from Internet, namely from repository list view adapter
     *
     * @param event repository downloaded from Internet event
     */
    public void onEvent(RepositoryDownloadedEvent event) {
        Toast.makeText(getActivity(), event.getRepository().getAlias()
                + " "
                + getString(R.string.downloaded), Toast.LENGTH_SHORT).show();
        mAdapter.updateRepositories();
    }

    /**
     * Listens for repository download from Internet failed, namely from repository list view adapter
     *
     * @param event repository download failed from Internet event
     */
    public void onEvent(RepositoryDownloadFailedEvent event) {
        Toast.makeText(getActivity(), event.getThrowable().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for repository being edited, namely from repository list view adapter
     *
     * @param event repository being edited event
     */
    public void onEvent(RepositoryBeginEditingEvent event) {
        EditRepositoryDialogFragment fragment
                = EditRepositoryDialogFragment.newInstance(event.getRepository());
        fragment.show(getFragmentManager(), "edit_repository");
    }

    /**
     * Listens for repository edited, namely from repository list view adapter
     *
     * @param event repository edited event
     */
    public void onEvent(RepositoryEditedEvent event) {
        mAdapter.updateRepositories();
    }

    /**
     * Listens for repository added, namely from add repository dialog fragment
     *
     * @param event repository added event
     */
    public void onEvent(RepositoryAddedEvent event) {
        mAdapter.updateRepositories();
    }
}