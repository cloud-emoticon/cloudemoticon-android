package org.ktachibana.cloudemoji.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.inject(this);
    }
}