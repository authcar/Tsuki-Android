package com.example.tsuki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/**
 * Menjadwalkan notifikasi reminder period menggunakan AlarmManager.
 *
 * Notifikasi yang dijadwalkan:
 *  - 3 hari sebelum next period
 *  - 1 hari sebelum next period
 *  - Hari H next period
 *  - Saat fertile window dimulai
 */
public class ReminderScheduler {

    // ID unik per notifikasi
    private static final int NOTIF_3_DAYS  = 101;
    private static final int NOTIF_1_DAY   = 102;
    private static final int NOTIF_TODAY   = 103;
    private static final int NOTIF_FERTILE = 104;

    /**
     * Schedule semua reminder berdasarkan data siklus.
     *
     * @param context       Context
     * @param periodStartDay   Hari mulai period terakhir
     * @param periodStartMonth Bulan mulai period terakhir (0-based)
     * @param periodStartYear  Tahun mulai period terakhir
     * @param cycleLength   Panjang siklus dalam hari
     */
    public static void scheduleReminders(Context context,
                                         int periodStartDay,
                                         int periodStartMonth,
                                         int periodStartYear,
                                         int cycleLength) {
        // Hitung tanggal next period
        Calendar nextPeriod = Calendar.getInstance();
        nextPeriod.set(periodStartYear, periodStartMonth, periodStartDay, 8, 0, 0);
        nextPeriod.set(Calendar.MILLISECOND, 0);
        nextPeriod.add(Calendar.DAY_OF_YEAR, cycleLength);

        // Hitung tanggal fertile window (ovulation - 5)
        Calendar ovulation = Calendar.getInstance();
        ovulation.set(periodStartYear, periodStartMonth, periodStartDay, 8, 0, 0);
        ovulation.set(Calendar.MILLISECOND, 0);
        ovulation.add(Calendar.DAY_OF_YEAR, cycleLength - 14);

        Calendar fertileStart = (Calendar) ovulation.clone();
        fertileStart.add(Calendar.DAY_OF_YEAR, -5);

        // Cancel semua reminder lama dulu
        cancelAllReminders(context);

        // Schedule reminder baru (hanya kalau waktunya di masa depan)
        long now = System.currentTimeMillis();

        // 3 hari sebelum period
        Calendar minus3 = (Calendar) nextPeriod.clone();
        minus3.add(Calendar.DAY_OF_YEAR, -3);
        if (minus3.getTimeInMillis() > now) {
            scheduleAlarm(context, NOTIF_3_DAYS, minus3.getTimeInMillis(),
                    "Period Reminder 🌸",
                    "Your period is expected in 3 days. Stock up on supplies!");
        }

        // 1 hari sebelum period
        Calendar minus1 = (Calendar) nextPeriod.clone();
        minus1.add(Calendar.DAY_OF_YEAR, -1);
        if (minus1.getTimeInMillis() > now) {
            scheduleAlarm(context, NOTIF_1_DAY, minus1.getTimeInMillis(),
                    "Period Tomorrow 🌸",
                    "Your period starts tomorrow. Be prepared!");
        }

        // Hari H period
        if (nextPeriod.getTimeInMillis() > now) {
            scheduleAlarm(context, NOTIF_TODAY, nextPeriod.getTimeInMillis(),
                    "Period Day 🌸",
                    "Your period may start today. Take care of yourself!");
        }

        // Fertile window
        if (fertileStart.getTimeInMillis() > now) {
            scheduleAlarm(context, NOTIF_FERTILE, fertileStart.getTimeInMillis(),
                    "Fertile Window 🌿",
                    "Your fertile window starts today. Track your cycle in Tsuki!");
        }
    }

    public static void cancelAllReminders(Context context) {
        int[] ids = {NOTIF_3_DAYS, NOTIF_1_DAY, NOTIF_TODAY, NOTIF_FERTILE};
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int id : ids) {
            Intent intent = new Intent(context, PeriodReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context, id, intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (pi != null && alarmManager != null) {
                alarmManager.cancel(pi);
                pi.cancel();
            }
        }
    }

    private static void scheduleAlarm(Context context, int notifId,
                                      long triggerAtMillis,
                                      String title, String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, PeriodReminderReceiver.class);
        intent.putExtra(PeriodReminderReceiver.EXTRA_NOTIF_ID, notifId);
        intent.putExtra(PeriodReminderReceiver.EXTRA_TITLE,    title);
        intent.putExtra(PeriodReminderReceiver.EXTRA_MESSAGE,  message);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, notifId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Gunakan setExactAndAllowWhileIdle agar notifikasi tepat waktu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }
}
