package com.meijialife.simi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.VideoListAdapter;
import com.meijialife.simi.bean.AdData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoChannel;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.bean.ParamsBean;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelps;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.player.PlayVodActivity;
import com.meijialife.simi.ui.BannerLayout;
import com.meijialife.simi.ui.IndicatorTabBar;
import com.meijialife.simi.ui.IndicatorTabBarForTrial;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 视听课程
 */
public class TrialCourseListActivity extends Activity
        implements OnClickListener, ListItemClickHelps {

    private List<VideoList> videoDatas;
    private List<VideoList> allVideoDatas;
    private VideoListAdapter adapter;
    private PullToRefreshListView mListView;
    private IndicatorTabBar mIndicatorTabBar;
    private List<VideoChannel> channels;
    private VideoChannel currentChannel; // 当前选中的频道
    private int lastChannelIndex = 0; // 上一个选中的频道；
    private int page = 1;
    private List<String> urls;
    private BannerLayout bannerLayout;
    private ArrayList<AdData> adBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_trial_course);
        super.onCreate(savedInstanceState);

        initView();
        getChannelList();
    }

    private void initView() {
        mIndicatorTabBar = (IndicatorTabBar) findViewById(R.id.m_trial_tabBar);
        mIndicatorTabBar.setCallBack(this);

        videoDatas = new ArrayList<VideoList>();
        allVideoDatas = new ArrayList<VideoList>();
        adapter = new VideoListAdapter(this);
        mListView = (PullToRefreshListView) findViewById(R.id.m_trial_listview);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        initIndicator();

        mListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        // 下拉刷新任务
                        String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                        page = 1;
                        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                        getVideoList(page, currentChannel);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        // 上拉加载任务
                        String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                        if (videoDatas != null && videoDatas.size() >= 10) {
                            page = page + 1;
                            getVideoList(page, currentChannel);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TrialCourseListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT)
                                    .show();
                            mListView.onRefreshComplete();
                        }
                    }
                });

        mListView.setOnItemClickListener(
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*String vid = "57ddfa1b0cf2394d3659a195";
            String client_id = "199b3f31e08d160c";
            String client_secret = "08865c02e2f9dd9c7f11a72a02ddda9a";*/
                        User user = DBHelper.getUser(TrialCourseListActivity.this);
                        if (null == user) {
                            startActivity(new Intent(TrialCourseListActivity.this, LoginActivity.class));
                            return;
                        }

                        Intent intent = new Intent(TrialCourseListActivity.this, CourseActivity.class);
            /*intent.putExtra(PlayVodActivity.CONSTANCE_VID, vid);
            intent.putExtra(PlayVodActivity.CONSTANCE_CLIENT_ID, client_id);
            intent.putExtra(PlayVodActivity.CONSTANCE_CLIENT_SECRET, client_secret);*/
                        intent.putExtra("videoListData", allVideoDatas.get(position));
                        startActivity(intent);
                    }
                });
        findViewById(R.id.rl_total_search).setOnClickListener(this);
        findViewById(R.id.title_btn_left).setOnClickListener(this);

        bannerLayout = (BannerLayout) findViewById(R.id.m_top_banner);
        bannerLayout.setOnBannerItemClickListener(
                new BannerLayout.OnBannerItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        AdData adBean = adBeanList.get(position);
                        RouteUtil routeUtil = new RouteUtil(TrialCourseListActivity.this);
                        routeUtil.Routing(
                                adBean.getGoto_type(),
                                adBean.getAction(),
                                adBean.getGoto_url(),
                                adBean.getService_type_ids(),
                                adBean.getParams());
                    }
                });
    }

    public void getAdList() {
        if (!NetworkUtils.isNetworkConnected(TrialCourseListActivity.this)) {
            Toast.makeText(
                    TrialCourseListActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "0");
        map.put("app_type", "xcloud");
        AjaxParams param = new AjaxParams(map);
        // showDialog();
        new FinalHttp()
                .get(
                        Constants.URL_GET_ADS_LIST,
                        param,
                        new AjaxCallBack<Object>() {
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
                                                adBeanList =
                                                        gson.fromJson(data, new TypeToken<ArrayList<AdData>>() {
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
                                    UIUtils.showToast(TrialCourseListActivity.this, errorMsg);
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
     * 更新顶部频道
     */
    private void updateChannel(List<VideoChannel> channels) {
        mIndicatorTabBar.setMaxColumn(4);
        mIndicatorTabBar.initViewForVideo(channels);
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新"); // 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新..."); // 刷新时
        startLabels.setReleaseLabel("释放更新"); // 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新..."); // 刷新时
        endLabels.setReleaseLabel("释放加载"); // 下来达到一定距离时，显示的提示
    }

    @Override
    protected void onStart() {
        if (currentChannel != null) {
            page = 1;
            getVideoList(page, currentChannel);
        }
        getAdList();
        super.onStart();
    }

    /**
     * 获得视频文章列表
     */
    public void getVideoList(int page, VideoChannel channel) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = DBHelper.getUser(this);

        Map<String, String> map = new HashMap<String, String>();

        if (user != null) {
            map.put("user_id", user.getId());
        }
        map.put("channel_id", channel.getChannel_id());
        map.put("keyword", "");
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp()
                .get(
                        Constants.GET_VIDEO_LIST,
                        param,
                        new AjaxCallBack<Object>() {
                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                super.onFailure(t, errorNo, strMsg);
                                Toast.makeText(
                                        TrialCourseListActivity.this,
                                        getString(R.string.network_failure),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onSuccess(Object t) {
                                super.onSuccess(t);
                                String errorMsg = "";
                                try {
                                    if (StringUtils.isNotEmpty(t.toString())) {
                                        LogOut.i("onSuccess", t.toString());
                                        JSONObject obj = new JSONObject(t.toString());
                                        String status = obj.getString("status");
                                        String msg = obj.getString("msg");
                                        String data = obj.getString("data");
                                        if (StringUtils.isEquals(status, "0")) { // 正确
                                            if (StringUtils.isNotEmpty(data)) {
                                                Gson gson = new Gson();
                                                videoDatas =
                                                        gson.fromJson(data, new TypeToken<ArrayList<VideoList>>() {
                                                        }.getType());
                                                showData(videoDatas);
                                            } else {

                                                mListView.onRefreshComplete();
                                            }
                                        } else {
                                            errorMsg = getString(R.string.servers_error);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mListView.onRefreshComplete();
                                    errorMsg = getString(R.string.servers_error);
                                }
                                // 操作失败，显示错误信息
                                if (!StringUtils.isEmpty(errorMsg.trim())) {
                                    mListView.onRefreshComplete();
                                    UIUtils.showToast(TrialCourseListActivity.this, errorMsg);
                                }
                            }
                        });
    }

    private void showData(List<VideoList> videoDatas) {
        if (videoDatas != null && videoDatas.size() > 0) {
            if (page == 1) {
                allVideoDatas.clear();
            }
            for (VideoList videoData : videoDatas) {
                allVideoDatas.add(videoData);
            }
            adapter.setData(allVideoDatas);
        } else {
            if (page == 1) {
                allVideoDatas.clear();
                adapter.setData(allVideoDatas);
            }
        }
        mListView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_total_search:
                startActivity(new Intent(TrialCourseListActivity.this, ArticleSearchActivity.class));
                break;
            case R.id.title_btn_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(ParamsBean params, int index, boolean flag) {
        allVideoDatas.clear();
        currentChannel = channels.get(index);

        if (index != lastChannelIndex) {
            page = 1;
            lastChannelIndex = index;
        }

        getVideoList(page, currentChannel);
    }

    /**
     * 获得频道列表
     */
    public void getChannelList() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        //        map.put("", "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp()
                .get(
                        Constants.GET_CHANNEL_LIST,
                        param,
                        new AjaxCallBack<Object>() {
                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                super.onFailure(t, errorNo, strMsg);
                                Toast.makeText(
                                        TrialCourseListActivity.this,
                                        getString(R.string.network_failure),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onSuccess(Object t) {
                                super.onSuccess(t);
                                String errorMsg = "";
                                try {
                                    if (StringUtils.isNotEmpty(t.toString())) {
                                        LogOut.i("onSuccess", t.toString());
                                        JSONObject obj = new JSONObject(t.toString());
                                        String status = obj.getString("status");
                                        String msg = obj.getString("msg");
                                        String data = obj.getString("data");
                                        if (StringUtils.isEquals(status, "0")) { // 正确
                                            if (StringUtils.isNotEmpty(data)) {
                                                Gson gson = new Gson();
                                                channels =
                                                        gson.fromJson(
                                                                data, new TypeToken<ArrayList<VideoChannel>>() {
                                                                }.getType());

                                                updateChannel(channels);
                                                currentChannel = channels.get(0);
                                                lastChannelIndex = 0;
                                                getVideoList(page, currentChannel);

                                            } else {
                                                // 无频道
                                                errorMsg = getString(R.string.servers_error);
                                            }
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
                                    UIUtils.showToast(TrialCourseListActivity.this, errorMsg);
                                }
                            }
                        });
    }
}
