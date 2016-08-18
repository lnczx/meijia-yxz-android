package com.meijialife.simi.fra;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.meijialife.simi.activity.AllPartnerListActivity;
import com.meijialife.simi.activity.ArticleDetailActivity;
import com.meijialife.simi.activity.ArticleSearchActivity;
import com.meijialife.simi.activity.ChannelListActivity;
import com.meijialife.simi.activity.CommonUtilActivity;
import com.meijialife.simi.activity.FeedListActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.TrialCourseListActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.bean.ParamsBean;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelps;
import com.meijialife.simi.ui.BannerLayout;
import com.meijialife.simi.ui.IndicatorTabBar;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.ui.SignPopWindow;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SimpleLoginImpl;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.community.share.UMShareServiceFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zbar.lib.CaptureActivity;

import net.tsz.afinal.FinalBitmap;
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
 * 首页
 * 
 * @author RUI
 *
*/
public class Home1NewFra extends BaseFragment implements OnClickListener, ListItemClickHelps {

    private View v;
    private View v1;
    private FinalBitmap finalBitmap;
    private final static int SCANNIN_GREQUEST_CODES = 5;

    private ArrayList<FindBean> findBeanList;
    // 列表
    private PullToRefreshListView mListView;
    private HomeTag homeTag;
    private List<HomePosts> homePosts;
    private List<HomePosts> allHomePosts;
    private HomeListAdapter homeListAdapter;
    private View headerView;
    private Button loadMoreButton;
    private LinearLayout mLlLoadMore;

    private int page = 1;

    private IndicatorTabBar mIndicatorTabBar1;
    private IndicatorTabBar mIndicatorTabBar3;
    private List<String> tabNames;
    private ParamsBean pBean;

    private LinearLayout m_rl_category;
    private LinearLayout new_frg_search;

