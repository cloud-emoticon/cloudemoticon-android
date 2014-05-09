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

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Repository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryListFragment extends Fragment {
    FunDapter<Repository> adapter;

    public RepositoryListFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_repository_list, container, false);
        ListView repositoryListView = (ListView) rootView.findViewById(R.id.repositoryListView);
        repositoryListView.setEmptyView(rootView.findViewById(R.id.repositoryEmptyView));
        this.adapter = generateAdapter(Repository.listAll(Repository.class));
        repositoryListView.setAdapter(adapter);
        return rootView;
    }

    private FunDapter<Repository> generateAdapter(final List<Repository> repositories) {
        BindDictionary<Repository> dictionary = new BindDictionary<Repository>();
        dictionary.addStringField(R.id.repositoryAliasTextView,
                new StringExtractor<Repository>() {
                    @Override
                    public String getStringValue(Repository repository, int i) {
                        return repository.getAlias();
                    }
                }
        );
        dictionary.addStringField(R.id.repositoryUrlTextView,
                new StringExtractor<Repository>() {
                    @Override
                    public String getStringValue(Repository repository, int i) {
                        return repository.getRemoteAddress();
                    }
                }
        );
        ArrayList<Repository> whyArrayListAsInterface = new ArrayList<Repository>();
        for (Repository repository : repositories) {
            whyArrayListAsInterface.add(repository);
        }
        return new FunDapter<Repository>(getActivity(), whyArrayListAsInterface, R.layout.list_item_repository, dictionary);
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
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), "add_repository");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Repository> repositories = Repository.listAll(Repository.class);
        ArrayList<Repository> whyArrayListAsInterface = new ArrayList<Repository>();
        for (Repository repository : repositories) {
            whyArrayListAsInterface.add(repository);
        }
        adapter.updateData(whyArrayListAsInterface);
    }
}