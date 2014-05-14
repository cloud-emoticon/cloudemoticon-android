package org.ktachibana.cloudemoji.adapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.ktachibana.cloudemoji.Constants;

import java.util.ArrayList;

public class DrawerListViewAdapter implements ListAdapter, Constants {
    private ArrayList<DrawerListItem> items;

    public DrawerListViewAdapter(ArrayList<DrawerListItem> items) {
        this.items = items;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return items.get(i).getType() != DRAWER_LIST_ITEM_TYPE_HEADER;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return items.get(i).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
