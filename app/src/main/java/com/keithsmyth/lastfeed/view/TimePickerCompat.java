package com.keithsmyth.lastfeed.view;

import android.os.Build;
import android.widget.TimePicker;

public class TimePickerCompat {

    public static int getHour(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT < 23) {
            // noinspection deprecation
            return timePicker.getCurrentHour();
        } else {
            return timePicker.getHour();
        }
    }

    public static int getMinute(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT < 23) {
            // noinspection deprecation
            return timePicker.getCurrentMinute();
        } else {
            return timePicker.getMinute();
        }
    }

    public static void setHour(TimePicker timePicker, int hour) {
        if (Build.VERSION.SDK_INT < 23) {
            // noinspection deprecation
            timePicker.setCurrentHour(hour);
        } else {
            timePicker.setHour(hour);
        }
    }

    public static void setMinute(TimePicker timePicker, int minute) {
        if (Build.VERSION.SDK_INT < 23) {
            // noinspection deprecation
            timePicker.setCurrentMinute(minute);
        } else {
            timePicker.setMinute(minute);
        }
    }
}
