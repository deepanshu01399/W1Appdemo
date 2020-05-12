package com.deepanshu.whatsappdemo.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class NotificationChannelUtil {
    public static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    public static NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createnotificationChannel(Context context) {
        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String name = "dummy  Notification channel";
        String description = "channel for  notification"; // The user-visible description of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;//for high priority
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        notificationChannel.setDescription(description);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.setShowBadge(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);

    }

}
