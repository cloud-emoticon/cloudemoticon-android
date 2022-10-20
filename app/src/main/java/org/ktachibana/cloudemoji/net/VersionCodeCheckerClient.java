package org.ktachibana.cloudemoji.net;

import androidx.annotation.NonNull;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.NetworkUtils;

import cz.msebera.android.httpclient.Header;

public class VersionCodeCheckerClient extends BaseHttpClient {
    public void checkForLatestVersionCode(@NonNull final IntCallback callback) {
        if (NetworkUtils.networkAvailable()) {
            mClient.get(Constants.UPDATE_CHECKER_URL, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int versionCode = response.getInt("version");
                        callback.success(versionCode);
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
