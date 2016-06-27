package com.meijialife.simi.broadcastReceiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meijialife.simi.activity.CardAlertActivity;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.AlertCardData;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.CalendarUtils;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 事务提醒周期提醒---每月提醒---提醒崩溃
 */
@SuppressLint({"NewApi", "UseValueOf"})
public class MonthAlermReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


        Date date = new Date();  //发生日期
        int cycleTypeId = 0;     //提醒类型
        long interval_time = 0L; //间隔时间
        String card_id = "0";     //卡片Id


        AssetsDatabaseManager.initManager(context);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("simi01.db");
        List<AlertCardData> alertList = AssetsDatabaseManager.searchAlertCardByPeriod(db, "4");

        for (Iterator iterator = alertList.iterator(); iterator.hasNext(); ) {
            AlertCardData alertCardData = (AlertCardData) iterator.next();

            Long serviceLong = DateUtils.getLongByPattern(Long.valueOf(alertCardData.getService_time()), "dd HH:mm").getTime();
            Long currentLong = DateUtils.getLongByPattern(System.currentTimeMillis(), "dd HH:mm").getTime();
            boolean flag = serviceLong / 1000 == currentLong / 1000;
            Log.d("tag", "每月提醒--- "+flag);
            Log.d("tag", "每月提醒CurrentStr--- " + currentLong);
            Log.d("tag", "每月工作日提醒serviceStr " + serviceLong);
            if (flag) {
                card_id = alertCardData.getAlert_id();
                interval_time = Long.valueOf(alertCardData.getInteval_time());
                date = DateUtils.getLongByPattern(Long.valueOf(alertCardData.getService_time()), "yyyy-MM-dd HH:mm");

                if (0 != interval_time) {
                    AlermUtils.setAlarmTime(context, date.getTime(), interval_time, intent);
                }
                //其他周期执行的弹屏操作
                Intent intent2 = new Intent(context, CardAlertActivity.class);
                intent2.putExtra("card_id", card_id);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
                AlermUtils.playAudio(context);
            }
        }
    }
}