package org.ktachibana.cloudemoji.input;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.adapters.HistoryListViewAdapter;
import org.ktachibana.cloudemoji.adapters.SourceListViewAdapter;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.disk.History;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

public class InputMethodPagerAdapter extends PagerAdapter {


    public interface OnInputCompletedListener {
        void onInputCompleted(Entry entry);
    }

    private int pageFavIdv;
    private int pageHisIdx;


    private final List<Long> mIdList;
    private SourceInMemoryCache mCache;
    private OnInputCompletedListener mLinstner;
    private Context mContext;
    private FavoriteListViewAdapter favAdaptor;
    private HashMap<Integer, Runnable> updateActions = new HashMap<>();
    private EventBus BUS;

    InputMethodPagerAdapter(Context context, SourceInMemoryCache cache) {
        mCache = cache;
        mIdList = cache.getAllKeys();
        mContext = context;
        pageFavIdv = 0;
        pageHisIdx = getCount() - 1;
    }

    void setOnInputCompletedListener(OnInputCompletedListener lis) {
        mLinstner = lis;
    }

    public View createSourceView(final ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        View rootView = inflater.inflate(R.layout.fragment_source, null);
        final PinnedHeaderListView list = (PinnedHeaderListView) rootView.findViewById(R.id.list);
        final Source source = mCache.get(fromPositionToKey(position));
        final SourceListViewAdapter adapter = new SourceListViewAdapter(container.getContext(), source);
        list.setAdapter(adapter);
        updateActions.put(position, new Runnable() {
            @Override
            public void run() {
                SourceListViewAdapter adapter = new SourceListViewAdapter(container.getContext(), source);
                list.setAdapter(adapter);
            }
        });
        list.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                if (mLinstner != null) {
                    Entry entry = (Entry) adapter.getItem(section, position);
                    mLinstner.onInputCompleted(entry);
                }
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
        });
        container.addView(rootView);
        return rootView;
    }

    public View createFavView(ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        View favoriteEmptyView = rootView.findViewById(R.id.emptyView);
        TextView emptyViewTextView = (TextView) rootView.findViewById(R.id.emptyViewTextView);
        final FavoriteListViewAdapter adapter = new FavoriteListViewAdapter(mContext, false);
        favAdaptor = adapter;
        DragSortListView listView = (DragSortListView) rootView.findViewById(R.id.favoriteListView);
        listView.setEmptyView(favoriteEmptyView);
        emptyViewTextView.setText(mContext.getString(R.string.no_favorite_prompt));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mLinstner != null) {
                    Favorite favorite = (Favorite) adapter.getItem(i);
                    Entry entry = new Entry(favorite.getEmoticon(), favorite.getDescription());
                    mLinstner.onInputCompleted(entry);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        container.addView(rootView);
        return rootView;
    }

    public View createHistory(ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        View favoriteEmptyView = rootView.findViewById(R.id.emptyView);
        TextView emptyViewTextView = (TextView) rootView.findViewById(R.id.emptyViewTextView);
        final HistoryListViewAdapter adapter = new HistoryListViewAdapter(mContext);
        ListView listView = (ListView) rootView.findViewById(R.id.historyListView);
        listView.setEmptyView(favoriteEmptyView);
        emptyViewTextView.setText(mContext.getString(R.string.no_favorite_prompt));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mLinstner != null) {
                    History history = (History) adapter.getItem(i);
                    Entry entry = new Entry(history.getEmoticon(), history.getDescription());
                    mLinstner.onInputCompleted(entry);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        container.addView(rootView);
        return rootView;
    }

    void updateView(boolean isAdd) {

        if (favAdaptor != null && isAdd) {
            favAdaptor.updateFavorites();
        }
        if (!isAdd) {
            for (Map.Entry<Integer, Runnable> runnable : updateActions.entrySet())
                runnable.getValue().run();
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (position == pageFavIdv)
            view = createFavView(container);
        else if (position == pageHisIdx)
            view = createHistory(container);
        else
            view = createSourceView(container, position - 1);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position == pageFavIdv)
            favAdaptor = null;
        else
            updateActions.remove(position);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mCache.size() + 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == pageFavIdv) return mContext.getString(R.string.fav);
        if (position == pageHisIdx) return mContext.getString(R.string.history);
        return mCache.get(fromPositionToKey(position - 1)).getAlias();
    }

    private long fromPositionToKey(int position) {
        return mIdList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
