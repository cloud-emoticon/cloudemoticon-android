package org.ktachibana.cloudemoji;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Fires notification after boot up if user preference said so
 */
public class BootUpDummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Test from dummy")
                .setContentText("233333")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);
        finish();
    }
}
