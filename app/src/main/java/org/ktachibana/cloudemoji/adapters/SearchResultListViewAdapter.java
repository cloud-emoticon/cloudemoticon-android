package org.ktachibana.cloudemoji.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.ui.ScrollableEmoticonMaterialDialogBuilder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map.Entry<Entry, HashSet<String>>> mResults;
    private LayoutInflater mInflater;

    public SearchResultListViewAdapter(Context context, List<Map.Entry<Entry, HashSet<String>>> results) {
        mContext = context;
        mResults = results;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        final Map.Entry<Entry, HashSet<String>> result = mResults.get(position);
        final Entry entry = result.getKey();
        final HashSet<String> sources = result.getValue();

        // Build sources string
        final StringBuilder sourcesStringBuilder = new StringBuilder();
        sourcesStringBuilder.append("from: ");
        Iterator<String> i = sources.iterator();
        while (i.hasNext()) {
            sourcesStringBuilder.append(i.next());
            if (i.hasNext()) {
                sourcesStringBuilder.append(", ");
            }
        }
        final String sourcesString = sourcesStringBuilder.toString();

        // Build description
        final String description;
        if ("".equals(entry.getDescription())) {
            description = sourcesString;
        } else {
            description = String.format("%s\n%s", entry.getDescription(), sourcesString);
        }

        viewHolder.emoticon.setText(entry.getEmoticon());
        viewHolder.description.setText(description);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.emoticonTextView)
        TextView emoticon;

        @Bind(R.id.descriptionTextView)
        TextView description;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
