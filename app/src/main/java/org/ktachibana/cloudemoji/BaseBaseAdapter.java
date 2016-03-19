package org.ktachibana.cloudemoji;

import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.events.EmptyEvent;


/**
 * Base adapter for all list adapters to extend
 * It includes event bus
 */
public class BaseBaseAdapter extends android.widget.BaseAdapter {
    protected EventBus mBus;

    public BaseBaseAdapter() {
        mBus = EventBus.getDefault();
        mBus.register(this);
    }

    @Subscribe
    public void handle(EmptyEvent event) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
