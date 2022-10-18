package org.ktachibana.cloudemoji.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.activities.MainActivity;

/**
 * Abstract Notification codes from MainActivity to be shared among MainActivity and BootUpReceiver
 */
public class NotificationUtils {

    /**
     * Setup notification with user preference
     */
    public static void setupNotificationWithPref(Context context, String notificationVisibility) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel current notification
        notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);

        switch (notificationVisibility) {
            case "no":
                // If not showing
                notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
                break;

            case "panel":
                // If only shows in panel
                createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_MIN);
                showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_MIN);
                break;

            case "both":
                // If shows on both panel and status bar
                createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_DEFAULT);
                showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_DEFAULT);
                break;
        }
    }

    private static void createQuickTriggerNotificationChannel(
            Context context,
            NotificationManager notificationManager,
            int importance
    ) {
        if (SystemUtils.aboveOreo()) {
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

    @SuppressLint("UnspecifiedImmutableFlag")
    private static void showQuickTriggerNotification(
            Context context,
            NotificationManager notificationManager,
            int priority
    ) {
        String title = context.getString(R.string.app_name);
        String text = context.getString(R.string.touch_to_launch);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent;
        if (SystemUtils.aboveS()) {
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