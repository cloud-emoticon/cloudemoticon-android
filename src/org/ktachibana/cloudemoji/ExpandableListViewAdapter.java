package org.ktachibana.cloudemoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<RepoXmlParser.Category> categories;

    public ExpandableListViewAdapter(Context context, List<RepoXmlParser.Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String header = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_group_item, null);
        }
        TextView listHeader = (TextView) convertView
                .findViewById(R.id.expandable_list_header);
        listHeader.setText(header);
        return convertView;
    }

    @Override
    //TODO: Make it a simple_list_item_2
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        RepoXmlParser.Entry child = (RepoXmlParser.Entry) getChild(groupPosition, childPosition);
        String string = child.string;
        String note = child.note;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_child_item, null);
        }
        TextView listChild = (TextView) convertView
                .findViewById(R.id.expandable_list_child);
        if (!note.equals(""))
            listChild.setText(string + " (" + note + ")");
        else
            listChild.setText(string);
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition).name;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories.get(groupPosition).entries.get(childPosition);
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categories.get(groupPosition).entries.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
