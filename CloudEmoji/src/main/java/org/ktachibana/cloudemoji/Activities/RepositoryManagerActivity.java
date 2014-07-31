package org.ktachibana.cloudemoji.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.fragments.RepositoryFragment;


public class RepositoryManagerActivity extends BaseActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_manager);

        RepositoryFragment fragment = new RepositoryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.repositoryMainContainer, fragment)
                .commit();

        // Pop up add repository dialog if from store scheme
        if (getIntent() != null) {
            String scheme = getIntent().getScheme();
            if (scheme != null) {
                if (scheme.equals("cloudemoticon") || scheme.equals("cloudemoticons")) {
                    String passedInUrl = getIntent().getDataString().replaceFirst("cloudemoticon", "http");
                    fragment.popupAddRepositoryDialog(passedInUrl);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                setResult(REPOSITORY_MANAGER_REQUEST_CODE);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
