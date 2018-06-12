package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.FeedListAdapter;
import com.meijialife.simi.bean.AdData;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelpes;
import com.meijialife.simi.ui.BannerLayout;
import com.meijialife.simi.ui.IndicatorTabBars;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：文章、动态、问答列表
 * @author： kerryg
 * @date:2015年12月5日
 */
public class FeedListActivity extends BaseActivity implements OnClickListener, ListItemClickHelpes {

    private FeedListAdapter feedListAdapter;
    private ArrayList<FeedData> myFeedDataList;
    private ArrayList<FeedData> totalFeedDataList;
    private List<String> tabNames;
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private IndicatorTabBars tab_indicators;
    private RelativeLayout m__rl_question;
    private int page = 1;
    private String feedFrom = "0";// 0=所有，1=我发布
    private String lastFeedFrom = "0";
    private List<String> urls;
    private BannerLayout bannerLayout;
    private ArrayList<AdData> adBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_feed_list);
        super.onCreate(savedInstanceState);
        initView();
    }


    @Override
    protected void onStart() {
        getFeedList(page, feedFrom);
        getAdList();
        super.onStart();
    }

    private void initView() {

        requestBackBtn();
        setTitleName("问答互助");
        initCompanyView();

    }

    private void initCompanyView() {
        myFeedDataList = new ArrayList<FeedData>();
        totalFeedDataList = new ArrayList<FeedData>();
        tab_indicators = (IndicatorTabBars) findViewById(R.id.tab_indicators);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_feed_list);
        m__rl_question = (RelativeLayout) findViewById(R.id.m__rl_question);
        m__rl_question.setVisibility(View.VISIBLE);
        m__rl_question.setOnClickListener(this);
        feedListAdapter = new FeedListAdapter(this);
        mPullRefreshListView.setAdapter(feedListAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);

        tabNames = new ArrayList<String>();
        tabNames.add("最新");
        tabNames.add("悬赏");
        tabNames.add("精选");
        tabNames.add("我的");

        tab_indicators = (IndicatorTabBars) findViewById(R.id.tab_indicators);
        tab_indicators.setCallBack(this);
        tab_indicators.setMaxColumn(4);
        tab_indicators.initView(tabNames);

        initIndicator();

        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFeedList(page, feedFrom);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myFeedDataList != null && myFeedDataList.size() >= 10) {
                    page = page + 1;
                    getFeedList(page, feedFrom);
                    feedListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FeedListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedData feedData = totalFeedDataList.get(position);
                Intent intent = new Intent(FeedListActivity.this, FeedDetailActivity.class);
                intent.putExtra("fid", feedData.getFid());
                startActivity(intent);

            }
        });

        bannerLayout = (BannerLayout) findViewById(R.id.m_top_banner);
        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AdData adBean = adBeanList.get(position);
                RouteUtil routeUtil = new RouteUtil(FeedListActivity.this);
                routeUtil.Routing(adBean.getGoto_type(), adBean.getAction(), adBean.getGoto_url(), adBean.getParams(), adBean.getService_type_ids());
            }
        });
    }
    public void getAdList() {
        if (!NetworkUtils.isNetworkConnected(FeedListActivity.this)) {
            Toast.makeText(FeedListActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "0");
        map.put("app_type", "xcloud");
        AjaxParams param = new AjaxParams(map);
        // showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
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
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                adBeanList = gson.fromJson(data, new TypeToken<ArrayList<AdData>>() {
                                }.getType());
                                showBanner(adBeanList);
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
                    UIUtils.showToast(FeedListActivity.this, errorMsg);
                }
            }
        });
    }


    protected void showBanner(List<AdData> adList) {
        urls = new ArrayList<>();
        for (Iterator iterator = adList.iterator(); iterator.hasNext(); ) {
            AdData adBean = (AdData) iterator.next();
            urls.add(adBean.getImg_url());
        }
        bannerLayout.setViewUrls(urls);
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

    /**
     * 获取动态列表接口
     */
    private void getFeedList(int page, String feed_from) {
        User user = DBHelper.getUser(this);
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        if (user != null) {
            map.put("user_id", user.getId());
        }
        map.put("page", page + "");
        map.put("feed_from", feed_from);
        map.put("feed_type", "2");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_FRIEND_DYNAMIC_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(FeedListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFeedDataList = gson.fromJson(data, new TypeToken<ArrayList<FeedData>>() {
                                }.getType());
                                showData(myFeedDataList);
                            } else {
                                feedListAdapter.setData(new ArrayList<FeedData>());
                                mPullRefreshListView.onRefreshComplete();
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
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(FeedListActivity.this, errorMsg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 处理数据加载的方法
     *
     * @param list
     */
    private void showData(List<FeedData> myFeedDataList) {
        if (myFeedDataList != null && myFeedDataList.size() > 0) {
            if (page == 1) {
                totalFeedDataList.clear();
            }
            for (FeedData feedData : myFeedDataList) {
                totalFeedDataList.add(feedData);
            }
            // 给适配器赋值
            feedListAdapter.setData(totalFeedDataList);
        }
        mPullRefreshListView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        Boolean login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        switch (v.getId()) {
            case R.id.m__rl_question:// 我要提问
                if (!login) {
                    startActivity(new Intent(FeedListActivity.this, LoginActivity.class));
                    return;
                } else {
                    startActivity(new Intent(FeedListActivity.this, FeedQuestionActivity.class));
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(String typeId) {
        feedFrom = typeId;

        if (!feedFrom.equals(lastFeedFrom)) {
            page = 1;
            lastFeedFrom = feedFrom;
        }

        getFeedList(page, feedFrom);
    }
}
