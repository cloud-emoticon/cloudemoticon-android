package org.ktachibana.cloudemoji.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.activities.MainActivity;

/**
 * Abstract Notification codes from MainActivity to be shared among MainActivity and BootUpDummyActivity
 */
public class NotificationHelper {

    /**
     * Switch notification state to according to current user preference
     */
    public static void switchNotificationState(Context context, String notificationVisibility) {
        // Cancel current notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.PERSISTENT_NOTIFICATION_ID);
        if (notificationVisibility.equals("no")) {
            notificationManager.cancel(MainActivity.PERSISTENT_NOTIFICATION_ID);
        } else if (notificationVisibility.equals("panel")) {
            Notification notification = buildNotification(context, Notification.PRIORITY_MIN);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(MainActivity.PERSISTENT_NOTIFICATION_ID, notification);
        } else if (notificationVisibility.equals("both")) {
            Notification notification = buildNotification(context, Notification.PRIORITY_DEFAULT);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(MainActivity.PERSISTENT_NOTIFICATION_ID, notification);
        }
    }

    /**
     * Build notification with a given priority
     *
     * @param priority priority from Notification.priority
     * @return a Notification object
     */
    private static Notification buildNotification(Context context, int priority) {
        String title = context.getString(R.string.app_name);
        String text = context.getString(R.string.touch_to_launch);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return new NotificationCompat.Builder(context)
                .setContentTitle(title)                     // Title
                .setContentText(text)                       // Text
                .setSmallIcon(R.drawable.ic_notification)   // Icon
                .setContentIntent(pIntent)                  // Intent to launch this app
                .setWhen(0)                                 // No time to display
                .setPriority(priority)                      // Given priority
                .build();
    }
}