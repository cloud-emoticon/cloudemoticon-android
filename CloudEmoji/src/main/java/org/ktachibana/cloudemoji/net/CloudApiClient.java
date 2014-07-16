package org.ktachibana.cloudemoji.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ktachibana.cloudemoji.Constants;

public class CloudApiClient implements Constants {
    private static final String REGISTER = "/api/account.php?f=register";
    private AsyncHttpClient mClient;

    public CloudApiClient() {
        mClient = new AsyncHttpClient();
    }

    public static Status getRegisterStatus(int code) {
        switch (code) {
            case 101:
                return Status.OK;
            case 208:
                return Status.ILLEGAL_USERNAME;
            case 209:
                return Status.EXISTING_USERNAME;
            case 210:
                return Status.ILLEGAL_EMAIL_ADDRESS;
            case 211:
                return Status.EXISTING_EMAIL_ADDRESS;
            default:
                return Status.NA;
        }
    }

    public void register(String userName,
                         String password,
                         String emailAddress,
                         JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.add("u", userName);
        params.add("p", password);
        params.add("e", emailAddress);
        mClient.post(CLOUD_API_HOST + REGISTER, params, handler);
    }

    public enum Status {
        OK,
        ILLEGAL_USERNAME,
        EXISTING_USERNAME,
        ILLEGAL_EMAIL_ADDRESS,
        EXISTING_EMAIL_ADDRESS,
        NA
    }
}
