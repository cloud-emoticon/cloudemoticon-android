package org.ktachibana.cloudemoji;

import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

public class BaseHttpClient {
    protected AsyncHttpClient mClient;

    public interface BaseCallback {
        void fail(Throwable t);

        void finish();
    }

    public interface IntCallback extends BaseCallback {
        void success(int result);
    }

    public interface ListCallback<T> extends BaseCallback {
        void success(List<T> result);
    }

    public interface ObjectCallback<T> extends BaseCallback {
        void success(T result);
    }

    public BaseHttpClient() {
        mClient = new AsyncHttpClient();
    }
}
