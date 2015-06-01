package org.ktachibana.cloudemoji.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.net.ApiClient;
import org.ktachibana.cloudemoji.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AccountActivity extends BaseActivity implements Constants {
    @InjectView(R.id.userName)
    EditText userName;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.emailAddress)
    EditText emailAddress;
    @InjectView(R.id.register)
    Button register;
    // TODO: change to MaterialDialog
    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.inject(this);

        mProgressDialog = new ProgressDialog(this);

        // Create handlers
        final JsonHttpResponseHandler onRegisteredHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Get code and user name string if exists
                    int code = (Integer) response.get("code");
                    String userNameString;
                    try {
                        userNameString = (String) response.get("Username");
                    } catch (JSONException e) {
                        userNameString = "";
                    }

                    // Convert status
                    ApiClient.Status status
                            = ApiClient.getRegisterStatus(code);

                    // Traverse status
                    if (status == ApiClient.Status.OK) {
                        showSnackBar(getString(R.string.register_successful) + "\n" + userNameString);
                        userName.setText("");
                        emailAddress.setText("");
                    } else if (status == ApiClient.Status.ILLEGAL_USERNAME) {
                        userName.setError(getString(R.string.illegal_username));
                        userName.setText("");
                    } else if (status == ApiClient.Status.EXISTING_USERNAME) {
                        userName.setError(getString(R.string.existing_username));
                        userName.setText("");
                    } else if (status == ApiClient.Status.ILLEGAL_EMAIL_ADDRESS) {
                        emailAddress.setError(getString(R.string.illegal_email_address));
                        emailAddress.setText("");
                    } else if (status == ApiClient.Status.EXISTING_EMAIL_ADDRESS) {
                        emailAddress.setError(getString(R.string.existing_email_address));
                        emailAddress.setText("");
                    } else {
                        Log.e(DEBUG_TAG, String.format(("Illegal code %d"), code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showSnackBar(throwable.getLocalizedMessage());
            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }
        };

        // Register listeners
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.networkAvailable(AccountActivity.this)) {
                    mProgressDialog.setMessage(getString(R.string.registering));
                    mProgressDialog.show();
                    new ApiClient()
                            .register(
                                    userName.getText().toString(),
                                    password.getText().toString(),
                                    emailAddress.getText().toString(),
                                    onRegisteredHandler);
                } else {
                    showSnackBar(R.string.bad_conn);
                }
            }
        });
    }
}