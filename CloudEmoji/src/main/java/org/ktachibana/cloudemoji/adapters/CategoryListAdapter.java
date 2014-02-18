package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import org.ktachibana.cloudemoji.activities.MainActivity;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;

import java.util.List;

/**
 * Adapter that holds a list of simple_list_item_2 text views
 */
public class CategoryListAdapter extends ArrayAdapter<RepoXmlParser.Entry> {

    private List<RepoXmlParser.Entry> category;
    private LayoutInflater inflater;
    private Typeface font;

    public CategoryListAdapter(Context context, int resource, List<RepoXmlParser.Entry> category) {
        super(context, resource, category);
        this.category = category;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.font = MainActivity.getFont();
    }

    @Override
    public int getCount() {
        return category.size();
    }

    @Override
    public RepoXmlParser.Entry getItem(int position) {
        return category.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get string and note
        RepoXmlParser.Entry entry = category.get(position);
        String string = entry.string;
        String note = entry.note;
        // If no note included
        if (note.equals("")) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            view.setText(string);
            if (font != null) {
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
            if (font != null) {
                lineOne.setTypeface(font);
            }
            lineTwo.setText(note);
            return view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (category.get(position).note.equals("")) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return category.isEmpty();
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