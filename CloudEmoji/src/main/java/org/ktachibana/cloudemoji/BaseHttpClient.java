package org.ktachibana.cloudemoji;

import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

public class BaseHttpClient {
    protected AsyncHttpClient mClient;

    public interface BaseCallback {
        void fail(Throwable t);
    }

    public interface IntCallback extends BaseCallback {
        void success(int result);
    }

    public interface ListCallback extends BaseCallback {
        void success(List result);
    }

    public BaseHttpClient() {
        mClient = new AsyncHttpClient();
    }
}
