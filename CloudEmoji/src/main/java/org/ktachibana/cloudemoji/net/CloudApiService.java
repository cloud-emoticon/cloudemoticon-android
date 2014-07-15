package org.ktachibana.cloudemoji.net;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.POST;

public interface CloudApiService {
    @POST("/api/account.php?f=register")
    void register(
            @Field("u") String userName,
            @Field("p") String password,
            @Field("e") String emailAddress,
            Callback<Void> callback);
}
