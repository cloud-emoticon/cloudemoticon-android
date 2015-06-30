package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.disk.History;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HistoryListViewAdapter extends BaseAdapter {
    private List<History> mHistory;
    private LayoutInflater mInflater;

    public HistoryListViewAdapter(Context mContext) {
        mHistory = History.listAll(History.class);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mHistory.size();
    }

    @Override
    public Object getItem(int position) {
        return mHistory.get(position);
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

        final History history = mHistory.get(position);

        viewHolder.emoticon.setText(history.getEmoticon());
        if (history.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(history.getDescription());
        }

        return convertView;
    }

    public void updateHistory() {
        mHistory = History.listAll(History.class);
        notifyDataSetChanged();
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
