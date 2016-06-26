package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.SearchResultFragmentBuilder;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.disk.History;
import org.ktachibana.cloudemoji.models.memory.Category;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private LinkedHashMap<Long, Source> sourceCache;
    private HashMap<Entry, HashSet<String>> searchCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            sourceCache = Parcels.unwrap(getIntent().getExtras().getParcelable(MainActivity.SOURCE_CACHE_TAG));
        } else {
            sourceCache = Parcels.unwrap(savedInstanceState.getParcelable(MainActivity.SOURCE_CACHE_TAG));
        }
        searchCache = initializeSearchCache(sourceCache);
        onQuery("");
    }

    /**
     * Put all entries of favorites, histories and all sources into search cache
     */
    private HashMap<Entry, HashSet<String>> initializeSearchCache(LinkedHashMap<Long, Source> sourceCache) {
        HashMap<Entry, HashSet<String>> searchCache = new HashMap<>();

        // Favorites
        for (Favorite favorite : Favorite.listAll(Favorite.class)) {
            addToSearchCache(searchCache, new Entry(favorite.getEmoticon(), favorite.getDescription()), getString(R.string.fav));
        }

        // History
        for (History history : History.listAll(History.class)) {
            addToSearchCache(searchCache, new Entry(history.getEmoticon(), history.getDescription()), getString(R.string.history));
        }

        // Sources
        for (Map.Entry<Long, Source> source : sourceCache.entrySet()) {
            for (Category category : source.getValue().getCategories()) {
                for (Entry entry : category.getEntries()) {
                    addToSearchCache(searchCache, entry, source.getValue().getAlias());
                }
            }
        }

        return searchCache;
    }

    private void addToSearchCache(HashMap<Entry, HashSet<String>> searchCache, Entry entry, String source) {
        if (!searchCache.containsKey(entry)) {
            searchCache.put(entry, new HashSet<String>());
        }
        searchCache.get(entry).add(source);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MainActivity.SOURCE_CACHE_TAG, Parcels.wrap(sourceCache));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Auto focus SearchView
        SearchView searchView = (SearchView) (MenuItemCompat.getActionView(menu.findItem(R.id.search)));
        searchView.setIconified(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        onQuery(query);
        return true;
    }

    private void onQuery(String query) {
        if ("".equals(query)) {
            replaceMainContainer(new SearchResultFragmentBuilder("", new ArrayList<Map.Entry<Entry, HashSet<String>>>()).build());
        } else {
            List<Map.Entry<Entry, HashSet<String>>> result = new ArrayList<>();
            for (Map.Entry<Entry, HashSet<String>> entry : searchCache.entrySet()) {
                if (entry.getKey().searchQuery(query)) {
                    result.add(entry);
                }
            }
            replaceMainContainer(new SearchResultFragmentBuilder(query, result).build());
        }
    }
}
