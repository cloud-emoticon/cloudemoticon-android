package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.helpers.MyMenuItem;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;

import java.util.ArrayList;
import java.util.List;

public class SectionedMenuAdapter implements ListAdapter {
    private List<MyMenuItem> menuItemMap;
    private Context context;

    public SectionedMenuAdapter(Context context, RepoXmlParser.Emoji data) {
        this.context = context;
        menuItemMap = new ArrayList<MyMenuItem>();
        // Put section header for "local"
        menuItemMap.add(new MyMenuItem(context.getResources().getString(R.string.local), MyMenuItem.SECTION_HEADER_TYPE));
        // Put my fav
        menuItemMap.add(new MyMenuItem(context.getResources().getString(R.string.my_fav), MyMenuItem.FAV_TYPE));
        // Put section header for "repository"
        menuItemMap.add(new MyMenuItem(context.getResources().getString(R.string.repositories), MyMenuItem.SECTION_HEADER_TYPE));
        // Put all other categories
        for (RepoXmlParser.Category category : data.categories) {
            menuItemMap.add(new MyMenuItem(category.name, MyMenuItem.CATEGORY_TYPE, category));
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return menuItemMap.get(position).getType() != MyMenuItem.SECTION_HEADER_TYPE;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return menuItemMap.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItemMap.get(position);
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) convertView;
        // Determine if the menu item is section header or list item
        MyMenuItem menuItem = menuItemMap.get(position);
        // If it is a section header
        if (menuItem.getType() == MyMenuItem.SECTION_HEADER_TYPE) {
            if (textView == null) {
                textView = (TextView) inflater.inflate(R.layout.text_separator_style, parent, false);
            }
            String sectionName = menuItem.getItemName();
            textView.setText(sectionName);
        }
        // Else it is a list item
        else {
            if (textView == null) {
                textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            String itemName = menuItem.getItemName();
            textView.setText(itemName);
        }
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        return (menuItemMap.get(position).getType() == MyMenuItem.SECTION_HEADER_TYPE) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return menuItemMap.isEmpty();
    }
}
