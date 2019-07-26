package org.ktachibana.cloudemoji.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.activities.MainActivity;

/**
 * Abstract Notification codes from MainActivity to be shared among MainActivity and BootUpDummyActivity
 */
public class NotificationUtils {

    /**
     * Setup notification with user preference
     */
    public static void setupNotificationWithPref(Context context, String notificationVisibility) {
        // Cancel current notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.PERSISTENT_NOTIFICATION_ID);

        // If not showing
        if (notificationVisibility.equals("no")) {
            notificationManager.cancel(Constants.PERSISTENT_NOTIFICATION_ID);
        }

        // If only shows in panel
        else if (notificationVisibility.equals("panel")) {
            createQuickTriggerNotificationChannel(context, Notification.PRIORITY_MIN);
            Notification notification = buildNotification(context, Notification.PRIORITY_MIN);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(Constants.PERSISTENT_NOTIFICATION_ID, notification);
        }

        // If shows on both panel and status bar
        else if (notificationVisibility.equals("both")) {
            createQuickTriggerNotificationChannel(context, Notification.PRIORITY_DEFAULT);
            Notification notification = buildNotification(context, Notification.PRIORITY_DEFAULT);
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(Constants.PERSISTENT_NOTIFICATION_ID, notification);
        }
    }

    private static void createQuickTriggerNotificationChannel(Context context, int priority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(
                    Constants.QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.quick_trigger_notification_channel_name),
                    priority
            );
            channel.setDescription(context.getString(R.string.quick_trigger_notification_channel_description));
            notificationManager.createNotificationChannel(channel);
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
        return new NotificationCompat.Builder(context, Constants.QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)                     // Title
                .setContentText(text)                       // Text
                .setSmallIcon(R.drawable.ic_notification)   // Icon
                .setContentIntent(pIntent)                  // Intent to launch this app
                .setWhen(0)                                 // No time to display
                .setPriority(priority)                      // Given priority
                .build();
    }
}