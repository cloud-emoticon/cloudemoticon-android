package org.ktachibana.cloudemoji.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryStoreListViewAdapter;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDuplicatedEvent;
import org.ktachibana.cloudemoji.models.memory.StoreRepository;
import org.ktachibana.cloudemoji.net.RepositoryStoreDownloaderClient;
import org.ktachibana.cloudemoji.ui.NonCancelableProgressMaterialDialogBuilder;
import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoryStoreActivity extends BaseActivity {
    private static final String STATE_TAG = "state";
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.contribution_prompt)
    TextView mContributionPrompt;
    private List<StoreRepository> mRepositories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_store);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mRepositories = Parcels.unwrap(savedInstanceState.getParcelable(STATE_TAG));
            showRepositoryStore();
            return;
        }

        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(this)
                .title(R.string.please_wait)
                .content(R.string.loading_repository_store)
                .show();

        new RepositoryStoreDownloaderClient().downloadRepositoryStore(
                new BaseHttpClient.ListCallback<StoreRepository>() {
                    @Override
                    public void success(List<StoreRepository> result) {
                        mRepositories = result;
                        showRepositoryStore();
                    }

                    @Override
                    public void fail(Throwable t) {
                        showSnackBar(t.getLocalizedMessage());
                    }

                    @Override
                    public void finish() {
                        dialog.dismiss();
                    }
                });

        mContributionPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(RepositoryStoreActivity.this)
                        .title(getString(R.string.repository_store_contribution_prompt))
                        .content(getString(R.string.repository_store_contribution_warning))
                        .positiveText(getString(R.string.proceed))
                        .negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(Constants.REPO_STORE_CONTRIBUTION_URL));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_TAG, Parcels.wrap(mRepositories));
    }

    private void showRepositoryStore() {
        mList.setAdapter(new RepositoryStoreListViewAdapter(this, mRepositories));
    }

    @Subscribe
    public void handle(RepositoryAddedEvent event) {
        showSnackBar(getString(R.string.added_to_repo) + ": " + event.getRepository().getAlias());
    }

    @Subscribe
    public void handle(RepositoryDuplicatedEvent event) {
        showSnackBar(R.string.duplicate_url);
    }
}
