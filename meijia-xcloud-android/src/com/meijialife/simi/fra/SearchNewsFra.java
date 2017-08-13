package com.meijialife.simi.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
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
 * 搜索-->文章页面 （同首页下面的文章列表一样）
 */
public class SearchNewsFra extends BaseFragment {

    private PullToRefreshListView mListView;
    private List<HomePosts> homePosts;
    private List<HomePosts> allHomePosts;
    private HomeListAdapter homeListAdapter;

    private int page = 1;
    private String keyword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_search_news, null);

        initListView(v);

        return v;
    }

    private void initListView(View v) {
        homePosts = new ArrayList<HomePosts>();
        allHomePosts = new ArrayList<HomePosts>();
        mListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_listview);

        homeListAdapter = new HomeListAdapter(getActivity());
        mListView.setAdapter(homeListAdapter);

        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        initIndicator();

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getUserTagMsgList(page);
                homeListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (homePosts != null && homePosts.size() >= 10) {
                    page = page + 1;
                    getUserTagMsgList(page);
                    homeListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position >= 2) {
//                    int p = position - 1;
//                    // int p = position;
//                    HomePosts homePost = allHomePosts.get(p);
//                    Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
//                    // Intent intent = new Intent(getActivity(), WebViewsActivity.class);
//                    intent.putExtra("url", homePost.getUrl());
//                    intent.putExtra("p_id", homePost.getId());// 文章Id
//                    intent.putExtra("is_show", true);
//                    intent.putExtra("article_content", homePost.getContent());
//                    getActivity().startActivity(intent);
//                }
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

    public void search(String keyword){
        this.keyword = keyword;
        page = 1;
        showDialog();
        getUserTagMsgList(page);
    }

    /**
     * 根据用户标签访问精选接口
     *
     * @param page
     */
    public void getUserTagMsgList(int page) {
        if(keyword == null || keyword.length() == 0){
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = DBHelper.getUser(getActivity());

        Map<String, String> map = new HashMap<String, String>();

        if(user != null){
            map.put("user_id", user.getId());
        }

        map.put("keyword", keyword);
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_SEARCH_NEWS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
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
                                HomeTag homeTag = new HomeTag();
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

}
