package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryListViewAdapter;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDeletedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryListFragment extends Fragment implements Constants {
    @InjectView(R.id.repositoryListView)
    ListView repositoryListView;

    @InjectView(R.id.repositoryEmptyView)
    RelativeLayout repositoryEmptyView;

    private RepositoryListViewAdapter adapter;

    public RepositoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_repository_list, container, false);
        ButterKnife.inject(this, rootView);

        repositoryListView.setEmptyView(repositoryEmptyView);
        this.adapter = new RepositoryListViewAdapter(Repository.listAll(Repository.class), getActivity());
        repositoryListView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_repository_manager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_repository: {
                AddRepositoryDialogFragment fragment = new AddRepositoryDialogFragment();
                fragment.show(getFragmentManager(), "add_repository");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(RepositoryDownloadedEvent event) {
        if (event.getException() == null) {
            Toast.makeText(getActivity(), event.getRepository().getAlias()
                    + " "
                    + getString(R.string.downloaded), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), event.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        adapter.updateRepositories(Repository.listAll(Repository.class));
    }

    public void onEvent(RepositoryDeletedEvent event) {
        adapter.updateRepositories(Repository.listAll(Repository.class));
    }

    public void onEvent(RepositoryAddedEvent event) {
        adapter.updateRepositories(Repository.listAll(Repository.class));
    }
}