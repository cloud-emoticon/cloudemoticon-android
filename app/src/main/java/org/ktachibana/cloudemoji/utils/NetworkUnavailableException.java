package org.ktachibana.cloudemoji.utils;

import org.ktachibana.cloudemoji.BaseApplication;
import org.ktachibana.cloudemoji.R;

public class NetworkUnavailableException extends Exception {
    public NetworkUnavailableException() {
        super(BaseApplication.context().getString(R.string.bad_conn));
    }
}
