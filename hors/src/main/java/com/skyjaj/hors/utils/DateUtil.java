package com.skyjaj.hors.utils;


import android.annotation.SuppressLint;

import com.skyjaj.hors.bean.CustomDate;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date;  

  
public class DateUtil {

    private final static  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private final static  SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    private final static  SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
    private final static  SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
    private final static  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final static  SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd HH:mm");
    private final static  SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月");
    private final static  SimpleDateFormat sdf4 = new SimpleDateFormat("MM-dd HH:mm");
    private final static  SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 获取上一天,String格式：yyyyMMdd
     * @param date
     * @return
     */
    public static Date getLastDate(Timestamp date) {
        //通过日历获取下一天日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * 获取年，如2016
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        return Integer.parseInt(sdfYear.format(date));
    }

    /**
     * 获取月1~12
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        return Integer.parseInt(sdfMonth.format(date));
    }

    /**
     * time 形如 yyyyMMdd HH:mm 返回的格式：yyyy-MM-dd HH:mm
     * @param time
     * @return
     */
    public static String string2TimeFormatOne(String time) {

        if (time == null || "".equals(time)) {
            return null;
        }
        try {
            Date date = sdf2.parse(time);
            return sdf1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }




    /**
     * time 形如  yyyy-MM-dd HH:mm 返回的格式：yyyyMMdd HH:mm
     * @param time
     * @return
     */
    public static String string2TimeFormatfour(String time) {

        if (time == null || "".equals(time)) {
            return null;
        }
        try {
            Date date = sdf1.parse(time);
            return sdf3.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * time 形如 yyyyMMdd HH:mm 返回的格式：yyyy年MM月
     * @param time
     * @return
     */
    public static String string2TimeFormatTwo(String time) {

        if (time == null || "".equals(time)) {
            return null;
        }
        try {
            Date date = sdf2.parse(time);
            return sdf3.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * time 形如 yyyyMMdd HH:mm 返回的格式：MM-dd HH:mm  截取显示月份和日期 以及时间
     * @param time
     * @return
     */
    public static String string2TimeFormatThree(String time) {

        if (time == null || "".equals(time)) {
            return null;
        }
        try {
            Date date = sdf2.parse(time);
            return sdf4.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取日 从1算起
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        return Integer.parseInt(sdfDay.format(date));
    }


    /**
     * 获取下一天,String格式：yyyyMMdd
     * @param date
     * @return
     */
    public static Date getNextDate(Timestamp date) {
        //通过日历获取下一天日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, +1);
        return cal.getTime();
    }

    //格式转换
    public static Date Timestamp2Date(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTime();
    }

    //形如 yyMMdd
    public static String Timestamp2String(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return sdf.format(cal.getTime());
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String Timestamp2StringFormat2(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return sdf5.format(cal.getTime());
    }

    //形如 MM
    public static int Timestamp2Month(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return Integer.parseInt(sdf.format(cal.getTime()));
    }


    public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五","周六" };  
  
    public static int getMonthDays(int year, int month) {  
        if (month > 12) {  
            month = 1;  
            year += 1;  
        } else if (month < 1) {  
            month = 12;  
            year -= 1;  
        }

        int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };  
        int days = 0;
        // 闰年2月29天
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {  
            arr[1] = 29;
        }  
  
        try {  
            days = arr[month - 1];  
        } catch (Exception e) {  
            e.getStackTrace();  
        }  
        return days;
    }  
      
    public static int getYear() {  
        return Calendar.getInstance().get(Calendar.YEAR);  
    }  
  
    public static int getMonth() {  
        return Calendar.getInstance().get(Calendar.MONTH) + 1;  
    }  
  
    public static int getCurrentMonthDay() {  
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);  
    }  
  
    public static int getWeekDay() {  
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);  
    }  
  
    public static int getHour() {  
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);  
    }  
    public static int getMinute() {  
        return Calendar.getInstance().get(Calendar.MINUTE);  
    }  
    public static CustomDate getNextSunday() {
          
        Calendar c = Calendar.getInstance();  
        c.add(Calendar.DATE, 7 - getWeekDay()+1);  
        CustomDate date = new CustomDate(c.get(Calendar.YEAR),  
                c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));  
        return date;  
    }  
  
    public static int[] getWeekSunday(int year, int month, int day, int pervious) {  
        int[] time = new int[3];  
        Calendar c = Calendar.getInstance();  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month);  
        c.set(Calendar.DAY_OF_MONTH, day);  
        c.add(Calendar.DAY_OF_MONTH, pervious);  
        time[0] = c.get(Calendar.YEAR);  
        time[1] = c.get(Calendar.MONTH )+1;  
        time[2] = c.get(Calendar.DAY_OF_MONTH);  
        return time;  
  
    }  
  
    public static int getWeekDayFromDate(int year, int month) {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(getDateFromString(year, month));  
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;  
        if (week_index < 0) {  
            week_index = 0;  
        }  
        return week_index;  
    }  
  
    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {  
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))  
                + "-01";  
        Date date = null;  
        try {  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            date = sdf.parse(dateString);  
        } catch (ParseException e) {  
            System.out.println(e.getMessage());  
        }  
        return date;  
    }  
    public static boolean isToday(CustomDate date){  
        return(date.year == DateUtil.getYear() &&  
                date.month == DateUtil.getMonth()   
                && date.day == DateUtil.getCurrentMonthDay());  
    }  
      
    public static boolean isCurrentMonth(CustomDate date){  
        return(date.year == DateUtil.getYear() &&  
                date.month == DateUtil.getMonth());  
    }  
}  