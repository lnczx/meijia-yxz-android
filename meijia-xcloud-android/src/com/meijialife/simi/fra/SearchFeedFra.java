package com.meijialife.simi.fra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.FeedDetailActivity;
import com.meijialife.simi.activity.FeedListActivity;
import com.meijialife.simi.adapter.FeedListAdapter;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.IndicatorTabBars;
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
 * 搜索-->问答页面
 */
public class SearchFeedFra extends BaseFragment {

    private FeedListAdapter feedListAdapter;
    private ArrayList<FeedData> myFeedDataList;
    private ArrayList<FeedData> totalFeedDataList;
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件

    private int page = 0;
    private String keyword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_search_feed, null);

        initCompanyView(v);
        return v;
    }

    private void initCompanyView(View v) {
        myFeedDataList = new ArrayList<FeedData>();
        totalFeedDataList = new ArrayList<FeedData>();
        mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_listview);
        feedListAdapter = new FeedListAdapter(getActivity());
        mPullRefreshListView.setAdapter(feedListAdapter);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        initIndicator();

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 0;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFeedList(page);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myFeedDataList != null && myFeedDataList.size() >= 10) {
                    page = page + 1;
                    getFeedList(page);
                    feedListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedData feedData = totalFeedDataList.get(position);
                Intent intent = new Intent(getActivity(), FeedDetailActivity.class);
                intent.putExtra("fid", feedData.getFid());
                startActivity(intent);

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

    public void search(String keyword){
        this.keyword = keyword;
        page = 1;
        showDialog();
        getFeedList(page);
    }

    /**
     * 获取问答列表接口
     */
    private void getFeedList(int page) {
        User user = DBHelper.getUser(getActivity());
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        if (user != null) {
            map.put("user_id", user.getId());
        }
        map.put("keyword", keyword);
        map.put("page", page + "");
        map.put("feed_from", "0");
        map.put("feed_type", "2");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_SEARCH_FEED, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                dismissDialog();
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     *
     */
    private void showData(List<FeedData> myFeedDataList) {
        if (myFeedDataList != null && myFeedDataList.size() > 0) {
            if (page == 0) {
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

}
