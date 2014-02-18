package org.ktachibana.cloudemoji.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.CategoryListAdapter;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;
import org.ktachibana.cloudemoji.interfaces.OnCopyToClipBoardListener;
import org.ktachibana.cloudemoji.interfaces.OnFavoritesDatabaseOperationsListener;

/**
 * Fragment that holds a list of string in my favorites database
 */
public class FavoritesFragment extends Fragment {

    private ListView listView;
    private OnCopyToClipBoardListener copyToClipBoardCallback;
    private OnFavoritesDatabaseOperationsListener favoritesDatabaseOperationsCallback;

    public FavoritesFragment() {
        // Required constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            copyToClipBoardCallback = (OnCopyToClipBoardListener) activity;
            favoritesDatabaseOperationsCallback = (OnFavoritesDatabaseOperationsListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate rootView
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Find listView and update
        listView = (ListView) rootView.findViewById(R.id.favListView);
        listView.setEmptyView(rootView.findViewById(R.id.emptyView));
        registerForContextMenu(listView);
        updateFavoritesList();

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, 0, 0, R.string.edit_entry);
        menu.add(0, 1, 1, R.string.remove_from_fav);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get string and note from view
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View rootView = info.targetView;
        RepoXmlParser.Entry entry = CategoryListAdapter.getEntryFromView(rootView);
        String string = entry.string;

        // Edit entry
        if (item.getItemId() == 0) {
            EditEntryDialogFragment.createInstance(entry).show(getChildFragmentManager(), "edit");
        }

        // Remove entry
        else if (item.getItemId() == 1) {
            favoritesDatabaseOperationsCallback.onRemoveEntryByString(string);
            updateFavoritesList();
        }
        return true;
    }

    /**
     * Update the favorite list view
     */
    public void updateFavoritesList() {
        RepoXmlParser.Category mockedCategory = mockCategory();
        if (mockedCategory != null) {
            listView.setAdapter(new CategoryListAdapter(getActivity().getBaseContext(), mockedCategory));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RepoXmlParser.Entry entry = (RepoXmlParser.Entry) parent.getAdapter().getItem(position);
                    copyToClipBoardCallback.onCopyToClipBoard(entry.string);
                }
            });
        }
    }

    /**
     * Get a mocked Category from database
     *
     * @return mocked Category
     */
    private RepoXmlParser.Category mockCategory() {
        return new RepoXmlParser.Category("fav", favoritesDatabaseOperationsCallback.onGetAllEntries());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                EditEntryDialogFragment.createInstance(null).show(getChildFragmentManager(), "add");
                updateFavoritesList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}