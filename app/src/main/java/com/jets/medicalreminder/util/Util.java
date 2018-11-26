package com.jets.medicalreminder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.jets.medicalreminder.alarm.AlarmActivity;
import com.jets.medicalreminder.beans.Medicine;

public class Util {

    // Constructor
    private Util() {
    }


    public enum Interval {
        MONTHS(DateUtils.DAY_IN_MILLIS * 30), WEEKS(DateUtils.WEEK_IN_MILLIS), DAYS(DateUtils.DAY_IN_MILLIS),
        HOURS(DateUtils.HOUR_IN_MILLIS), MINUTES(DateUtils.MINUTE_IN_MILLIS);

        public final long milliSeconds;

        Interval(long milliSeconds) {
            this.milliSeconds = milliSeconds;
        }

        public String toString() {
            String origin = super.toString();
            return (origin.substring(0, 1) + origin.substring(1).toLowerCase(
                    Locale.getDefault()));
        }
    }


    // Set Alarm
    public static void setAlarm(Context context, Medicine medicine) {
        // If the start time is in the past, set the alarm to the nearest incoming time
        long interval = medicine.getInterval();
        long startDateTime = medicine.getStartDateTime();
        while (startDateTime <= System.currentTimeMillis()) {
            startDateTime += interval;
        }

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(Constants.EXTRA_MEDICINE_ID, medicine.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, medicine.getId(), alarmIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, startDateTime, interval, pendingIntent);
    }

    // Cancel Alarm
    public static void cancelAlarm(Context context, Medicine medicine) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, medicine.getId(), alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }


    // To Date String
    public static String toDateString(long time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(time);
    }

    // To Date
    public static long toMilliseconds(String dateString, String format) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(dateString, format));
        return calendar.getTimeInMillis();
    }

    public static Date toDate(String dateString, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.parse(dateString);
    }

}
