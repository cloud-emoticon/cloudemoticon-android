package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LeftDrawerListViewAdapter extends BaseAdapter {
    private List<LeftDrawerListItem> mItems;
    private Context mContext;

    public LeftDrawerListViewAdapter(List<LeftDrawerListItem> items, Context context) {
        this.mItems = items;
        this.mContext = context;
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
            LayoutInflater inflater
                    = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_left_drawer, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Setup contents
        viewHolder.imageView
                .setImageDrawable(mContext.getResources().getDrawable(mItems.get(i).getDrawable()));
        viewHolder.textView.setText(mItems.get(i).getText());

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.leftDrawerListItemImageView)
        ImageView imageView;
        @InjectView(R.id.leftDrawerListItemTextView)
        TextView textView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
