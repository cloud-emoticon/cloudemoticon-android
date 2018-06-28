package org.ktachibana.cloudemoji.net;

import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.R;

public class NetworkUnavailableException extends Exception {
    public NetworkUnavailableException() {
        super(BaseApplication.Companion.context().getString(R.string.bad_conn));
    }
}