    private LinearLayout ll_more_columns;// 更多频道
    private LinearLayout ll_more_columns2;// 更多频道
    private String userTagString = "";
    private List<String> urls;
    private BannerLayout bannerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_1, null);
        v1 = inflater.inflate(R.layout.home_1, null);

        initView(v);

        return v;
    }

    private void initView(View v) {
        // 初始化首页广告
        finalBitmap = FinalBitmap.create(getActivity());
        finalBitmap.configDiskCachePath(getActivity().getApplication().getFilesDir().toString());
        finalBitmap.configDiskCacheSize(1024 * 1024 * 10);
        finalBitmap.configLoadfailImage(R.drawable.ad_loading);
        urls = new ArrayList<String>();

        initListView(v);
        m_rl_category = (LinearLayout) v.findViewById(R.id.m_rl_category);
        new_frg_search = (LinearLayout) v.findViewById(R.id.new_frg_search);
        ll_more_columns = (LinearLayout) v.findViewById(R.id.ll_more_columns);
        ll_more_columns2 = (LinearLayout) v.findViewById(R.id.ll_more_columns2);
        setListener(v);

        tabNames = new ArrayList<String>();
        tabNames.add("精选");
        tabNames.add("招聘");
        tabNames.add("绩效");
        tabNames.add("薪资");
        tabNames.add("案例");
        tabNames.add("行政");
        tabNames.add("职场");
        tabNames.add("培训");
        tabNames.add("员工关系");
        tabNames.add(" 人资规划");
        tabNames.add("行业");

        mIndicatorTabBar1 = (IndicatorTabBar) v.findViewById(R.id.tab_indicator1);
        mIndicatorTabBar1.setCallBack(this);
        mIndicatorTabBar1.setMaxColumn(5);
        mIndicatorTabBar1.initView(tabNames);

        mIndicatorTabBar3 = (IndicatorTabBar) v.findViewById(R.id.tab_indicator3);
        mIndicatorTabBar3.setCallBack(this);
        mIndicatorTabBar3.setMaxColumn(5);
        mIndicatorTabBar3.initView(tabNames);

    }

    private void setListener(View v) {
        v.findViewById(R.id.m_home1).setOnClickListener(this);
        v.findViewById(R.id.m_home2).setOnClickListener(this);
        v.findViewById(R.id.m_home3).setOnClickListener(this);
        v.findViewById(R.id.m_home4).setOnClickListener(this);
        v.findViewById(R.id.m_homes1).setOnClickListener(this);
        v.findViewById(R.id.m_homes2).setOnClickListener(this);
        v.findViewById(R.id.m_homes3).setOnClickListener(this);
        v.findViewById(R.id.m_homes4).setOnClickListener(this);
        v.findViewById(R.id.btn_saoma).setOnClickListener(this);
        v.findViewById(R.id.rl_total_search).setOnClickListener(this);
        v.findViewById(R.id.m_rl_sign).setOnClickListener(this);
        v.findViewById(R.id.m_rl_question).setOnClickListener(this);
        ll_more_columns.setOnClickListener(this);
        ll_more_columns2.setOnClickListener(this);
    }

    private void initListView(View v) {
        homePosts = new ArrayList<HomePosts>();
        allHomePosts = new ArrayList<HomePosts>();
        findBeanList = new ArrayList<FindBean>();
        mListView = (PullToRefreshListView) v.findViewById(R.id.m_lv_home);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.home1_banner, mListView, false);
        headerView.setLayoutParams(layoutParams);
        ListView listView = mListView.getRefreshableView();
        listView.addHeaderView(headerView);

        bannerLayout = (BannerLayout) headerView.findViewById(R.id.m_top_banner);
        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                FindBean findBean = findBeanList.get(position);
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", findBean.getGoto_url());
                startActivity(intent);
            }
        });

        View headview2 = View.inflate(getActivity(), R.layout.new_frg_bottom, null);
        listView.addHeaderView(headview2);// ListView条目中的悬浮部分 添加到头部
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mIndicatorTabBar1.getParent().requestDisallowInterceptTouchEvent(false);
                mIndicatorTabBar3.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        // listView.addFooterView(footView);
        mListView.setMode(Mode.BOTH);
        initIndicator();
        mLlLoadMore = (LinearLayout) v.findViewById(R.id.m_ll_load_more);

        homeListAdapter = new HomeListAdapter(getActivity());
        mListView.setAdapter(homeListAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getMsgList(page, pBean);
                homeListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (homePosts != null && homePosts.size() >= 10) {
                    page = page + 1;
                    getMsgList(page, pBean);
                    homeListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                }
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int p = position - 2;
                // int p = position;
                HomePosts homePost = allHomePosts.get(p);
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                // Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                intent.putExtra("url", homePost.getUrl());
                intent.putExtra("p_id", homePost.getId());// 文章Id
                intent.putExtra("is_show", true);
                intent.putExtra("home_post", homePost);
                intent.putExtra("article_content", homePost.getContent());
                getActivity().startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {

                    AnimationSet as = new AnimationSet(true);
                    AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
                    aa.setDuration(500);
                    as.addAnimation(aa);
                    LayoutAnimationController ac = new LayoutAnimationController(as);
                    new_frg_search.setLayoutAnimation(ac);
                    new_frg_search.setVisibility(View.VISIBLE);
                    m_rl_category.setVisibility(View.VISIBLE);
                } else {
                    m_rl_category.setVisibility(View.GONE);
                    new_frg_search.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    @Override
    public void onStart() {
        pBean = new ParamsBean();
        pBean.setCount("10");
        pBean.setOrder("DESC");
        pBean.setJson("get_tag_posts");
        String userTags = SpFileUtil.getString(getActivity(), SpFileUtil.KEY_USER_TAGS, Constants.USER_TAGS, "");
        pBean.setInclude("id,title,modified,url,thumbnail,custom_fields");
        // 根据用户标签是否为空判断访问不同的精选接口
        if (StringUtils.isNotEmpty(userTags)) {
            pBean.setSlug(userTags);
            getUserTagMsgList(page, pBean);
        } else {
            pBean.setSlug("%E9%A6%96%E9%A1%B5%E7%B2%BE%E9%80%89");
            getMsgList(page, pBean);
        }
        getAdList();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainActivity");
        /*
         * pBean = new ParamsBean(); pBean.setCount("10"); pBean.setOrder("DESC"); pBean.setJson("get_tag_posts"); String userTags =
         * SpFileUtil.getString(getActivity(),SpFileUtil.KEY_USER_TAGS, Constants.USER_TAGS,"");
         * pBean.setInclude("id,title,modified,url,thumbnail,custom_fields"); //根据用户标签是否为空判断访问不同的精选接口 if(StringUtils.isNotEmpty(userTags)){
         * pBean.setSlug(userTags); getUserTagMsgList(page,pBean); }else { pBean.setSlug("%E9%A6%96%E9%A1%B5%E7%B2%BE%E9%80%89"); getMsgList(page,
         * pBean); } getAdList();
         */
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");

    }

    public void getAdList() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
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
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                findBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                showBanner(findBeanList);
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

    protected void showBanner(List<FindBean> adList) {
        urls.clear();
        for (Iterator iterator = adList.iterator(); iterator.hasNext();) {
            FindBean findBean = (FindBean) iterator.next();
            urls.add(findBean.getImg_url());
        }
        bannerLayout.setViewUrls(urls);
    }

    /**
     * 获得所有首页列表接口
     */
    public void getMsgList(int page, ParamsBean params) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();

        map.put("json", params.getJson());
        map.put("count", params.getCount());
        map.put("order", params.getOrder());
        // json分为两种：1、get_tag_posts;2、get_category_posts
        if (StringUtils.isEquals(params.getJson(), "get_tag_posts")) {
            map.put("slug", params.getSlug());
        } else {
            map.put("id", params.getId());
        }
        map.put("include", params.getInclude());
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME1_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String data = obj.getString("pages");
                        // String tag = obj.getString("tag");
                        String posts = obj.getString("posts");
                        if (StringUtils.isEquals(status, "ok")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                homePosts = gson.fromJson(posts, new TypeToken<ArrayList<HomePosts>>() {
                                }.getType());
                                // homeTag = gson.fromJson(tag, HomeTag.class);
                                homeTag = new HomeTag();
                                showData(homePosts, homeTag);
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
                    if (isAdded()) {
                        errorMsg = getString(R.string.servers_error);
                    }
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mListView.onRefreshComplete();
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    /**
     * 根据用户标签访问精选接口
     * 
     * @param page
     * @param params
     */
    public void getUserTagMsgList(int page, ParamsBean params) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();

        map.put("count", params.getCount());
        map.put("order", params.getOrder());
        map.put("slug", params.getSlug());
        map.put("include", params.getInclude());
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME3_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String data = obj.getString("pages");
                        // String tag = obj.getString("tag");
                        String posts = obj.getString("posts");
                        if (StringUtils.isEquals(status, "ok")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                homePosts = gson.fromJson(posts, new TypeToken<ArrayList<HomePosts>>() {
                                }.getType());
                                // homeTag = gson.fromJson(tag, HomeTag.class);
                                homeTag = new HomeTag();
                                showData(homePosts, homeTag);
                            }
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mListView.onRefreshComplete();
                    if (isAdded()) {
                        errorMsg = getString(R.string.servers_error);
                    }
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mListView.onRefreshComplete();
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    private void showData(List<HomePosts> homePosts, HomeTag homeTag) {
        if (homePosts != null && homePosts.size() > 0) {
            if (page == 1) {
                allHomePosts.clear();
            }
            for (HomePosts homePost : homePosts) {
                allHomePosts.add(homePost);
            }
            homeListAdapter.setData(allHomePosts, homeTag);
        }
        mListView.onRefreshComplete();
    }

    /**
     * 首页扫描加好友
     * 
     * @param friend_id
     */
    public void addFriend(final String friend_id) {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("friend_id", friend_id);
        AjaxParams param = new AjaxParams(map);

        // showDialog();
        new FinalHttp().get(Constants.URL_GET_ADD_FRIEND, param, new AjaxCallBack<Object>() {
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
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            // 添加成功，跳转到好友界面
                            Intent intent = new Intent(getActivity(), FriendPageActivity.class);
                            intent.putExtra("friend_id", friend_id);
                            startActivity(intent);
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

    protected void useCustomLogin() {
        LoginSDKManager.getInstance().addAndUse(new SimpleLoginImpl());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        boolean is_login = SpFileUtil.getBoolean(getActivity().getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        switch (v.getId()) {
        case R.id.m_home1://试听课程
            startActivity(new Intent(getActivity(), TrialCourseListActivity.class));
            break;
        case R.id.m_home2:
          /*  intent = new Intent(getActivity(), WebViewsActivity.class);
            intent.putExtra("url", Constants.JIN_PIN_KE_CHENG_URL);
            getActivity().startActivity(intent);*/
            break;
        case R.id.m_home3:// 知识学院
            intent = new Intent(getActivity(), WebViewsActivity.class);
            intent.putExtra("url", Constants.ZHI_SHI_XUE_YUAN_URL);
            getActivity().startActivity(intent);
            break;
        case R.id.m_home4:// 常用工具
            startActivity(new Intent(getActivity(), CommonUtilActivity.class));
            break;
        case R.id.m_rl_sign:// 首页签到
            if (!is_login) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                postSign();
            }
            break;
        case R.id.m_rl_question: // 同行热聊
            // 打开微社区的接口, 参数1为Context类型(关注页面-------AllFeedsFragment,主页面-------CommunityMainFragment)
            //关注Fragment：AllFeedsFragment;关注布局：umeng_comm_feeds_frgm_layout,登录布局：umeng_comm_login_dialog
            CommunitySDK mCommSDK = CommunityFactory.getCommSDK(getActivity());
            mCommSDK.openCommunity(getActivity());
            UMShareServiceFactory.getSocialService().getConfig()
                    .setPlatforms(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
            UMShareServiceFactory.getSocialService().getConfig()
                    .setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
            useCustomLogin();
            break;
        case R.id.m_homes1://互助问答
            startActivity(new Intent(getActivity(), FeedListActivity.class));
            break;

        case R.id.m_homes2:// 简历交换
            intent = new Intent(getActivity(), WebViewsActivity.class);
            intent.putExtra("url", Constants.JIAN_LI_JIAO_HUAN_URL);
            getActivity().startActivity(intent);
            break;

        case R.id.m_homes3:// 找服务商
            intent = new Intent(getActivity(), AllPartnerListActivity.class);
            getActivity().startActivity(intent);
            break;

        case R.id.m_homes4:// 福利商城
            is_login = SpFileUtil.getBoolean(getActivity().getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
            if (!is_login) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                Intent intent6 = new Intent();
                intent6.setClass(getActivity(), PointsShopActivity.class);
                intent6.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
                intent6.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
                intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(getActivity()).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
                getActivity().startActivity(intent6);
            }
            break;
        case R.id.btn_saoma:// 二维码
            Intent intents = new Intent();
            intents.setClass(getActivity(), CaptureActivity.class);
            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intents, SCANNIN_GREQUEST_CODES);
            break;
        case R.id.rl_total_search:// 跳到到搜索页面
            startActivity(new Intent(getActivity(), ArticleSearchActivity.class));
            break;
        case R.id.ll_more_columns:// 频道列表页面
            if (!is_login) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ChannelListActivity.class));
            }
            break;
        case R.id.ll_more_columns2:// 频道列表页面
            if (!is_login) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ChannelListActivity.class));
            }
            break;
        default:
            break;
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

    /**
     * 签到获取积分接口
     */
    private void postSign() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.net_not_open),Toast.LENGTH_SHORT).show();
            return;
        }
        User user = DBHelper.getUser(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", "" + user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_DAY_SIGN, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (!StringUtils.isEmpty(data)) {
                                SignPopWindow signPopWindow = new SignPopWindow(getActivity(), msg, data);
                                signPopWindow.showPopupWindow(v1);
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getActivity().getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getActivity().getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getActivity().getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getActivity().getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isAdded()) {
                        errorMsg = getActivity().getString(R.string.servers_error);
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
    public void onDestroy() {
        super.onDestroy();
        page = 1;
        homePosts.clear();
        allHomePosts.clear();
    }

    @Override
    public void onClick(ParamsBean params, boolean flag) {
        allHomePosts.clear();
        if (flag) {
            getUserTagMsgList(page, params);
        } else {
            getMsgList(page, params);
        }
    }
}
