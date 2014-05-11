package org.ktachibana.cloudemoji.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDeletedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.viewholders.RepositoryViewHolder;

import de.greenrobot.event.EventBus;
import uk.co.ribot.easyadapter.EasyAdapter;

public class RepositoryListFragment extends Fragment implements Constants {
    private EasyAdapter<Repository> adapter;

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
        ListView repositoryListView = (ListView) rootView.findViewById(R.id.repositoryListView);
        repositoryListView.setEmptyView(rootView.findViewById(R.id.repositoryEmptyView));
        this.adapter = new EasyAdapter<Repository>(getActivity(), RepositoryViewHolder.class, Repository.listAll(Repository.class));
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
            Toast.makeText(getActivity(), event.getRepository().getAlias(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), event.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onEvent(RepositoryDeletedEvent event) {
        adapter.setItems(Repository.listAll(Repository.class));
    }

    public void onEvent(RepositoryAddedEvent event) {
        adapter.addItem(event.getRepository());
    }
}