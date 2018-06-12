package com.meijialife.simi.alerm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.meijialife.simi.Constants;
import com.meijialife.simi.bean.AlertCardData;
import com.meijialife.simi.broadcastReceiver.DayAlermReceiver;
import com.meijialife.simi.broadcastReceiver.MonthAlermReceiver;
import com.meijialife.simi.broadcastReceiver.WeekAlermReceiver;
import com.meijialife.simi.broadcastReceiver.WeekdayAlermReceiver;
import com.meijialife.simi.broadcastReceiver.YearAlermReceiver;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.SpFileUtil;

/**
 * 提醒工具类
 *
 */
@SuppressLint("NewApi")
public class AlermUtils {

    public static final String TAG = "Alerm";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
    private static SimpleDateFormat formatDay = new SimpleDateFormat("dd");
    private static SimpleDateFormat formatHours = new SimpleDateFormat("HH");
    private static SimpleDateFormat formatMinutes = new SimpleDateFormat("mm");
    private static SimpleDateFormat formatSeconds = new SimpleDateFormat("ss");
    private static SimpleDateFormat formatCode = new SimpleDateFormat("MMddHHmm");

    /**
     * 初始化本地提醒闹钟
     * 
     * @param context
     * @param remindAlerm
     *            提醒设置 0 = 不提醒 1 = 按时提醒 2 = 5分钟 3 = 15分钟 4 = 提前30分钟 5 = 提前一个小时 6 = 提前2小时 7 = 提前6小时 8 = 提前一天 9 = 提前两天
     * @param date
     *            时间 yyyy-MM-dd HH:mm:ss
     * @param title
     *            提醒类型 差旅规划 or 会议安排 or 事务提醒。。。。。等等等
     * 
     */
    public static void initAlerm(Context context, int remindAlerm, Date date, String title, String msg) {
        if (date == null) {
            return;
        }
        // Date date = new Date(lDate.getYear()-1900, lDate.getMonthOfYear()-1, lDate.getDayOfMonth(), lTime.getHourOfDay(), lTime.getMinuteOfHour(),
        // lTime.getSecondOfMinute());

        LogOut.i("===========",
                "任务时间" + formatYear.format(date) + "-" + formatMonth.format(date) + "-" + formatDay.format(date) + " " + formatHours.format(date)
                        + ":" + formatMinutes.format(date) + ":" + formatSeconds.format(date));

        switch (remindAlerm) {
        case 0:// 不提醒
            LogOut.i(TAG, "不提醒");
            break;
        case 1:// 按时提醒
            setAlerm(context, date, title, msg);
            break;
        case 2:// 5分钟
            setAlerm(context, DateUtils.getDate5(date), title, msg);
            break;
        case 3:// 15分钟
            setAlerm(context, DateUtils.getDate15(date), title, msg);
            break;
        case 4:// 提前30分钟
            setAlerm(context, DateUtils.getDate30(date), title, msg);
            break;
        case 5:// 提前一个小时
            setAlerm(context, DateUtils.getDate1(date), title, msg);
            break;
        case 6:// 提前2小时
            setAlerm(context, DateUtils.getDate2(date), title, msg);
            break;
        case 7:// 提前6小时
            setAlerm(context, DateUtils.getDate6(date), title, msg);
            break;
        case 8:// 提前一天
            setAlerm(context, DateUtils.getDate1d(date), title, msg);
            break;
        case 9:// 提前两天
            setAlerm(context, DateUtils.getDate2d(date), title, msg);
            break;

        default:
            break;
        }

    }

    public static void initAlerm(Context context, int remindAlerm, Date date, String title, String msg, String card_id) {
        if (date == null) {
            return;
        }
        // Date date = new Date(lDate.getYear()-1900, lDate.getMonthOfYear()-1, lDate.getDayOfMonth(), lTime.getHourOfDay(), lTime.getMinuteOfHour(),
        // lTime.getSecondOfMinute());

        LogOut.i("===========",
                "任务时间" + formatYear.format(date) + "-" + formatMonth.format(date) + "-" + formatDay.format(date) + " " + formatHours.format(date)
                        + ":" + formatMinutes.format(date) + ":" + formatSeconds.format(date));

        switch (remindAlerm) {
        case 0:// 不提醒
            LogOut.i(TAG, "不提醒");
            break;
        case 1:// 按时提醒
            setAlerm(context, date, title, msg, card_id);
            break;
        case 2:// 5分钟
            setAlerm(context, DateUtils.getDate5(date), title, msg, card_id);
            break;
        case 3:// 15分钟
            setAlerm(context, DateUtils.getDate15(date), title, msg, card_id);
            break;
        case 4:// 提前30分钟
            setAlerm(context, DateUtils.getDate30(date), title, msg, card_id);
            break;
        case 5:// 提前一个小时
            setAlerm(context, DateUtils.getDate1(date), title, msg, card_id);
            break;
        case 6:// 提前2小时
            setAlerm(context, DateUtils.getDate2(date), title, msg, card_id);
            break;
        case 7:// 提前6小时
            setAlerm(context, DateUtils.getDate6(date), title, msg, card_id);
            break;
        case 8:// 提前一天
            setAlerm(context, DateUtils.getDate1d(date), title, msg, card_id);
            break;
        case 9:// 提前两天
            setAlerm(context, DateUtils.getDate2d(date), title, msg, card_id);
            break;

        default:
            break;
        }
    }

