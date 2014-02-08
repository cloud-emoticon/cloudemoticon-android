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
        menu.add(0, 0, 0, R.string.remove_from_fav);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            // Get string and note from view
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            View rootView = info.targetView;
            RepoXmlParser.Entry entry = CategoryListAdapter.getEntryFromView(rootView);
            String string = entry.string;

            // Remove from database
            favoritesDatabaseOperationsCallback.onRemoveEntryByString(string);
            updateFavoritesList();
        }
        return true;
    }

    /**
     * Get a mocked Category from database
     *
     * @return mocked Category
     */
    private RepoXmlParser.Category mockCategory() {
        return new RepoXmlParser.Category("fav", favoritesDatabaseOperationsCallback.onGetAllEntries());
    }

    /**
     * Update the favorite list view
     */
    private void updateFavoritesList() {
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
}