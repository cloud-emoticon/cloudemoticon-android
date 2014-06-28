package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linearlistview.LinearListView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Source;
import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class SourceListViewAdapter extends SectionedBaseAdapter {
    private Source mSource;
    private Context mContext;
    private LayoutInflater mInflater;

    public SourceListViewAdapter(Context context, Source source) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSource = source;
    }

    @Override
    public Object getItem(int section, int position) {
        return mSource.getCategories().get(section).getEntries().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return mSource.getCategories().size();
    }

    @Override
    public int getCountForSection(int section) {
        return mSource.getCategories().get(section).getEntries().size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final Entry entry = (Entry) getItem(section, position);
        ((TextView) convertView).setText(entry.getEmoticon());

        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue_light));
            ((TextView) convertView).setTextAppearance(mContext, android.R.style.TextAppearance_Small);
            ((TextView) convertView).setTypeface(null, Typeface.BOLD);
            ((TextView) convertView).setTextColor(mContext.getResources().getColor(android.R.color.white));
        }

        final Category category = mSource.getCategories().get(section);
        ((TextView) convertView).setText(category.getName());

        return convertView;
    }
}
