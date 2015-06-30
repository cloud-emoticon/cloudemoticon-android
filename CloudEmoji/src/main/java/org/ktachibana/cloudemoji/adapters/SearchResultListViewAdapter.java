package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.memory.Entry;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchResultListViewAdapter extends BaseAdapter {
    private List<Entry> mResults;
    private LayoutInflater mInflater;

    public SearchResultListViewAdapter(Context mContext, List<Entry> results) {
        mResults = results;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Standard view holder pattern
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_history, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Entry result = mResults.get(position);

        viewHolder.emoticon.setText(result.getEmoticon());
        if (result.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(result.getDescription());
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.emoticonTextView)
        TextView emoticon;

        @InjectView(R.id.descriptionTextView)
        TextView description;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
