package com.example.tsuki;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PeriodReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTIF_ID = "notif_id";
    public static final String EXTRA_TITLE    = "title";
    public static final String EXTRA_MESSAGE  = "message";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.createNotificationChannel(context);

        int    notifId = intent.getIntExtra(EXTRA_NOTIF_ID, 1);
        String title   = intent.getStringExtra(EXTRA_TITLE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        if (title == null)   title   = "Tsuki Reminder";
        if (message == null) message = "Check your cycle today.";

        NotificationHelper.sendNotification(context, notifId, title, message);
    }
}
