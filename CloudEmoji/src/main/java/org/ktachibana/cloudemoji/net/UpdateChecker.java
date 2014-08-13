package org.ktachibana.cloudemoji.net;

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
                int vercode = Integer.valueOf(responseString);
                EventBus.getDefault().post(new UpdateCheckedEvent(vercode));
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
