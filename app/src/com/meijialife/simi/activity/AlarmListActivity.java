package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AlarmListAdapter;
import com.meijialife.simi.bean.AlarmData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.wheelview.ArrayWheelAdapter;
import com.meijialife.simi.ui.wheelview.WheelView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 常用提醒列表
 * 
 */
public class AlarmListActivity extends BaseActivity {

    private ListView listview;
    private AlarmListAdapter adapter;
    private List<AlarmData> alarmDatas;
    private User user;
    private String settingId;//提醒设置Id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_alarm_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("常用提醒设置");
        requestBackBtn();
        
        user = DBHelper.getUser(AlarmListActivity.this);
        listview = (ListView) findViewById(R.id.m_alarm_listView);
        view_mask = findViewById(R.id.view_mask);
        adapter = new AlarmListAdapter(AlarmListActivity.this);
        listview.setAdapter(adapter);
        getUserAlarmList();

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmData alarmData = alarmDatas.get(position);
                settingId = alarmData.getSetting_id();
                showRemindWindow() ;
            }
        });
    }

    
    public void showRemindWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_remind, null, false);
        TextView tvTitle = (TextView)v.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        InitTimeRemind(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);
 
        mTimePopup.showAtLocation(view_mask, Gravity.BOTTOM, 0, 0);
        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    private int remindAlerm = 1;// 提醒设置 0 = 不提醒 1 = 一天前 3 = 3天前 7=7天前
    private PopupWindow mTimePopup;
    private View view_mask;

    private View InitTimeRemind(View view) {

        remind = (WheelView) view.findViewById(R.id.remind);
        String[] items = new String[4];
        items[0] = "1天前";
        items[1] = "3天前";
        items[2] = "7天前";
        items[3] = "不提醒";
        /*items[4] = "1天前";
        items[5] = "3天前";
        items[6] = "7天前";
        items[7] = "不提醒";*/
        final String[] alarm_day = new String[4];
        alarm_day[0] = "1";
        alarm_day[1] = "3";
        alarm_day[2] = "7";
        alarm_day[3] = "0";
        /*alarm_day[4] = "1";
        alarm_day[5] = "3";
        alarm_day[6] = "7";
        alarm_day[7] = "0";*/


        arryadapter = new ArrayWheelAdapter<>(this, items);
        remind.setViewAdapter(arryadapter);
        remind.setCyclic(false);// 是否可循环滑动
        remind.setVisibleItems(items.length);// 设置显示行数
        remind.setCurrentItem(1);
        arryadapter.setTextColor(getResources().getColor(R.color.simi_color_black));
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = remind.getCurrentItem();
                remindAlerm = currentItem;
//                String itemText = (String) arryadapter.getItemText(currentItem);
//                tv_xiaoxi_content.setText(itemText);
                setUserAlarm(settingId, alarm_day[currentItem]);
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
                // Toast.makeText(MainPlusTrevelActivity.this, time, 1).show();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }

    /**
     * 获取常用提醒接口
     */
    public void getUserAlarmList() {
        if (!NetworkUtils.isNetworkConnected(AlarmListActivity.this)) {
            Toast.makeText(AlarmListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        param.put("user_id",user.getId());
        new FinalHttp().get(Constants.GET_UESR_ALARM, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(AlarmListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                Gson gson = new Gson();
                                alarmDatas = gson.fromJson(data, new TypeToken<ArrayList<AlarmData>>() {
                                }.getType());
                                adapter.setData(alarmDatas);
                            } 
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(AlarmListActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 设置常用提醒接口
     */
    public void setUserAlarm(String settingId,String alarmDay) {
        if (!NetworkUtils.isNetworkConnected(AlarmListActivity.this)) {
            Toast.makeText(AlarmListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        param.put("user_id",user.getId());
        param.put("setting_id",settingId);
        param.put("alarm_day",alarmDay);
        new FinalHttp().post(Constants.POST_SET_ALARM, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(AlarmListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            getUserAlarmList();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(AlarmListActivity.this, errorMsg);
                }
            }
        });
    }
}