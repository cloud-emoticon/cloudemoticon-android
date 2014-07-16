package org.ktachibana.cloudemoji.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountAuthenticationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }
}
