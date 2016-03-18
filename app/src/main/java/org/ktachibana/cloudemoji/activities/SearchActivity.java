package org.ktachibana.cloudemoji.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.SearchFinishedEvent;
import org.ktachibana.cloudemoji.events.SearchInitiatedEvent;
import org.ktachibana.cloudemoji.fragments.SearchResultFragmentBuilder;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Log.i("submit", query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("change", newText);
        return false;
    }
}
