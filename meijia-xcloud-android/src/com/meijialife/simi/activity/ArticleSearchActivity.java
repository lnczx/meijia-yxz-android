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
import com.meijialife.simi.adapter.VideoListAdapter;
import com.meijialife.simi.adapter.VideoListAdapter;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
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

    private VideoListAdapter adapter;
    private List<VideoList> videoDatas;
    private List<VideoList> allVideoDatas;
    private HomeTag homeTag;
    
    private PullToRefreshListView mListView;//上拉刷新的控件 
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
        videoDatas = new ArrayList<VideoList>();
        allVideoDatas = new ArrayList<VideoList>();
        mListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_search_list);
        adapter = new VideoListAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setMode(Mode.BOTH);
        initIndicator();
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
                    mListView.onRefreshComplete();
                }
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(videoDatas!=null && videoDatas.size()>=10){
                    page = page+1;
                    key = et_search_kw.getText().toString();
                    if(StringUtils.isNotEmpty(key)){
                        searchArticleByKw(key,page);
                    }else {
                        mListView.onRefreshComplete();
                    }
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(ArticleSearchActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete(); 
                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ArticleSearchActivity.this, CourseActivity.class);
                intent.putExtra("videoListData", allVideoDatas.get(position));
                startActivity(intent);
                
            }
        });
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicator()  
    {  
        ILoadingLayout startLabels = mListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示  
  
        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(  
                false, true);  
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示  
    }
    /**
     * 根据关键字搜索服务商
     * 
     * @param kw
     */
    public void searchArticleByKw(String kw,int page) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
//        map.put("channel_id", "");
        map.put("keyword", kw);
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.GET_VIDEO_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ArticleSearchActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

                mListView.onRefreshComplete();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        LogOut.i("onSuccess", t.toString());
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String data = obj.getString("data");
                        if (StringUtils.isEquals(status, "0")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                videoDatas = gson.fromJson(data, new TypeToken<ArrayList<VideoList>>() {
                                }.getType());
                                homeTag = new HomeTag();
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
                    UIUtils.showToast(ArticleSearchActivity.this, errorMsg);
                }
            }
        });
    }

  
    @Override
    protected void onDestroy() {
        super.onDestroy();
        key="";
        videoDatas.clear();
        allVideoDatas.clear();;
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
        }
        mListView.onRefreshComplete();
    }
    

}
