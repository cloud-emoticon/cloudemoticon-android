package org.ktachibana.cloudemoji.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.activities.MainActivity;

/**
 * Abstract Notification codes from MainActivity to be shared among MainActivity and BootUpReceiver
 */
public class NotificationUtils implements Constants {
    /**
     * Setup notification with user preference
     */
    public static void setupNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String notificationVisibility = preferences.getString(Constants.PREF_NOTIFICATION_VISIBILITY, Constants.QUICK_TRIGGER_NOTIFICATION_VISIBILITY_BOTH);
        switch (notificationVisibility) {
            case QUICK_TRIGGER_NOTIFICATION_VISIBILITY_NO:
                // If not showing
                notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
                break;

            case QUICK_TRIGGER_NOTIFICATION_VISIBILITY_PANEL:
                // If only shows in panel
                if (doesQuickTriggerNotificationExist(context)) {
                    break;
                }
                notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
                createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_MIN);
                showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_MIN);
                break;

            case QUICK_TRIGGER_NOTIFICATION_VISIBILITY_BOTH:
                // If shows on both panel and status bar
                if (doesQuickTriggerNotificationExist(context)) {
                    break;
                }
                notificationManager.cancel(Constants.QUICK_TRIGGER_NOTIFICATION_ID);
                createQuickTriggerNotificationChannel(context, notificationManager, NotificationManagerCompat.IMPORTANCE_DEFAULT);
                showQuickTriggerNotification(context, notificationManager, NotificationCompat.PRIORITY_DEFAULT);
                break;
        }
    }

    private static boolean doesQuickTriggerNotificationExist(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!SystemUtils.aboveMarshmallow23()) {
            return false;
        }

        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == Constants.QUICK_TRIGGER_NOTIFICATION_ID) {
                return true;
            }
        }
        return false;
    }

    private static void createQuickTriggerNotificationChannel(
            Context context,
            NotificationManager notificationManager,
            int importance
    ) {
        if (SystemUtils.aboveOreo26()) {
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
        if (SystemUtils.aboveS31()) {
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