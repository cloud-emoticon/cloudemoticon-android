package org.ktachibana.cloudemoji;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

public class MyExpandableListAdapter implements ExpandableListAdapter {

	private Context context;
	private List<RepoXmlParser.Category> data;

	public MyExpandableListAdapter(Context context,
			List<RepoXmlParser.Category> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).entries.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView == null) {
			view = new TextView(context);
		}
		else
		{
			view = (TextView) convertView;
		}
		view.setText(data.get(groupPosition).entries.get(childPosition).string);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).entries.size();
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView view;
		if (convertView == null) {
			view = new TextView(context);
		}
		else
		{
			view = (TextView) convertView;
		}
		view.setText("Category: " + data.get(groupPosition).name);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		
	}

}
