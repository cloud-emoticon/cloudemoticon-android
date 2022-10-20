package org.ktachibana.cloudemoji.net;

import androidx.annotation.NonNull;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.models.memory.StoreRepository;
import org.ktachibana.cloudemoji.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RepositoryStoreDownloaderClient extends BaseHttpClient {
    public void downloadRepositoryStore(@NonNull final ListCallback callback) {
        if (NetworkUtils.networkAvailable()) {
            mClient.get(Constants.STORE_URL, new JsonHttpResponseHandler() {
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
                        callback.success(repositories);
                    } catch (JSONException e) {
                        callback.fail(e);
                    }
                }

                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    callback.fail(throwable);
                }

                @Override
                public void onFinish() {
                    callback.finish();
                }
            });
        } else {
            callback.fail(new NetworkUnavailableException());
            callback.finish();
        }
    }
}
