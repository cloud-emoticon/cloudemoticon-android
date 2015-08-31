package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryStoreListViewAdapter;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDuplicatedEvent;
import org.ktachibana.cloudemoji.models.memory.StoreRepository;
import org.ktachibana.cloudemoji.net.RepositoryStoreDownloaderClient;
import org.ktachibana.cloudemoji.utils.NonCancelableProgressMaterialDialogBuilder;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.Subscribe;

public class RepositoryStoreActivity extends BaseActivity {
    private static final String STATE_TAG = "state";
    private List<StoreRepository> mRepositories;

    @Bind(R.id.list)
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_store);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mRepositories = Parcels.unwrap(savedInstanceState.getParcelable(STATE_TAG));
            showRepositoryStore();
            return;
        }

        final MaterialDialog dialog = new NonCancelableProgressMaterialDialogBuilder(this)
                .title(R.string.please_wait)
                .content(R.string.downloading)
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
        showSnackBar(event.getRepository().getAlias());
    }

    @Subscribe
    public void handle(RepositoryDuplicatedEvent event) {
        showSnackBar(R.string.duplicate_url);
    }
}
