package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.net.CloudApiClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountActivity extends BaseActivity {
    @InjectView(R.id.userName)
    EditText userName;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.emailAddress)
    EditText emailAddress;
    @InjectView(R.id.register)
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.inject(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CloudApiClient().register(
                        userName.getText().toString()
                        , password.getText().toString()
                        , emailAddress.getText().toString()
                        , new Callback<Void>() {
                            @Override
                            public void success(Void aVoid, Response response) {
                                if (response.getStatus() == 101)
                                    register.setText("YOLO");
                                else
                                    register.setText("WTF");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                int i = 1;
                            }
                        }
                );
            }
        });
    }
}
