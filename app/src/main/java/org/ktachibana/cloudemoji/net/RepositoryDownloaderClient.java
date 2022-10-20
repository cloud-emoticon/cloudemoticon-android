package org.ktachibana.cloudemoji.net;

import androidx.annotation.NonNull;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.BaseHttpClient;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.utils.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;

public class RepositoryDownloaderClient extends BaseHttpClient {
    public void downloadSource(@NonNull final Repository item, @NonNull final ObjectCallback<Repository> callback) {
        if (NetworkUtils.networkAvailable()) {
            mClient.get(
                    item.getUrl(),
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            // Write to file
                            File repositoryFile = new File(BaseApplication.context().getFilesDir(), item.getFileName());
                            FileOutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(repositoryFile);
                                IOUtils.write(responseBody, outputStream);

                                // Set repository to available and SAVE it
                                item.setAvailable(true);
                                item.save();

                                callback.success(item);
                            } catch (Exception e) {
                                callback.fail(e);
                            } finally {
                                IOUtils.closeQuietly(outputStream);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            callback.fail(error);
                        }

                        @Override
                        public void onFinish() {
                            callback.finish();
                        }
                    }
            );
        } else {
            callback.fail(new NetworkUnavailableException());
            callback.finish();
        }
    }
}
