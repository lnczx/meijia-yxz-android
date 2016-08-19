package com.meijialife.simi.fra;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AlarmListActivity;
import com.meijialife.simi.activity.Find2DetailActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.MainPlusAffairActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.activity.WebViewsFindActivity;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.UserMsgListAdapter;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.UserMsg;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CollapseCalendarView;
import com.meijialife.simi.ui.ImageCycleView;
import com.meijialife.simi.ui.ImageCycleView.ImageCycleViewListener;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.ui.TipPopWindow;
import com.meijialife.simi.ui.calendar.CalendarManager;
import com.meijialife.simi.ui.datepicker.bizs.calendars.DPCManager;
import com.meijialife.simi.ui.datepicker.bizs.decors.DPDecor;
import com.meijialife.simi.ui.datepicker.cons.DPMode;
import com.meijialife.simi.ui.datepicker.views.DatePicker;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日程
 * 
 * @author RUI
 * 
 */
public class ScheduleFra extends BaseFragment implements OnClickListener  {

    public String today_date;
    private CollapseCalendarView calendarView;// 日历View
    private CalendarManager calendarManager;// 日历管理
    public static ArrayList<CalendarMark> calendarMarks;// 日历当月所有有卡片的日期

    private ListView listview;
    private ListAdapter adapter;
    private ArrayList<Cards> cardlist;// 卡片数据
    private ArrayList<CardExtra> cardExtrasList;

    private TextView tv_tips;// 没有数据时的提示
    private ImageView iv_no_card;// 没有数据时提示 图片

    private int card_from; // 0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0

    private static View layout_mask, layout_guide;
    private final static int SCANNIN_GREQUEST_CODES = 5;
    private View v;
    private UserInfo userInfo;
    private ArrayList<FindBean> findBeanList;

    TextView tv_service_type_ids;// 服务大类集合

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    private String title_name = "发现";
    private boolean card_flag = false;
    private boolean ad_flag = false;

    /**
     * 广告轮播控件
     */
    private ImageCycleView mAdView;
    private Boolean is_log = false;
    private User user;

