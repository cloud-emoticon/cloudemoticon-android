package org.ktachibana.cloudemoji.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.UpdateCheckedEvent;

import de.greenrobot.event.EventBus;

/**
 * Check for update
 */
public class UpdateChecker implements Constants {
    public UpdateChecker() {
        EventBus.getDefault().register(this);
    }

    public void checkForLatestVercode() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UPDATE_CHECKER_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                EventBus.getDefault().post(new UpdateCheckedEvent(0));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonObject object = (JsonObject) new JsonParser().parse(responseString);
                int versionCode = object.get("version").getAsInt();
                EventBus.getDefault().post(new UpdateCheckedEvent(versionCode));
            }

            @Override
            public void onFinish() {
                EventBus.getDefault().unregister(this);
            }
        });
    }

    public void onEvent(EmptyEvent e) {

    }
}
