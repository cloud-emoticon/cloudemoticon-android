package org.ktachibana.cloudemoji.drawer;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;

import java.util.ArrayList;

public class DrawerListViewAdapter implements ListAdapter, Constants {
    private ArrayList<DrawerListItem> items;
    private LayoutInflater inflater;
    private Context context;

    public DrawerListViewAdapter(ArrayList<DrawerListItem> items, Context context) {
        this.items = items;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        int itemType = items.get(i).getType();
        switch (itemType) {
            case DRAWER_LIST_ITEM_TYPE_HEADER: {
                if (view == null) {
                    view = inflater.inflate(R.layout.list_item_drawer_header, viewGroup, false);
                }
                ((TextView) view).setText(items.get(i).getText());
                break;
            }
            case DRAWER_LIST_ITEM_TYPE_SOURCE: {
                if (view == null) {
                    view = inflater.inflate(R.layout.list_item_drawer_source, viewGroup, false);
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.drawerListItemSourceImageView);
                imageView.setImageDrawable(context.getResources().getDrawable(items.get(i).getDrawble()));
                TextView textView = (TextView) view.findViewById(R.id.drawerListItemSourceTextView);
                textView.setText(items.get(i).getText());
                break;
            }
            case DRAWER_LIST_ITEM_TYPE_CATEGORY: {
                if (view == null) {
                    view = inflater.inflate(R.layout.list_item_drawer_category, viewGroup, false);
                }
                TextView textView = (TextView) view.findViewById(R.id.drawerListItemCategoryTextView);
                textView.setText(items.get(i).getText());
                break;
            }
            default:
                break;
        }
        return view;
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
