package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryStoreListViewAdapter;
import org.ktachibana.cloudemoji.models.inmemory.StoreRepository;
import org.ktachibana.cloudemoji.utils.UncancelableProgressMaterialDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RepositoryStoreActivity extends BaseActivity implements Constants {
    @InjectView(R.id.list)
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_store);

        ButterKnife.inject(this);

        final MaterialDialog dialog = new UncancelableProgressMaterialDialogBuilder(this)
                .title(R.string.please_wait)
                .content(R.string.downloading)
                .show();

        new AsyncHttpClient().get(
                this,
                STORE_URL,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        List<StoreRepository> repositories = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String alias = object.getString("name");
                                String url = object.getString("codeurl");
                                String description = object.getString("introduction");
                                String author = object.getString("creator");
                                String authorUrl = object.getString("creatorurl");
                                String authorIconUrl = object.getString("iconurl");
                                StoreRepository repository = new StoreRepository(
                                        alias, url, description, author, authorUrl, authorIconUrl
                                );
                                repositories.add(repository);
                            }
                            showRepositoryStore(repositories);
                        } catch (JSONException e) {
                            showSnackBar(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        showSnackBar(throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onFinish() {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
    }

    private void showRepositoryStore(List<StoreRepository> repositories) {
        mList.setAdapter(new RepositoryStoreListViewAdapter(this, repositories));
    }
}
