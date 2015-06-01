package org.ktachibana.cloudemoji;

import com.loopj.android.http.AsyncHttpClient;

public class BaseHttpClient {
    protected AsyncHttpClient mClient;

    public interface IntCallback {
        void finish(boolean success, int result);
    }

    public BaseHttpClient() {
        mClient = new AsyncHttpClient();
    }
}
