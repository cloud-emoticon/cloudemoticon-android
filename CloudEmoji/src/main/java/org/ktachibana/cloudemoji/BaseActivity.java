package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Base activity for all activities with UI to extend
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
