package org.ktachibana.cloudemoji.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel current notification
        notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);

        // If not showing
        if (notificationVisibility.equals("no")) {
            notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
        }

        // If only shows in panel
        else if (notificationVisibility.equals("panel")) {
            createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_MIN);
            showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_MIN);
        }

        // If shows on both panel and status bar
        else if (notificationVisibility.equals("both")) {
            createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_DEFAULT);
            showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_DEFAULT);
        }
    }

    private static void createQuickTriggerNotificationChannel(
            Context context,
            NotificationManager notificationManager,
            int importance
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.quick_trigger_notification_channel_name),
                    importance
            );
            channel.setDescription(context.getString(R.string.quick_trigger_notification_channel_description));
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void showQuickTriggerNotification(
            Context context,
            NotificationManager notificationManager,
            int priority
    ) {
        String title = context.getString(R.string.app_name);
        String text = context.getString(R.string.touch_to_launch);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pIntent = PendingIntent.getActivity
                    (context, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pIntent = PendingIntent.getActivity
                    (context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        Notification notification = new NotificationCompat.Builder(context, Constants.QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)                     // Title
                .setContentText(text)                       // Text
                .setSmallIcon(R.drawable.ic_notification)   // Icon
                .setContentIntent(pIntent)                  // Intent to launch this app
                .setWhen(0)                                 // No time to display
                .setPriority(priority)                      // Given priority
                .setChannelId(Constants.QUICK_TRIGGER_NOTIFICATION_CHANNEL_ID)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(Constants.QUICK_TRIGGER_NOTIFICATION_ID, notification);
    }
}