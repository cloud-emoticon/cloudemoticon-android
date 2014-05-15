package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.linearlistview.LinearListView;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListItem;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListViewAdapter;
import org.ktachibana.cloudemoji.models.Repository;

import java.util.ArrayList;
import java.util.List;

public class LeftDrawerFragment extends Fragment implements Constants {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View rootView = inflater.inflate(R.layout.fragment_left_drawer, container, false);

        // Find views
        LinearListView sourceListView = (LinearListView) rootView.findViewById(R.id.leftDrawerSourceListView);
        LinearListView categoryListView = (LinearListView) rootView.findViewById(R.id.leftDrawerCategoryListView);

        // Setup source list view
        final List<LeftDrawerListItem> sourceListItems = getSourceListItems();
        sourceListView.setAdapter(new LeftDrawerListViewAdapter(sourceListItems, getActivity()));
        sourceListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                LeftDrawerListItem item = sourceListItems.get(i);
                long id = item.getId();
                Toast.makeText(getActivity(), String.valueOf(id), Toast.LENGTH_SHORT).show();
            }
        });

        // Setup category list view
        return rootView;
    }

    private List<LeftDrawerListItem> getSourceListItems() {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        // Add local favorite and history
        items.add(new LeftDrawerListItem(getString(R.string.my_fav), R.drawable.ic_favorite, LIST_ITEM_FAVORITE_ID));
        items.add(new LeftDrawerListItem(getString(R.string.history), R.drawable.ic_history, LIST_ITEM_HISTORY_ID));

        // Add remote repositories
        List<Repository> repositories = Repository.listAll(Repository.class);
        for (Repository repository : repositories) {
            if (repository.isAvailable()) {
                items.add(new LeftDrawerListItem(repository.getAlias(), R.drawable.ic_repository, repository.getId()));
            }
        }

        return items;
    }
}
