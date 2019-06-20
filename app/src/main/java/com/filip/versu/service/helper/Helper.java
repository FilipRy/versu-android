package com.filip.versu.service.helper;


import android.content.Context;

import com.filip.versu.R;

import java.util.Calendar;

public class Helper {


    public static final int APP_VERSION = 29;

    public static final String CACHE_LAST_TIME_REFRESH_KEY = "last_time_refresh";

    public static final float POSSIBILITIES_OVERLAY_DISPLAYED_ALPHA = 0.68f;
    public static final float POSSIBLITIES_OVERLAY_HIDDEN_ALPHA = 0.05f;

    /**
     * Converts the calendarIndex into concrete name of the month.
     * @param calendarIndex
     * @param context
     * @return
     */
    public static String convertToMonthName(int calendarIndex, Context context) {
        if(calendarIndex == Calendar.JANUARY) {
            return context.getString(R.string.Jan);
        }
        if(calendarIndex == Calendar.FEBRUARY) {
            return context.getString(R.string.Feb);
        }
        if(calendarIndex == Calendar.MARCH) {
            return context.getString(R.string.Mar);
        }
        if(calendarIndex == Calendar.APRIL) {
            return context.getString(R.string.Apr);
        }
        if(calendarIndex == Calendar.MAY) {
            return context.getString(R.string.May);
        }
        if(calendarIndex == Calendar.JUNE) {
            return context.getString(R.string.Jun);
        }
        if(calendarIndex == Calendar.JULY) {
            return context.getString(R.string.Jul);
        }
        if(calendarIndex == Calendar.AUGUST) {
            return context.getString(R.string.Aug);
        }
        if(calendarIndex == Calendar.SEPTEMBER) {
            return context.getString(R.string.Sep);
        }
        if(calendarIndex == Calendar.OCTOBER) {
            return context.getString(R.string.Oct);
        }
        if(calendarIndex == Calendar.NOVEMBER) {
            return context.getString(R.string.Nov);
        }
        if(calendarIndex == Calendar.DECEMBER) {
            return context.getString(R.string.Dec);
        }
        return "";
    }

}
