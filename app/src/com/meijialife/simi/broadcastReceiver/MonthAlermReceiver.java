package com.meijialife.simi.broadcastReceiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardAlertActivity;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.AlertCardData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.CalendarUtils;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api大于等于19
                    if (0 != interval_time) {
                        AlermUtils.setAlarmTime(context, date.getTime(), interval_time, intent);
                    }
                }
                //其他周期执行的弹屏操作
                Intent intent2 = new Intent(context, CardAlertActivity.class);
                intent2.putExtra("card_id", card_id);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
                AlermUtils.playAudio(context);

                getAlarm(context,card_id);
            }
        }
    }



    private void getAlarm(final Context context,String cardId) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        final String action = "alarm";
        User user = DBHelper.getUser(context);
        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id",cardId );
        map.put("user_id", "" + user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_GET_LAST_ALARM, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {

                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = context.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = context.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = context.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = context.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = context.getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(context, errorMsg);
                }
            }
        });
    }

}