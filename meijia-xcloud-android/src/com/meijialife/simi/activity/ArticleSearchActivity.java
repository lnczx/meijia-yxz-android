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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现---实现搜索列表展现和热搜词搜索功能
 * @author： kerryg
 * @date:2015年12月5日
 */
public class ArticleSearchActivity extends BaseActivity {

    private TextView tv_search;// 搜索按钮
    private EditText et_search_kw;// 编辑框

    private HomeListAdapter homeListAdapter;
    private List<HomePosts> homePost;
    private List<HomePosts> allHomePosts;
    private HomeTag homeTag;
    
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    public String key="";
    
    
    
    private int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.article_search_activity);
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        et_search_kw = (EditText) findViewById(R.id.et_search_kw);
        initSearchView();
        // 搜索增加点击事件
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String kw = et_search_kw.getText().toString();
                if (!StringUtils.isEmpty(kw)) {
                     searchArticleByKw(kw,page);
                } else {
                    et_search_kw.setHint("请输入搜索内容");
                    return;
                }
            }
        });
        
    }
    private void initSearchView(){
        homePost = new ArrayList<HomePosts>();
        allHomePosts = new ArrayList<HomePosts>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_search_list);
        homeListAdapter = new HomeListAdapter(this);
        mPullRefreshListView.setAdapter(homeListAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                key = et_search_kw.getText().toString();
                if(StringUtils.isNotEmpty(key)){
                    searchArticleByKw(key,page);
                }else {
                    mPullRefreshListView.onRefreshComplete();
                }
                homeListAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(homePost!=null && homePost.size()>=10){
                    page = page+1;
                    key = et_search_kw.getText().toString();
                    if(StringUtils.isNotEmpty(key)){
                        searchArticleByKw(key,page);
                    }else {
                        mPullRefreshListView.onRefreshComplete();
                    }
                    homeListAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(ArticleSearchActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ArticleSearchActivity.this, ArticleDetailActivity.class);
                HomePosts homePost = allHomePosts.get(position);
                intent.putExtra("url", homePost.getUrl());
                intent.putExtra("p_id", homePost.getId());// 文章Id
                intent.putExtra("is_show", true);
                intent.putExtra("home_post",homePost);
                intent.putExtra("article_content",homePost.getContent());
                startActivity(intent);
                
            }
        });
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicator()  
    {  
        ILoadingLayout startLabels = mPullRefreshListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示  
  
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(  
                false, true);  
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示  
    }
    /**
     * 根据关键字搜索服务商
     * 
     * @param service_type_ids
     */
    public void searchArticleByKw(String kw,int page) {
        if (!NetworkUtils.isNetworkConnected(ArticleSearchActivity.this)) {
            Toast.makeText(ArticleSearchActivity.this,"请求失败--第一次---", Toast.LENGTH_SHORT).show();

            Toast.makeText(ArticleSearchActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("count","10");
        map.put("order", "DESC");
        map.put("include","id,title,url,thumbnail,custom_fields");
        map.put("s", kw);
        map.put("page",page+"");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.GET_HOME_SEARCH_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ArticleSearchActivity.this,"请求失败-----", Toast.LENGTH_SHORT).show();

                mPullRefreshListView.onRefreshComplete();
                Toast.makeText(ArticleSearchActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String posts = obj.getString("posts");
                        if (StringUtils.isEquals(status, "ok")) { // 正确
                            if (StringUtils.isNotEmpty(posts)) {
                                Gson gson = new Gson();
                                homePost = gson.fromJson(posts, new TypeToken<ArrayList<HomePosts>>() {
                                }.getType());
                                homeTag = new HomeTag();
                                showData(homePost,homeTag);
                            } else {
                               mPullRefreshListView.onRefreshComplete();
                            }
                        }  else {
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
                    UIUtils.showToast(ArticleSearchActivity.this, errorMsg);
                }
            }
        });
    }

  
    @Override
    protected void onDestroy() {
        super.onDestroy();
        key="";
        homePost.clear();
        allHomePosts.clear();;
    }
    private void showData(List<HomePosts> homePosts, HomeTag homeTag) {
        if (homePosts != null && homePosts.size() > 0) {
            if (page == 1) {
                allHomePosts.clear();
            }
            for (HomePosts homePost : homePosts) {
                allHomePosts.add(homePost);
            }
            homeListAdapter.setData(allHomePosts,homeTag);
        }
        mPullRefreshListView.onRefreshComplete();
    }
    

}
