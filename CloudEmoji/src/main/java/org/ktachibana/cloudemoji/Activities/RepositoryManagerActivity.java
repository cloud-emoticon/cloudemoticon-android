package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.RepositoryListFragment;


public class RepositoryManagerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_manager);
        getSupportFragmentManager().beginTransaction().replace(R.id.repositoryMainContainer, new RepositoryListFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDownloadButtonClicked(View view) {
        Toast.makeText(this, "download clicked", Toast.LENGTH_SHORT).show();
    }

    public void onDeleteButtonClicked(View view) {
        Toast.makeText(this, "delete clicked", Toast.LENGTH_SHORT).show();
    }
}