    /**
     * 用户消息
     */
    private List<UserMsg> userMsgs;
    private ArrayList<UserMsg> totalUserMsgList;
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private int page = 1;
    private UserMsgListAdapter userMsgAdapter;
    DatePicker picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fra_schedule_layout, null);
        init(v);
        getTotalByMonth(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear());

        initUserMsgView(v);

        initCalendar();
        return v;
    }

    @SuppressLint("ResourceAsColor")
    private void init(View v) {
        today_date = LocalDate.now().toString();
        user = DBHelper.getUser(getActivity());
        if (user != null) {
            getAppHelp();
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
        //@// TODO: 2016/8/8 andye
//        v.findViewById(R.id.btn_chakan).setOnClickListener(this);
//        v.findViewById(R.id.ibtn_person).setOnClickListener(this);
//        v.findViewById(R.id.btn_rili).setOnClickListener(this);
//        v.findViewById(R.id.btn_saoma).setOnClickListener(this);
//        v.findViewById(R.id.btn_alarm).setOnClickListener(this);
//        v.findViewById(R.id.user_plus).setOnClickListener(this);
//        tv_service_type_ids = (TextView) v.findViewById(R.id.tv_service_type_ids);

        picker = (DatePicker) v.findViewById(R.id.main_dp);

        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);

        layout_mask = v.findViewById(R.id.layout_mask);
        userInfo = DBHelper.getUserInfo(getActivity());
        is_log = SpFileUtil.getBoolean(getActivity().getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        // 请求帮助接口
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);

    }

    /**
     * 初始化日历
     * 
     */
    private void initCalendar() {


//        calendarManager = new CalendarManager(LocalDate.now(), CalendarManager.State.MONTH, LocalDate.now().minusYears(1), LocalDate.now()
//                .plusYears(1));
//        calendarView = (CollapseCalendarView) v.findViewById(R.id.layout_calendar);
//        calendarView.init(calendarManager, getActivity(), this);
//
//        calendarView.setListener(new OnDateSelect() {
//            @Override
//            public void onDateSelected(LocalDate date) {
//                today_date = date.toString();
//                // getAdListByChannelId("0");
//                if (findBeanList != null && findBeanList.size() > 0) {
//                    ad_flag = true;
//                }
//                userMsgs.clear();
//                totalUserMsgList.clear();
//                // getCardListData(today_date, card_from);
//                getUserMsgListData(today_date, page);
//                // 如果广告和卡片有一个有值，则不显示
//            }
//
//        });

        DBHelper instance = DBHelper.getInstance(getActivity());
        List<CalendarMark> calendarMarks = instance.searchAll(CalendarMark.class);
        //刷新日历
        DrawCalendarPoint(calendarMarks);

        picker.setDate(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear());
        picker.setFestivalDisplay(true);
        picker.setTodayDisplay(true);
        picker.setHolidayDisplay(true);
        picker.setDeferredDisplay(true);
        picker.setMode(DPMode.SINGLE);

        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                today_date = date.toString();
                // getAdListByChannelId("0");
                if (findBeanList != null && findBeanList.size() > 0) {
                    ad_flag = true;
                }
                userMsgs.clear();
                totalUserMsgList.clear();
                getUserMsgListData(today_date, page);

            }
        });

    }

    public static void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public static void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
        @Override
        public void onImageClick(FindBean info, int position, View view) {
            String goto_type = info.getGoto_type().trim();
            String goto_url = info.getGoto_url().trim();
            String service_type_ids = info.getService_type_ids().trim();
            if (goto_type.equals("h5")) {
                Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                intent.putExtra("url", goto_url);
                intent.putExtra("title_name", "");
                intent.putExtra("service_type_ids", "");
                startActivity(intent);
            } else if (goto_type.equals("app")) {
                Intent intent = new Intent(getActivity(), Find2DetailActivity.class);
                intent.putExtra("service_type_ids", service_type_ids);
                intent.putExtra("title_name", title_name);
                startActivity(intent);
            } else if (goto_type.equals("h5+list")) {
                Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                intent.putExtra("url", goto_url);
                intent.putExtra("title_name", title_name);
                intent.putExtra("service_type_ids", service_type_ids);
                startActivity(intent);
            }

        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            // ImageLoader.getInstance().displayImage(imageURL, imageView);// 使用ImageLoader对图片进行加装！
            finalBitmap.display(imageView, imageURL);
        }

    };



    private void initUserMsgView(View v) {
        totalUserMsgList = new ArrayList<UserMsg>();
        userMsgs = new ArrayList<UserMsg>();
        mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_msg_list);
        userMsgAdapter = new UserMsgListAdapter(getActivity());
        mPullRefreshListView.setAdapter(userMsgAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getUserMsgListData(today_date, page);
                userMsgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (userMsgs != null && userMsgs.size() >= 10) {
                    page = page + 1;
                    getUserMsgListData(today_date, page);
                    userMsgAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserMsg userMsg = totalUserMsgList.get(position);
                String category = userMsg.getCategory().trim();
                String goto_url = userMsg.getGoto_url().trim();
                String params = userMsg.getParams().trim();
                String action = userMsg.getAction().trim();

                RouteUtil routeUtil = new RouteUtil(getActivity());
                routeUtil.Routing(category, action, goto_url, params);
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    @Override
    public void onResume() {
        super.onResume();

        user = DBHelper.getUser(getActivity());
        if (user != null) {
            getUserMsgListData(today_date, page);
        }

         /*else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }*/
        // mAdView.startImageCycle();//广告轮播
        // getTotalByMonth();
        // getCardListData(today_date, card_from);
        // getAdListByChannelId("0");
        // getUserInfo();

        MobclickAgent.onPageStart("MainActivity");
    }

    
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");
    }

    @Override
    public void onStop() {
        super.onStop();
        // mAdView.pushImageCycle();
    }

    public void isShowDefaultCard(boolean card_flag, boolean ad_flag) {
        if (card_flag || ad_flag) {
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        } else {
            tv_tips.setVisibility(View.VISIBLE);
            iv_no_card.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获得用户消息列表接口
     *
     * @param date
     */
    public void getUserMsgListData(String date, int page) {

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            UIUtils.showToast(getActivity(), getString(R.string.net_not_open));
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", date);
        map.put("user_id", user.getId());
        map.put("page", "" + page);
        AjaxParams param = new AjaxParams(map);

        // showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_MSG_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                // dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                // dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                userMsgs = gson.fromJson(data, new TypeToken<ArrayList<UserMsg>>() {
                                }.getType());
                                showData(userMsgs);
                                // userMsgAdapter.setData(userMsgs);
                            } else {
                                showData(new ArrayList<UserMsg>());
                                // userMsgAdapter.setData(new ArrayList<UserMsg>());
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
                    if (isAdded()) {
                        errorMsg = getString(R.string.servers_error);
                    }
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
//            case R.id.btn_chakan: // 查看
//                if (card_from == 0) {
//                    card_from = 1;
//                } else {
//                    card_from = 0;
//                }
//                // getCardListData(today_date, card_from);
//                getUserMsgListData(today_date, page);
//                break;
//            case R.id.btn_rili: // 日历展开/收起
//                LocalDate selectedDay = calendarManager.getSelectedDay();
//                if (calendarManager.getState() == CalendarManager.State.MONTH) {
//                    calendarManager = new CalendarManager(selectedDay, CalendarManager.State.WEEK, LocalDate.now().minusYears(1), LocalDate.now()
//                            .plusYears(1));
//                } else {
//                    calendarManager = new CalendarManager(selectedDay, CalendarManager.State.MONTH, LocalDate.now().minusYears(1), LocalDate.now()
//                            .plusYears(1));
//                }
//                calendarView.init(calendarManager, getActivity(), this);
//                break;
//            case R.id.btn_saoma:
//                intent = new Intent();
//                intent.setClass(getActivity(), CaptureActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent, SCANNIN_GREQUEST_CODES);
//                break;
            case R.id.btn_alarm://常用提醒
                intent = new Intent();
                intent.setClass(getActivity(), AlarmListActivity.class);
                startActivity(intent);
                break;
            case R.id.user_plus://快速事务提醒
                intent = new Intent();
                intent.setClass(getActivity(), MainPlusAffairActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 处理数据加载的方法
     *
     */
    private void showData(List<UserMsg> userMsgs) {
        if (userMsgs != null && userMsgs.size() > 0) {
            if (page == 1) {
                totalUserMsgList.clear();
            }
            for (UserMsg userMsg : userMsgs) {
                totalUserMsgList.add(userMsg);
            }
            // 给适配器赋值
            // userMsgAdapter.setData(totalUserMsgList);
        }
        userMsgAdapter.setData(totalUserMsgList);
        mPullRefreshListView.onRefreshComplete();
    }

    private AppHelpData appHelpData;

    /*
     * 帮助接口
     */

    private void getAppHelp() {

        if (user != null) {

            if (!NetworkUtils.isNetworkConnected(getActivity())) {
                UIUtils.showToast(getActivity(), getString(R.string.net_not_open));
                return;
            }
            final String action = "index";
            Map<String, String> map = new HashMap<String, String>();
            map.put("action", "index");
            map.put("user_id", user.getId());
            AjaxParams param = new AjaxParams(map);
            showDialog();
            new FinalHttp().get(Constants.URL_GET_APP_HELP_DATA, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    dismissDialog();
                    Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String errorMsg = "";
                    dismissDialog();
                    try {
                        if (StringUtils.isNotEmpty(t.toString())) {
                            JSONObject obj = new JSONObject(t.toString());
                            int status = obj.getInt("status");
                            String msg = obj.getString("msg");
                            String data = obj.getString("data");
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                if (StringUtils.isNotEmpty(data)) {
                                    Gson gson = new Gson();
                                    appHelpData = gson.fromJson(data, AppHelpData.class);
                                    TipPopWindow addPopWindow = new TipPopWindow(getActivity(), appHelpData, action);
                                    addPopWindow.showPopupWindow(v);
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
                        UIUtils.showToast(getActivity(), errorMsg);
                    }
                }
            });
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }


    /**
     * 按月份查看卡片日期分布接口（更新日历圆点标签）
     *
     */
    public void getTotalByMonth(final  int year, final int month) {

        user = DBHelper.getUser(getActivity());
        if (user != null) {
            if (!NetworkUtils.isNetworkConnected(getActivity())) {
                UIUtils.showToast(getActivity(), getString(R.string.net_not_open));
                return;
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("year", year+"");
            map.put("month", month+"");
            AjaxParams param = new AjaxParams(map);

            new FinalHttp().get(Constants.URL_GET_TOTAL_BY_MONTH, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    // dismissDialog();
                    Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                LogOut.debug("===="+t.toString());
                                // 先清除这个月的旧数据
                                DBHelper.clearCalendarMark(getActivity(), year + "", month + "");

                                if (StringUtils.isNotEmpty(data)) {
                                    Gson gson = new Gson();
                                    calendarMarks = gson.fromJson(data, new TypeToken<ArrayList<CalendarMark>>() {
                                    }.getType());

                                    DBHelper db = DBHelper.getInstance(getActivity());
                                    for (int i = 0; i < calendarMarks.size(); i++) {
                                        db.add(calendarMarks.get(i), calendarMarks.get(i).getService_date());
                                    }
                                }

                                // 刷新日历UI
//                                calendarView.updateUI();

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
                        if (isAdded()) {
                            errorMsg = getString(R.string.servers_error);
                        }

                    }
                    // 操作失败，显示错误信息
                    if (!StringUtils.isEmpty(errorMsg.trim())) {
                        UIUtils.showToast(getActivity(), errorMsg);
                    }
                }
            });
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case SCANNIN_GREQUEST_CODES:
            if (resultCode == (-1)) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result").trim();
                if (!StringUtils.isEmpty(result) && result.contains(Constants.RQ_IN_APP)) {// 判断是否为云行政二维码
                    // http://www.bolohr.com/d/open.html?category=app&action=feed&params=&goto_url=

                    if (!StringUtils.isEmpty(result) && result.contains("category=app")) {
                        String category = "", action = "", params = "", goto_url = "";
                        if (result.contains("params") && result.contains("goto_url")) {// 两个参数都有
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            params = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());
                            goto_url = temp[3].substring(temp[3].lastIndexOf("=") + 1, temp[3].length());

                        } else if (result.contains("params") && !result.contains("goto_url")) {// 只有参数params
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            params = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());

                        } else if (result.contains("goto_url") && !result.contains("params")) {// 只有参数goto_url
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            goto_url = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());
                        } else {
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                        }
                        if (!StringUtils.isEmpty(result)) {
                            RouteUtil routeUtil = new RouteUtil(getActivity());
                            routeUtil.Routing(category, action, goto_url, params);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                        intent.putExtra("url", result);
                        startActivity(intent);
                    }
                } else {// 非内部app扫描，webView显示
                    Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                    intent.putExtra("url", result);
                    startActivity(intent);
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        page = 1;
        userMsgs = null;
        totalUserMsgList = null;
        // mAdView.pushImageCycle();
    }

    /**
     * 画点
     * @param calendarMarks
     */
    public void DrawCalendarPoint(List<CalendarMark> calendarMarks){
        List<String> tmpTR = new ArrayList<>();
        for (CalendarMark mark:calendarMarks) {
            String data=mark.getService_date().trim();
            tmpTR.add(data);
            System.out.println("日期："+ data);
        }
//        String data=calendarMarks.get(0).getService_date().trim();
//        tmpTR.add(data);
//        System.out.println("日期：" + data);
//        DPCManager.getInstance().setDecorT(tmpTR);

        List<String> tmpT = new ArrayList<>();
        tmpT.add("2016-8-04");
        tmpT.add("2016-8-08");
        tmpT.add("2016-8-09");
        tmpT.add("2016-8-10");
        tmpT.add("2016-8-11");
        tmpT.add("2016-8-30");
        tmpT.add("2016-8-31");
        DPCManager.getInstance().setDecorT(tmpT);
        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorT(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorT(canvas, rect, paint, data);
                switch (data) {
                    default:
                        paint.setColor(Color.RED);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 6, paint);
                        break;
                }

            }

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTR(canvas, rect, paint, data);
                switch (data) {
                    case "2015-10-10":
                    case "2015-10-11":
                    case "2015-10-12":
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                        break;
                    default:
                        paint.setColor(Color.YELLOW);
                        canvas.drawRect(rect, paint);
                        break;
                }
            }

        });
//        picker.invalidate();
    }
}
