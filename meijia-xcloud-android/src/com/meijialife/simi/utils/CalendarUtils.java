package com.meijialife.simi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 日历工具类，封装一些日期和时间的方法
 * 
 * @author baojiarui
 *
 */
public class CalendarUtils {

    /**
     * 获取某一天的日期：yyyy-MM-dd
     * 
     * @param which
     *            哪一天，默认0今天，1：明天（整数往后推,负数往前移动）
     * @return
     */
    public static String getDateStr_(int which) {
        String dateString = "";

        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, which);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        dateString = formatter.format(date);

        return dateString;
    }

    /**
     * 获取当前时间 格式：yyyy-MM-ddHH:mm
     * 
     * @return
     */
    public static String getDateStrYMDHM() {
        String dateString = "";

        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm");
        dateString = formatter.format(date);

        return dateString;
    }

    /**
     * 获取当前年份 格式：YYYY
     * 
     * @return
     */
    public static int getCurrentYear() {
        String dateString = "";

        Date date = new Date();// 取时间
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(date);
        // date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        dateString = formatter.format(date);

        return Integer.parseInt(dateString);
    }

    /**
     * 获取当前月份 格式：MM
     * 
     * @return
     */
    public static int getCurrentMonth() {
        String dateString = "";

        Date date = new Date();// 取时间
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(date);
        // date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        dateString = formatter.format(date);

        return Integer.parseInt(dateString);
    }

    public static String getWeek() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "星期日";
        } else if ("2".equals(mWay)) {
            mWay = "星期一";
        } else if ("3".equals(mWay)) {
            mWay = "星期二";
        } else if ("4".equals(mWay)) {
            mWay = "星期三";
        } else if ("5".equals(mWay)) {
            mWay = "星期四";
        } else if ("6".equals(mWay)) {
            mWay = "星期五";
        } else if ("7".equals(mWay)) {
            mWay = "星期六";
        }
        return mWay;
    }
    
    public static String getWeeks() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "周日";
        } else if ("2".equals(mWay)) {
            mWay = "周一";
        } else if ("3".equals(mWay)) {
            mWay = "周二";
        } else if ("4".equals(mWay)) {
            mWay = "周三";
        } else if ("5".equals(mWay)) {
            mWay = "周四";
        } else if ("6".equals(mWay)) {
            mWay = "周五";
        } else if ("7".equals(mWay)) {
            mWay = "周六";
        }
        return mWay;
    }

    public static String getDay() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "星期日";
        } else if ("2".equals(mWay)) {
            mWay = "星期一";
        } else if ("3".equals(mWay)) {
            mWay = "星期二";
        } else if ("4".equals(mWay)) {
            mWay = "星期三";
        } else if ("5".equals(mWay)) {
            mWay = "星期四";
        } else if ("6".equals(mWay)) {
            mWay = "星期五";
        } else if ("7".equals(mWay)) {
            mWay = "星期六";
        }
        return mYear + "-" + mMonth + "-" + mDay;
    }
    
    /**
     * 判断当前日期是否为工作日
     * @return
     */
    public static boolean isWeekdayByToday() {
        boolean flag = true;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        if(day==1 || day==7){
            flag = false;
        }else {
            flag = true;
        }
        return flag;
       }

    /**
     * 根据日期返回周
     * @param date
     * @return
     */
    public static int dayForWeek(String date) throws Exception{
        //1==周日，7==周六
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(date));
        return c.get(Calendar.DAY_OF_WEEK);
       }

    /**
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getWeekStr(String date) throws Exception{
        //1==周日，7==周六
        String week ="周一";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(date));
        int mWay = c.get(Calendar.DAY_OF_WEEK);
        if (1==mWay) {
            week = "周日";
        } else if (2==mWay) {
            week = "周一";
        } else if (3.==mWay) {
            week = "周二";
        } else if (4==mWay) {
            week = "周三";
        } else if (5==mWay) {
            week = "周四";
        } else if (6==mWay) {
            week = "周五";
        } else if (7==mWay) {
            week = "周六";
        }
        return week;
       }

}
