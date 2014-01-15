package org.ktachibana.cloudemoji;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Adapter that holds a list of simple_list_item_2 text views
 */
public class DoubleItemListAdapter implements ListAdapter {

    private LayoutInflater inflater;
    private RepoXmlParser.Category cat;
    private boolean overrideSystemFont;

    public DoubleItemListAdapter(Context context, RepoXmlParser.Category cat, boolean overrideSystemFont) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cat = cat;
        this.overrideSystemFont = overrideSystemFont;

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return cat.entries.size();
    }

    @Override
    public Object getItem(int position) {
        return cat.entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get whether to override system font
        Typeface font = MainActivity.getTypeface();
        // Get string and note
        RepoXmlParser.Entry entry = cat.entries.get(position);
        String string = entry.string;
        String note = entry.note;
        // If no note included
        if (note.equals("")) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            view.setText(string);
            if (overrideSystemFont) {
                view.setTypeface(font);
            }
            return view;
        } else {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView lineOne = (TextView) view.findViewById(android.R.id.text1);
            TextView lineTwo = (TextView) view.findViewById(android.R.id.text2);
            lineOne.setText(string);
            if (overrideSystemFont) {
                lineOne.setTypeface(font);
            }
            lineTwo.setText(note);
            return view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (cat.entries.get(position).note.equals("")) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return cat.entries.isEmpty();
    }

    /**
     * Get back the underlying Entry from the list item view created by this adapter
     *
     * @param view view crated by this adapter
     * @return the underlying Entry
     */
    public static RepoXmlParser.Entry getEntryFromView(View view) {
        // Determine whether the view is simple_list_item_1 or 2
        TextView noteView = (TextView) view.findViewById(android.R.id.text2);
        // If noteView does not exist, then it is an simple_list_item_1
        if (noteView == null) {
            String string = ((TextView) view).getText().toString();
            return new RepoXmlParser.Entry(string, "");
        } else {
            String string = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
            String note = ((TextView) view.findViewById(android.R.id.text2)).getText().toString();
            return new RepoXmlParser.Entry(string, note);
        }
    }
}