package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.utils.IconifiedListItemView;

import java.util.List;

public class LeftDrawerListViewAdapter extends BaseAdapter {
    private List<LeftDrawerListItem> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    public LeftDrawerListViewAdapter(List<LeftDrawerListItem> items, Context context) {
        this.mItems = items;
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Standard view holder pattern
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_left_drawer, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Setup contents
        final LeftDrawerListItem item = mItems.get(i);
        viewHolder.listItemView.setIcon(mContext.getResources().getDrawable(item.getDrawable()));
        viewHolder.listItemView.setText(item.getText());

        return view;
    }

    static class ViewHolder {
        IconifiedListItemView listItemView;

        ViewHolder(View view) {
            this.listItemView = (IconifiedListItemView) view;
        }
    }

}
