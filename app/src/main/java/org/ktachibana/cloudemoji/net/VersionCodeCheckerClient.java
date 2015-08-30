package org.ktachibana.cloudemoji.net;

import android.support.annotation.NonNull;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.utils.SystemUtils;

public class VersionCodeCheckerClient extends BaseHttpClient implements Constants {
    public void checkForLatestVersionCode(@NonNull final IntCallback callback) {
        if (SystemUtils.networkAvailable()) {
            mClient.get(UPDATE_CHECKER_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int versionCode = response.getInt("version");
                        callback.success(versionCode);
                    } catch (JSONException e) {
                        callback.fail(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    callback.fail(throwable);
                }

                @Override
                public void onFinish() {
                    callback.finish();
                }
            });
        } else {
            callback.fail(NETWORK_UNAVAILABLE_EXCEPTION);
            callback.finish();
        }
    }
}
