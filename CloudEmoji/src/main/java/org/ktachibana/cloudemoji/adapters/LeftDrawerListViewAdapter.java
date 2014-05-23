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
    private List<LeftDrawerListItem> items;
    private Context context;

    public LeftDrawerListViewAdapter(List<LeftDrawerListItem> items, Context context) {
        this.items = items;
        this.context = context;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_left_drawer, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView
                .setImageDrawable(context.getResources().getDrawable(items.get(i).getDrawable()));
        viewHolder.textView.setText(items.get(i).getText());
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
