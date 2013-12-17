package org.ktachibana.cloudemoji;

import java.util.List;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySectionedBaseAdapter extends SectionedBaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<RepoXmlParser.Category> data;
	
	public MySectionedBaseAdapter(Context context, List<RepoXmlParser.Category> data) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
	}
	
	@Override
	public Object getItem(int section, int position) {
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public int getSectionCount() {
		return data.size();
	}

	@Override
	public int getCountForSection(int section) {
		return data.get(section).entries.size();
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		TextView view = null;
    	if (convertView == null) {
            view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
    	else
    	{
    		view = (TextView) convertView;
    	}
        view.setText(data.get(section).entries.get(position).string);
        return view;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		TextView view = null;
        if (convertView == null) {
            view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        else
        {
        	view = (TextView) convertView;
        }
        view.setText("Caterogy: " + data.get(section).name);
        view.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_light));
        return view;
	}

}
