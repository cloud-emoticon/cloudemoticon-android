package org.ktachibana.cloudemoji.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.AddRepositoryDialogFragment;
import org.ktachibana.cloudemoji.fragments.RepositoryListFragment;


public class RepositoryManagerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_manager);
        getSupportFragmentManager().beginTransaction().replace(R.id.repositoryMainContainer, new RepositoryListFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repository_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_repository: {
                new AddRepositoryDialogFragment().show(getSupportFragmentManager(), "add_repository");
                return true;
            }
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
