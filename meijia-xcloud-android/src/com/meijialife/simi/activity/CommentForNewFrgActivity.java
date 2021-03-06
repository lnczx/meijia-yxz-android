package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CommentForNewFrgAdapter;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.WebViewComment;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;


/**
 * @description：首页webView中评论列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class CommentForNewFrgActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private CommentForNewFrgAdapter adapter;
    private User user;
    private ArrayList<WebViewComment> myCommentList;
    private ArrayList<WebViewComment> totalCommentList;
    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private LinearLayout m_ll_comments;//评论文字
    private int page = 1;
    private String fid = "0";//文章Id
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_order_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
    	setTitleName("评论列表");
    	requestBackBtn();
    	fid = getIntent().getStringExtra("p_id");
    	/**
    	 * 列表赋值
    	 */
    	totalCommentList = new ArrayList<WebViewComment>();
    	myCommentList = new ArrayList<WebViewComment>();
    	m_ll_comments = (LinearLayout)findViewById(R.id.m_ll_comments);
    	m_ll_comments.setVisibility(View.VISIBLE);
    	mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
    	mPullRefreshListView.setOnItemClickListener(this);
        adapter = new CommentForNewFrgAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        user = DBHelper.getUser(this);
        getCommentList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCommentList(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myCommentList!=null && myCommentList.size()>=10){
                    page = page+1;
                    getCommentList(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(CommentForNewFrgActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalCommentList = null;
        myCommentList =  null;
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
     * webView评论列表接口
     */
     public void getCommentList(final int page){
         
         myCommentList = new ArrayList<WebViewComment>();
         showData(myCommentList);
         
         //判断是否有网络
         if (!NetworkUtils.isNetworkConnected(CommentForNewFrgActivity.this)) {
             Toast.makeText(CommentForNewFrgActivity.this, getString(R.string.net_not_open), 0).show();
             return;
         }
         Map<String,String> map = new HashMap<String,String>();
         map.put("fid",fid);
         map.put("user_id",user.getId());
         map.put("page",""+page);
         AjaxParams params = new AjaxParams(map);
         showDialog();
         new FinalHttp().get(Constants.URL_GET_DYNAMIC_COMMENT_LIST, params, new AjaxCallBack<Object>() {
             @Override
             public void onFailure(Throwable t, int errorNo, String strMsg) {
                 super.onFailure(t, errorNo, strMsg);
                 dismissDialog();
                 Toast.makeText(CommentForNewFrgActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                 //json字符串转为集合对象
                                 myCommentList = gson.fromJson(data, new TypeToken<ArrayList<WebViewComment>>() {
                                 }.getType());
                               showData(myCommentList);
                             } 
                         } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                             errorMsg = getString(R.string.servers_error);
                             mPullRefreshListView.onRefreshComplete();
                         } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                             errorMsg = getString(R.string.param_missing);
                             mPullRefreshListView.onRefreshComplete();
                         } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                             errorMsg = getString(R.string.param_illegal);
                             mPullRefreshListView.onRefreshComplete();
                         } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                             errorMsg = msg;
                             mPullRefreshListView.onRefreshComplete();
                         } else {
                             errorMsg = getString(R.string.servers_error);
                             mPullRefreshListView.onRefreshComplete();
                         }
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     errorMsg = getString(R.string.servers_error);
                     mPullRefreshListView.onRefreshComplete();
                 }
                 // 操作失败，显示错误信息
                 if (!StringUtils.isEmpty(errorMsg.trim())) {
                     UIUtils.showToast(CommentForNewFrgActivity.this, errorMsg);
                 }
             }
         });
     }
     /**
      * 处理数据加载的方法
      * @param list
      */
     private void showData(List<WebViewComment> myCommentList){
         if(myCommentList!=null && myCommentList.size()>0){
             if(page==1){
                 totalCommentList.clear();
             }
             for (WebViewComment comment : myCommentList) {
                 totalCommentList.add(comment);
             }
             //给适配器赋值
             adapter.setData(totalCommentList);
         }
         mPullRefreshListView.onRefreshComplete();
     }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       /* WebViewComment myOrder = totalOrderList.get(position);
        Intent intent = new Intent(CommentForNewFrgActivity.this,OrderDetailsActivity.class);
        intent.putExtra("myOrder", myOrder);
        intent.putExtra("orderId",myOrder.getOrder_id()+"");
        intent.putExtra("orderStatusId",myOrder.getOrder_status());
        startActivity(intent);*/
    }
}