    // 周期提醒
    public static void initAlerm(Context context, int period, int remindAlerm, Date date, String title, String msg, String card_id) {
        if (date == null) {
            return;
        }

        switch (remindAlerm) {
        case 0:// 不提醒
            LogOut.i(TAG, "不提醒");
            break;
        case 1:// 按时提醒
            if (period == 0) {
                setAlerm(context, date, title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, date, title, msg, card_id);
            }
            break;
        case 2:// 5分钟
            if (period == 0) {
                setAlerm(context, DateUtils.getDate5(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate5(date), title, msg, card_id);
            }
            break;
        case 3:// 15分钟
            if (period == 0) {
                setAlerm(context, DateUtils.getDate15(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate15(date), title, msg, card_id);
            }
            break;
        case 4:// 提前30分钟
            if (period == 0) {
                setAlerm(context, DateUtils.getDate30(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate30(date), title, msg, card_id);
            }
            break;
        case 5:// 提前一个小时
            if (period == 0) {
                setAlerm(context, DateUtils.getDate1(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate1(date), title, msg, card_id);
            }
            break;
        case 6:// 提前2小时
            if (period == 0) {
                setAlerm(context, DateUtils.getDate2(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate2(date), title, msg, card_id);
            }
            break;
        case 7:// 提前6小时
            if (period == 0) {
                setAlerm(context, DateUtils.getDate6(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate6(date), title, msg, card_id);
            }
            break;
        case 8:// 提前一天
            if (period == 0) {
                setAlerm(context, DateUtils.getDate1d(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate1d(date), title, msg, card_id);
            }
            break;
        case 9:// 提前两天
            if (period == 0) {
                setAlerm(context, DateUtils.getDate2d(date), title, msg, card_id);
            } else {// 设置周期闹钟
                setAlerm(context, period, DateUtils.getDate2d(date), title, msg, card_id);
            }
            break;

        default:
            break;
        }
    }

    /**
     * 初始化本地提醒闹钟
     * 
     * @param context
     * @param date
     *            时间 yyyy-MM-dd HH:mm:ss
     * @param title
     *            提醒类型 差旅规划 or 会议安排 or 事务提醒。。。。。等等等
     * 
     */
    public static void initAlerm(Context context, Date date, String title, String msg) {
        if (date == null) {
            return;
        }
        LogOut.i("===========",
                "任务时间" + formatYear.format(date) + "-" + formatMonth.format(date) + "-" + formatDay.format(date) + " " + formatHours.format(date)
                        + ":" + formatMinutes.format(date) + ":" + formatSeconds.format(date));

        setAlerm(context, date, title, msg);
    }

    /**
     * 设置提醒闹钟
     */
    private static void setAlerm(Context context, Date date, String title, String text) {
        if (date == null) {
            return;
        }

        Calendar c = Calendar.getInstance(); // 提醒时间
        // c.set(Calendar.YEAR, 2015); //年
        // c.set(Calendar.MONTH, 9); //月（一月为0）
        // c.set(Calendar.DAY_OF_MONTH, 18); //日
        // c.set(Calendar.HOUR_OF_DAY, 22); //时
        // c.set(Calendar.MINUTE, 54); //分
        // c.set(Calendar.SECOND, 10); //秒
        // c.set(Calendar.MILLISECOND, 0);

        c.set(Calendar.YEAR, Integer.parseInt(formatYear.format(date))); // 年
        c.set(Calendar.MONTH, (Integer.parseInt(formatMonth.format(date)) - 1)); // 月（一月为0）
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(formatDay.format(date))); // 日
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(formatHours.format(date))); // 时
        c.set(Calendar.MINUTE, Integer.parseInt(formatMinutes.format(date))); // 分
        c.set(Calendar.SECOND, 0); // 秒
        c.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, AlermReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        int requestCode = Integer.parseInt(formatCode.format(date));// 为每个闹钟单独设置requestCode，防止被覆盖
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am;

        // 获取系统进程
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private static void setAlerm(Context context, Date date, String title, String text, String card_id) {
        if (date == null) {
            return;
        }

        Calendar c = Calendar.getInstance(); // 提醒时间
        c.set(Calendar.YEAR, Integer.parseInt(formatYear.format(date))); // 年
        c.set(Calendar.MONTH, (Integer.parseInt(formatMonth.format(date)) - 1)); // 月（一月为0）
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(formatDay.format(date))); // 日
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(formatHours.format(date))); // 时
        c.set(Calendar.MINUTE, Integer.parseInt(formatMinutes.format(date))); // 分
        c.set(Calendar.SECOND, 0); // 秒
        c.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, AlermReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("date", date);
        intent.putExtra("card_id", card_id);

        int requestCode = Integer.parseInt(formatCode.format(date));// 为每个闹钟单独设置requestCode，防止被覆盖
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.valueOf(card_id).intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am;

        // 获取系统服务---闹钟（全局定时器）
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    // 周期性的提醒
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setAlerm(Context context, int period, Date date, String title, String text, String card_id) {
        if (date == null) {
            return;
        }

        Calendar c = Calendar.getInstance(); // 提醒时间
        c.set(Calendar.YEAR, Integer.parseInt(formatYear.format(date))); // 年
        c.set(Calendar.MONTH, (Integer.parseInt(formatMonth.format(date)) - 1)); // 月（一月为0）
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(formatDay.format(date))); // 日
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(formatHours.format(date))); // 时
        c.set(Calendar.MINUTE, Integer.parseInt(formatMinutes.format(date))); // 分
        c.set(Calendar.SECOND, 0); // 秒
        c.set(Calendar.MILLISECOND, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent();
        Log.i("tag", "周期执行类型---- "+period);
        if(period==1){
            intent.setClass(context,DayAlermReceiver.class);
        }else if(period==2){
            intent.setClass(context,WeekdayAlermReceiver.class);
        }else if(period==3){
            intent.setClass(context,WeekAlermReceiver.class);
        }else if(period==4){
            intent.setClass(context,MonthAlermReceiver.class);
        }else if(period==5){
            intent.setClass(context,YearAlermReceiver.class);
        }

        int requestCode = Integer.parseInt(card_id);// 为每个闹钟单独设置requestCode，防止被覆盖
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 获取系统服务---闹钟（全局定时器）

        AlertCardData acd = new AlertCardData();
        acd.setAlert_id(card_id);
        acd.setCycle_type_id(period + "");
        acd.setInteval_time(getIntevalTime(period) + "");
        acd.setService_time(date.getTime() + "");
        AssetsDatabaseManager.initManager(context); // 初始化，只需要调用一次
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();// 获取管理对象，因为数据库需要通过管理对象才能够获取
        SQLiteDatabase db = mg.getDatabase("simi01.db"); // 通过管理对象获取数据库
        AssetsDatabaseManager.insertAlertCard(db, acd);
      
        switch (period) 
        {
        case 1:// 每天定时执行
               // api>19进行的闹钟设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 3, pendingIntent);

            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(1), pendingIntent);
            }
            break;
        case 2:// 每个工作日
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            break;
        case 3:// 每周
               // api>19进行的闹钟设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(3), pendingIntent);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(3), pendingIntent);
            }
            break;
        case 4:// 每月
               // api>19进行的闹钟设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(4), pendingIntent);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(4), pendingIntent);
            }
            break;
        case 5:// 每年
               // api>19进行的闹钟设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(5), pendingIntent);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), getIntevalTime(5), pendingIntent);
            }
            break;

        default:
            break;
        }

    }

    public static long getIntevalTime(int cycleTypId) {
        long intevalTime = 0L;
        switch (cycleTypId) {
        case 1:// 每天周期执行
            intevalTime = AlarmManager.INTERVAL_DAY;
            break;
        case 2:// 每个工作日周期执行
            intevalTime = AlarmManager.INTERVAL_DAY;// 工作日按照每天周期执行来判断
            break;
        case 3:// 每周
            intevalTime = AlarmManager.INTERVAL_DAY * 7;
            break;
        case 4:// 每月
            intevalTime = AlarmManager.INTERVAL_DAY * 30;
            break;
        case 5:// 每年
            intevalTime = AlarmManager.INTERVAL_DAY * 365;
            break;
        default:
            break;
        }
        return intevalTime;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setAlarmTime(Context context, Long time, long timeInMillis, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, intent.getIntExtra("id", 0), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.RTC_WAKEUP, time, timeInMillis, sender);
        }
    }

    /**
     * 取消提醒闹钟
     */
    public static void cancelSigninAlerm(Context context) {

        Intent intent = new Intent(context, AlermReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am;
        // 获取系统进程
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // cancel
        am.cancel(pendingIntent);

        // Log.e("提醒 已取消");
    }

    /**
     * 关闭对应的闹钟
     * 
     * @param context
     * @param requestCode
     */
    public static void cancelSigninAlerm(Context context, int requestCode) {

        Intent intent = new Intent(context, AlermReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am;
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // cancel
        am.cancel(pendingIntent);

    }

    /**
     * 播放通知
     */
    public static void playAudio(Context context) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void playAudios(Context context) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(context,notification);
        r.play();
    }


    public static void cancleAudios(Context context) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(context,notification);
        if(r.isPlaying()){
            r.stop();
        }
    }
}
