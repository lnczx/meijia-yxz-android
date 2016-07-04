package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.meijialife.simi.adapter.FeedCommentAdapter;
import com.meijialife.simi.bean.FeedCommentData;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelper;
import com.meijialife.simi.utils.AlertWindow;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：文章、动态、问答列表详情
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class FeedDetailActivity extends BaseActivity implements OnClickListener,ListItemClickHelper{
    
    
    private FeedCommentAdapter feedListAdapter;
    
    private ArrayList<FeedCommentData> myFeedCommentDataList;
    private ArrayList<FeedCommentData> totalFeedCommentDataList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private RelativeLayout m__rl_question;
    private int page = 1;
    private View headerView;
    private FeedData feedData;
    private String fid;
    
    private TextView m_tv_money;//金币数
    private ImageView m_iv_gold;//金币图标
    private TextView m_tv_title;//问题题目
    private TextView m_tv_time;//发表时间
    private TextView m_tv_count;//答案个数
    private TextView m_tv_submit;//关闭问题
    
    private User user;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_feed_comment_list);
        super.onCreate(savedInstanceState);
        initView();
    }
    
    @Override
    protected void onStart() {
        getFeed(fid);   
        super.onStart();
    }
    private void initView(){
        user = DBHelper.getUser(FeedDetailActivity.this);
        fid = getIntent().getStringExtra("fid");
        initCompanyView();
        getFeed(fid);
        requestBackBtn();
    }
    
    private void initCompanyView(){
        myFeedCommentDataList = new ArrayList<FeedCommentData>();
        totalFeedCommentDataList = new ArrayList<FeedCommentData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_feed_list);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        headerView = getLayoutInflater().inflate(R.layout.layout_feed_detail, mPullRefreshListView, false);
        headerView.setLayoutParams(layoutParams);
        ListView listView =mPullRefreshListView.getRefreshableView();
        listView.addHeaderView(headerView);
        
        m_tv_money = (TextView)headerView.findViewById(R.id.m_tv_money);
        m_iv_gold = (ImageView) headerView.findViewById(R.id.m_iv_gold);
        m_tv_title = (TextView)headerView.findViewById(R.id.m_tv_title);
        m_tv_time = (TextView)headerView.findViewById(R.id.m_tv_time);
        m_tv_count = (TextView)headerView.findViewById(R.id.m_tv_count);
        m_tv_submit = (TextView)findViewById(R.id.m_tv_submit);
        
        
        m__rl_question = (RelativeLayout)findViewById(R.id.m__rl_question);
        feedListAdapter = new FeedCommentAdapter(this,this);
        mPullRefreshListView.setAdapter(feedListAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        
        setOnClick();
        initIndicator();
     
        
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFeedCommentList(page,feedData.getFid());    
                feedListAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myFeedCommentDataList!=null && myFeedCommentDataList.size()>=10){
                    page = page+1;
                    getFeedCommentList(page,feedData.getFid());    
                    feedListAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(FeedDetailActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    
    private void setOnClick(){
        findViewById(R.id.m_btn_ask).setOnClickListener(this);
        m__rl_question.setOnClickListener(this);
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
     * 获取动态列表接口
     */
    private void getFeedCommentList(int page,String fid) {
        User user = DBHelper.getUser(this);
       
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        if(user!=null){
            map.put("user_id", user.getId());
        }
        map.put("page", page+"");
        map.put("fid",fid);
        map.put("feed_type", "2");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_DYNAMIC_COMMENT_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FeedDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                myFeedCommentDataList = gson.fromJson(data, new TypeToken<ArrayList<FeedCommentData>>() {
                                }.getType());
                                showData(myFeedCommentDataList);
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
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(FeedDetailActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 获取动态列表接口
     */
    private void getFeed(String fid) {
        User user = DBHelper.getUser(this);
        
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        if(user!=null){
            map.put("user_id", user.getId());
        }
        map.put("fid",fid);
        map.put("feed_type", "2");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_DYNAMIC_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FeedDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                feedData = gson.fromJson(data, FeedData.class);
                                showView(feedData);
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
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(FeedDetailActivity.this, errorMsg);
                }
            }
        });
    }
    
    private void showView(FeedData feedData){
        setTitleName(feedData.getName()+"的提问");
        if(StringUtils.isEquals("0",feedData.getFeed_extra())){
            m_iv_gold.setVisibility(View.GONE);
            m_tv_money.setVisibility(View.GONE);

        }else{
            m_iv_gold.setVisibility(View.VISIBLE);
            m_tv_money.setVisibility(View.VISIBLE);
            m_tv_money.setText(feedData.getFeed_extra());
        }
        m_tv_title.setText(feedData.getTitle());
        m_tv_time.setText(feedData.getAdd_time_str());
        m_tv_count.setText(feedData.getTotal_comment()+"个答案");
        Boolean login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if(login){
            if(StringUtils.isEquals(user.getId(),feedData.getUser_id())){
                m__rl_question.setVisibility(View.VISIBLE);
                m_tv_submit.setText("关闭问题");
            }
            if(feedData.getStatus()==2){//问题关闭则不显示关闭按钮
                m__rl_question.setVisibility(View.GONE);
                m_tv_submit.setText("关闭问题"); 
//                findViewById(R.id.m_ll_bottom).setVisibility(View.GONE);
            }
        }else {
            m__rl_question.setVisibility(View.GONE);
            m_tv_submit.setText("关闭问题"); 
//            findViewById(R.id.m_ll_bottom).setVisibility(View.GONE);
        }
        getFeedCommentList(page,fid);
    }
    /**
     * 关闭问题接口
     * @param fid
     */
    private void postCloseCommen(String fid) {
        User user = DBHelper.getUser(this);
        if(user!=null){
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, getString(R.string.net_not_open), 0).show();
                return;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("fid",fid);
            map.put("feed_type", "2");
            AjaxParams param = new AjaxParams(map);
            showDialog();
            new FinalHttp().post(Constants.URL_POST_CLOSE_COMMENT, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    dismissDialog();
                    Toast.makeText(FeedDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(FeedDetailActivity.this,"关闭问题成功",Toast.LENGTH_SHORT).show();
                                finish();
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
                    if(!StringUtils.isEmpty(errorMsg.trim())){
                        mPullRefreshListView.onRefreshComplete();
                        UIUtils.showToast(FeedDetailActivity.this, errorMsg);
                    }
                }
            });}else {
                startActivity(new Intent(FeedDetailActivity.this,LoginActivity.class));
                finish();
            }
    }
    
    /**
     * 问题点赞接口
     * @param comment_id
     * @param action
     */
    private void postZan(String comment_id,String action) {
        User user = DBHelper.getUser(this);
        if(user!=null){
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, getString(R.string.net_not_open), 0).show();
                return;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("fid",feedData.getFid());
            map.put("comment_id",comment_id);
            map.put("action",action);
            map.put("feed_type", "2");
            AjaxParams param = new AjaxParams(map);
            showDialog();
            new FinalHttp().post(Constants.URL_FEED_POST_ZAN, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    dismissDialog();
                    Toast.makeText(FeedDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                getFeedCommentList(page, feedData.getFid());
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
                    if(!StringUtils.isEmpty(errorMsg.trim())){
                        mPullRefreshListView.onRefreshComplete();
                        UIUtils.showToast(FeedDetailActivity.this, errorMsg);
                    }
                }
            });}else {
                startActivity(new Intent(FeedDetailActivity.this,LoginActivity.class));
            }
    }
    /**
     * 采纳回答接口
     * @param comment_id
     * @param action
     */
    private void postDone(String comment_id,String feedId) {
        User user = DBHelper.getUser(this);
        if(user!=null){
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, getString(R.string.net_not_open), 0).show();
                return;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("fid",feedId);
            map.put("comment_id",comment_id);
            map.put("feed_type", "2");
            AjaxParams param = new AjaxParams(map);
            showDialog();
            new FinalHttp().post(Constants.URL_POST_DONE, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    dismissDialog();
                    Toast.makeText(FeedDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                getFeed(feedData.getFid());
//                                getFeedCommentList(page, feedData.getFid());
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
                    if(!StringUtils.isEmpty(errorMsg.trim())){
                        mPullRefreshListView.onRefreshComplete();
                        UIUtils.showToast(FeedDetailActivity.this, errorMsg);
                    }
                }
            });}else {
                startActivity(new Intent(FeedDetailActivity.this,LoginActivity.class));
                finish();
            }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        myFeedCommentDataList = null;
        myFeedCommentDataList= null;
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<FeedCommentData> myFeedCommentDataList){
        if(myFeedCommentDataList!=null && myFeedCommentDataList.size()>0){
            if(page==1){
                totalFeedCommentDataList.clear();
            }
            for (FeedCommentData feedData : myFeedCommentDataList) {
                totalFeedCommentDataList.add(feedData);
            }
            //给适配器赋值
            feedListAdapter.setData(totalFeedCommentDataList,feedData);
        }
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m__rl_question://关闭问题
            
            AlertWindow.dialog(FeedDetailActivity.this, "关闭问题","确定关闭此问题?(问题关闭之后将不会收到新的答案)", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    postCloseCommen(feedData.getFid());
                }
            }, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    return;
                }
            });
            break;
        case R.id.m_btn_ask://我来回答
            Intent intent = new Intent(FeedDetailActivity.this, FeedAnswerActivity.class);
            intent.putExtra("feedData", feedData);
            startActivity(intent);
            break;
        default:
            break;
        }
        
    }
    @Override
    public void onClick(String param1, String param2,boolean flag) {
        if(flag){
            postZan(param1,param2);
        }else {
            postDone(param1,param2);
        }
    }
}
