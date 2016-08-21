package com.meijialife.simi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.bean.ParamsBean;
import com.meijialife.simi.inter.ListItemClickHelps;
import com.meijialife.simi.ui.IndicatorTabBarForTrial;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class TrialCourseListActivity extends Activity implements OnClickListener, ListItemClickHelps {


    private List<HomePosts> homePosts;
    private List<HomePosts> allHomePosts;
    private HomeListAdapter homeListAdapter;
    private PullToRefreshListView mListView;
    private HomeTag homeTag;
    private IndicatorTabBarForTrial mIndicatorTabBar;
    private List<String> tabNames;
    private ParamsBean pBean;

    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_trial_course);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {

        homePosts = new ArrayList<HomePosts>();
        allHomePosts = new ArrayList<HomePosts>();
        homeListAdapter = new HomeListAdapter(this);
        mListView = (PullToRefreshListView) findViewById(R.id.m_trial_listview);
        mListView.setAdapter(homeListAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        initIndicator();
        tabNames = new ArrayList<String>();
        tabNames.add("精选");
        tabNames.add("人力");
        tabNames.add("行政");
        tabNames.add("企管");
        tabNames.add("考证");
        tabNames.add("技能");

        mIndicatorTabBar = (IndicatorTabBarForTrial) findViewById(R.id.m_trial_tabBar);
        mIndicatorTabBar.setCallBack(this);
        mIndicatorTabBar.setMaxColumn(6);
        mIndicatorTabBar.initView(tabNames);

        ListView listView = mListView.getRefreshableView();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mIndicatorTabBar.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                    Toast.makeText(TrialCourseListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int p = position;
                HomePosts homePost = allHomePosts.get(p);
                Intent intent = new Intent(TrialCourseListActivity.this, ArticleDetailActivity.class);
                intent.putExtra("url", homePost.getUrl());
                intent.putExtra("p_id", homePost.getId());// 文章Id
                intent.putExtra("is_show", true);
                intent.putExtra("home_post", homePost);
                intent.putExtra("article_content", homePost.getContent());
                intent.putExtra("from", ArticleDetailActivity.fromTrial);
                startActivity(intent);
            }
        });
        findViewById(R.id.rl_total_search).setOnClickListener(this);
        findViewById(R.id.title_btn_left).setOnClickListener(this);

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
    protected void onStart() {
        pBean = new ParamsBean();
        pBean.setCount("10");
        pBean.setOrder("DESC");
        pBean.setJson("get_tag_posts");
        pBean.setInclude("id,title,modified,url,thumbnail,custom_fields");
        pBean.setSlug("精选课程");
        getMsgList(page, pBean);
        super.onStart();
    }

    /**
     * 获得所有首页列表接口
     */
    public void getMsgList(int page, ParamsBean params) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();

        map.put("json", params.getJson());
        map.put("count", params.getCount());
        map.put("order", params.getOrder());
        map.put("slug", params.getSlug());
        map.put("include", params.getInclude());
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME1_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(TrialCourseListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
    public void onClick(ParamsBean params, boolean flag) {
        allHomePosts.clear();
        getMsgList(page, params);
    }
}