package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CompanySettingAdapter;
import com.meijialife.simi.adapter.Find2Adapter;
import com.meijialife.simi.bean.CompanySetting;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.ToActivityUtil;
import com.meijialife.simi.utils.UIUtils;

/**
 * 首页常用工具页面
 */
public class CommonUtilActivity extends BaseActivity implements View.OnClickListener {

    //    private ListView listview;
    private CompanySettingAdapter adapter;
    private ArrayList<CompanySetting> companySettings;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件
    private int page = 1;
    private Find2Adapter findAdapter;
    private List<FindBean> myFindBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_city_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("工具资料");
        requestBackBtn();
        findViewById(R.id.layout_policy).setOnClickListener(this);
        findViewById(R.id.layout_talents).setOnClickListener(this);
        findViewById(R.id.layout_calculator).setOnClickListener(this);
        findViewById(R.id.layout_free_data).setOnClickListener(this);


//        listview = (ListView) findViewById(R.id.listview);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        findAdapter = new Find2Adapter(CommonUtilActivity.this);
        mPullRefreshListView.setAdapter(findAdapter);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        initIndicator();

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getAdsList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FindBean findBean = myFindBeanList.get(position);
                RouteUtil routeUtil = new RouteUtil(CommonUtilActivity.this);
                routeUtil.Routing(findBean.getGoto_type(), findBean.getAction(), findBean.getGoto_url(),
                        findBean.getService_type_ids(), findBean.getTitle());
            }
        });

//        adapter = new CompanySettingAdapter(this);
//        listview = (ListView) findViewById(R.id.listview);
//        listview.setAdapter(adapter);
//
//        listview.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CompanySetting setting = companySettings.get(position);
//                ToActivityUtil.gotoWebPage(CommonUtilActivity.this, "null", setting.getSetting_json());
//            }
//        });


        getAdsList();
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshListView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

//        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
//                false, true);
//        endLabels.setPullLabel("上拉加载");
//        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
//        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 获得频道中广告信息接口（图片+文字）
     */
    public void getAdsList() {
        if (!NetworkUtils.isNetworkConnected(CommonUtilActivity.this)) {
            Toast.makeText(CommonUtilActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "1");
        map.put("page", "" + "1");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(CommonUtilActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFindBeanList = gson.fromJson(data, new TypeToken<List<FindBean>>() {
                                }.getType());
                                showData(myFindBeanList);
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
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(CommonUtilActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     */
    private void showData(List<FindBean> myFindBeanList) {
        if (myFindBeanList != null && myFindBeanList.size() > 0) {
            //给适配器赋值
            findAdapter.setData(myFindBeanList);
        }
        mPullRefreshListView.onRefreshComplete();
    }


    @Deprecated
    public void getSetting() {
        // 判断是否有网络
        if (!NetworkUtils.isNetworkConnected(CommonUtilActivity.this)) {
            Toast.makeText(CommonUtilActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams params = new AjaxParams(map);
        params.put("setting_type", "common-tools");
        new FinalHttp().get(Constants.URL_GET_USER_SETTING, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(CommonUtilActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                companySettings = gson.fromJson(data, new TypeToken<ArrayList<CompanySetting>>() {
                                }.getType());
                                adapter.setData(companySettings);
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
                    UIUtils.showToast(CommonUtilActivity.this, errorMsg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_free_data:
                String url = "http://bolohr.com/doc";
                ToActivityUtil.gotoWebPage(CommonUtilActivity.this, null, url);
                break;
            case R.id.layout_talents:
                String url2 = "http://bolohr.com/hrtest";
                ToActivityUtil.gotoWebPage(CommonUtilActivity.this, null, url2);
                break;
            case R.id.layout_calculator:
                String url3 = "http://app.bolohr.com/simi-h5/show/tools-list.html";
                ToActivityUtil.gotoWebPage(CommonUtilActivity.this, null, url3);
                break;
            case R.id.layout_policy:
                String url4 = "http://bolohr.com/hrpolicy";
                ToActivityUtil.gotoWebPage(CommonUtilActivity.this, null, url4);
                break;
        }

    }
}