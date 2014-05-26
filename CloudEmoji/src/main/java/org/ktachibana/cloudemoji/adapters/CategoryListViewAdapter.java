package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.ktachibana.cloudemoji.events.EmoticonCopiedEvent;
import org.ktachibana.cloudemoji.models.Entry;

import java.util.List;

import de.greenrobot.event.EventBus;

public class CategoryListViewAdapter implements ListAdapter, View.OnClickListener {

    private List<Entry> mCategory;
    private LayoutInflater mInflater;

    public CategoryListViewAdapter(Context context, List<Entry> category) {
        this.mCategory = category;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return mCategory.size();
    }

    @Override
    public Entry getItem(int position) {
        return mCategory.get(position);
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
        Entry entry = mCategory.get(position);
        String string = entry.getEmoticon();
        String note = entry.getDescription();

        // If no note included
        if (note.equals("")) {
            // Inflate the view if not
            TextView view = (TextView) convertView;
            if (view == null) {
                view = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            // Set text
            view.setText(string);

            // TODO: hacky!
            view.setTag(string);
            view.setOnClickListener(this);

            return view;
        }

        // Else included
        else {
            // Inflate the view if not
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            // Find views
            TextView lineOne = (TextView) view.findViewById(android.R.id.text1);
            TextView lineTwo = (TextView) view.findViewById(android.R.id.text2);

            // Set emoticon text
            lineOne.setText(string);

            // TODO: hacky!
            lineOne.setTag(string);
            lineOne.setOnClickListener(this);

            // Set description text
            lineTwo.setText(note);

            return view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (mCategory.get(position).getDescription().equals("")) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return mCategory.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    // TODO: hacky!
    @Override
    public void onClick(View view) {
        // If it is the text view containing emoticon pressed
        if (view instanceof TextView) {
            if (view.getTag() != null) {
                try {
                    // Get the string and tell anybody who cared about an emoticon being copied
                    String string = (String) view.getTag();
                    EventBus.getDefault().post(new EmoticonCopiedEvent(string));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}