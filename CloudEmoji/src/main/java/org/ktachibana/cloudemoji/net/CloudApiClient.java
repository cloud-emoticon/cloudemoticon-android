package org.ktachibana.cloudemoji.net;

import org.ktachibana.cloudemoji.Constants;

import retrofit.Callback;
import retrofit.RestAdapter;

public class CloudApiClient implements Constants {
    private CloudApiServiceInterface mService;

    public CloudApiClient() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(CLOUD_API_HOST)
                .build();

        mService = adapter.create(CloudApiServiceInterface.class);
    }

    public void register(String userName, String password, String emailAddress, Callback<Void> callback) {
        mService.register(userName, password, emailAddress, callback);
    }
}
