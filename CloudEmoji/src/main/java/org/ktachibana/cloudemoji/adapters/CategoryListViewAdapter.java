package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.ktachibana.cloudemoji.models.Entry;

import java.util.List;

public class CategoryListViewAdapter implements ListAdapter {

    private List<Entry> category;
    private LayoutInflater inflater;

    public CategoryListViewAdapter(Context context, List<Entry> category) {
        this.category = category;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return category.size();
    }

    @Override
    public Entry getItem(int position) {
        return category.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get string and note
        Entry entry = category.get(position);
        String string = entry.getEmoticon();
        String note = entry.getDescription();

        // If no note included
        if (note.equals("")) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            view.setText(string);
            return view;
        }

        // Else included
        else {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView lineOne = (TextView) view.findViewById(android.R.id.text1);
            TextView lineTwo = (TextView) view.findViewById(android.R.id.text2);
            lineOne.setText(string);
            lineTwo.setText(note);
            return view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (category.get(position).getDescription().equals("")) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return category.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }
}