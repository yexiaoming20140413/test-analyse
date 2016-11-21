package com.chouti.analyse.utils;

import java.util.Calendar;
import java.util.Date;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
public class TimeUtils {
    private static long ONE_DAY_TIME_MS=24*60*60*1000L;

    public static long getTodayStartTime(){
        Calendar cl = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date=calendar.getTime();
        return date.getTime();
    }

    public static long getTodayEndTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date=calendar.getTime();
        return date.getTime();
    }

    public static long getLeftTodayStartTime(int days){
        long todayStart = getTodayStartTime();
        return todayStart-ONE_DAY_TIME_MS*days;
    }

    public static long getLeftTodayEndTime(int days){
        long todayEnd = getTodayEndTime();
        return todayEnd-ONE_DAY_TIME_MS*days;
    }

    /**
     * 根据时间戳计算该时间距离当前几天
     * @param timeStamp
     * @return
     */
    public static int getDaysByTimeLeftToday(long timeStamp){
        long startTime = getTodayStartTime();
        if(timeStamp >= startTime){
            return 0;
        }
        long timeRange = startTime - timeStamp;
        int days = (int) (timeRange / ONE_DAY_TIME_MS);
        return days +1;
    }

    public static void main(String args[]){
        int days = getDaysByTimeLeftToday(1478577600000l);
        System.out.println("days:"+days);
    }


}
