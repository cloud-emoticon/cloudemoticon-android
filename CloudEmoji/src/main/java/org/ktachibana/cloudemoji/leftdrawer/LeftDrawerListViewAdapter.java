package org.ktachibana.cloudemoji.leftdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;

import java.util.List;

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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_left_drawer, viewGroup, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.leftDrawerListItemImageView);
        TextView textView = (TextView) view.findViewById(R.id.leftDrawerListItemTextView);
        imageView.setImageDrawable(context.getResources().getDrawable(items.get(i).getDrawable()));
        textView.setText(items.get(i).getText());
        return view;
    }
}
