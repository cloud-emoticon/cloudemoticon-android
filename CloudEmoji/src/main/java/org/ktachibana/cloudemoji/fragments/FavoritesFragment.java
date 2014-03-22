package org.ktachibana.cloudemoji.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.CategoryListAdapter;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;
import org.ktachibana.cloudemoji.interfaces.OnCopyToClipBoardListener;
import org.ktachibana.cloudemoji.interfaces.OnFavoritesDatabaseOperationsListener;

import java.util.List;

/**
 * Fragment that holds a list of string in my favorites database
 */
public class FavoritesFragment extends Fragment {

    private ArrayAdapter<RepoXmlParser.Entry> adapter;
    private OnCopyToClipBoardListener copyToClipBoardCallback;
    private OnFavoritesDatabaseOperationsListener favoritesDatabaseOperationsCallback;

    public static final int ADD_REQUEST_CODE = 0;
    public static final int EDIT_REQUEST_CODE = 1;

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
        adapter = new CategoryListAdapter(getActivity().getBaseContext(), R.id.favListView, favoritesDatabaseOperationsCallback.onGetAllEntries());
        ListView listView = (ListView) rootView.findViewById(R.id.favListView);
        listView.setEmptyView(rootView.findViewById(R.id.emptyView));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepoXmlParser.Entry entry = (RepoXmlParser.Entry) parent.getAdapter().getItem(position);
                copyToClipBoardCallback.onCopyToClipBoard(entry.string);
            }
        });
        registerForContextMenu(listView);

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
            EditEntryDialogFragment editEntryDialogFragment = EditEntryDialogFragment.createInstance(entry);
            editEntryDialogFragment.setTargetFragment(this, EDIT_REQUEST_CODE);
            editEntryDialogFragment.show(getChildFragmentManager(), "edit");
        }

        // Remove entry
        else if (item.getItemId() == 1) {
            favoritesDatabaseOperationsCallback.onRemoveEntryByString(string);
            updateAdapter();
        }
        return true;
    }

    private void updateAdapter() {
        adapter.clear();
        List<RepoXmlParser.Entry> entries = favoritesDatabaseOperationsCallback.onGetAllEntries();
        for (RepoXmlParser.Entry entry : entries) {
            adapter.add(entry);
        }
        adapter.notifyDataSetChanged();
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
                EditEntryDialogFragment editEntryDialogFragment = EditEntryDialogFragment.createInstance(null);
                editEntryDialogFragment.setTargetFragment(this, ADD_REQUEST_CODE);
                editEntryDialogFragment.show(getChildFragmentManager(), "add");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        RepoXmlParser.Entry newEntry = (RepoXmlParser.Entry) intent.getSerializableExtra(EditEntryDialogFragment.NEW_ENTRY_KEY);
        if (resultCode == ADD_REQUEST_CODE) {
            favoritesDatabaseOperationsCallback.onAddEntry(newEntry);
        } else if (requestCode == EDIT_REQUEST_CODE) {
            RepoXmlParser.Entry oldEntry = (RepoXmlParser.Entry) intent.getSerializableExtra(EditEntryDialogFragment.OLD_ENTRY_KEY);
            favoritesDatabaseOperationsCallback.onUpdateEntryByString(oldEntry.string, newEntry);
        }
        updateAdapter();
    }
}