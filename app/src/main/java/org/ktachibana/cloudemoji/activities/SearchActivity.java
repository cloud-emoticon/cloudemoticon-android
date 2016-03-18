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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private LinkedHashMap<Long, Source> sourceCache;
    private HashMap<String, List<Entry>> searchCache;

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

        replaceMainContainer(new SearchResultFragmentBuilder("", new ArrayList<Entry>()).build());
    }

    /**
     * Put all entries of favorites, histories and all sources into search cache
     */
    private HashMap<String, List<Entry>> initializeSearchCache(LinkedHashMap<Long, Source> sourceCache) {
        HashMap<String, List<Entry>> searchCache = new HashMap<String, List<Entry>>();

        // Favorites
        for (Favorite favorite : Favorite.listAll(Favorite.class)) {
            addToSearchCache(searchCache, new Entry(favorite.getEmoticon(), favorite.getDescription()));
        }

        // History
        for (History history : History.listAll(History.class)) {
            addToSearchCache(searchCache, new Entry(history.getEmoticon(), history.getDescription()));
        }

        // Sources
        for (Map.Entry<Long, Source> source : sourceCache.entrySet()) {
            for (Category category : source.getValue().getCategories()) {
                for (Entry entry : category.getEntries()) {
                    addToSearchCache(searchCache, entry);
                }
            }
        }

        return searchCache;
    }

    private void addToSearchCache(HashMap<String, List<Entry>> searchCache, Entry entry) {
        List<String> keywords = Arrays.asList(entry.getEmoticon(), entry.getDescription());

        for (String keyword : keywords) {
            if (!searchCache.containsKey(keyword)) {
                searchCache.put(keyword, new ArrayList<Entry>());
            }
            searchCache.get(keyword).add(entry);
        }
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
        List<Entry> result = searchCache.get(query);
        if (result == null) {
            result = new ArrayList<>();
        }
        replaceMainContainer(new SearchResultFragmentBuilder(query, result).build());
    }
}
