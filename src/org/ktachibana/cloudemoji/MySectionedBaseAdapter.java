package org.ktachibana.cloudemoji;

import java.util.List;
import java.util.Random;

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
		return data.get(section).entries.get(position);
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
		View view = null;
    	if (convertView == null) {
            view = (View) inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }
    	else
    	{
    		view = (View) convertView;
    	}
    	TextView lineOne = (TextView)view.findViewById(android.R.id.text1);
        lineOne.setText(data.get(section).entries.get(position).string);
        TextView lineTwo = (TextView)view.findViewById(android.R.id.text2);
        lineTwo.setText(data.get(section).entries.get(position).note);
        return view;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		TextView view = null;
        if (convertView == null) {
            view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            Random r = new Random();
            int randomInt = r.nextInt(5);
            int randomColor;
            switch(randomInt) {
            	case 0:
            		randomColor = context.getResources().getColor(R.color.holo_blue_light);
            		break;
            	case 1:
            		randomColor = context.getResources().getColor(R.color.holo_gray_light);
            		break;
            	case 2:
            		randomColor = context.getResources().getColor(R.color.holo_green_light);
            		break;
            	case 3:
            		randomColor = context.getResources().getColor(R.color.holo_orange_light);
            		break;
            	case 4:
            		randomColor = context.getResources().getColor(R.color.holo_red_light);
            		break;
            	default:
            		randomColor = context.getResources().getColor(R.color.holo_blue_light);
            }
            view.setBackgroundColor(randomColor);
        }
        else
        {
        	view = (TextView) convertView;
        }
        view.setText(context.getString(R.string.category)+ ": " + data.get(section).name);
        return view;
	}

}
