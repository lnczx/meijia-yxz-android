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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
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
import com.meijialife.simi.adapter.CompanyListsAdapter;
import com.meijialife.simi.bean.CompanyData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：企业列表选择设置默认
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class CompanyListsActivity extends BaseActivity implements OnClickListener{
    
    
    private CompanyListsAdapter companyListAdapter;
    public static CompanyListsActivity instance =null;
    
    private ArrayList<CompanyData> myCompanyDataList;
    private ArrayList<CompanyData> totalCompanyeList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    private String mCompanyId;//默认企业Id
    
    private RelativeLayout m_rl_friend_add;//弹出popWindow
    private ImageView m_iv_friend_add;//弹出popWindow
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_company_lists);
        super.onCreate(savedInstanceState);
        instance = this;
        initView();
    }
    
    private void initView(){
        
        requestBackBtn();
        setTitleName("公司列表");
        initCompanyView();
    }
    
    private void initCompanyView(){
        totalCompanyeList = new ArrayList<CompanyData>();
        myCompanyDataList = new ArrayList<CompanyData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_company_list);
        findViewById(R.id.m_btn_confirm).setOnClickListener(this);
        m_iv_friend_add=(ImageView) findViewById(R.id.m_iv_friend_add);
        companyListAdapter = new CompanyListsAdapter(this);
        mPullRefreshListView.setAdapter(companyListAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getCompanyListByUserId(page);    
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCompanyListByUserId(page);    
                companyListAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myCompanyDataList!=null && myCompanyDataList.size()>=10){
                    page = page+1;
                    getCompanyListByUserId(page);    
                    companyListAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(CompanyListsActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       CompanyData companyData = totalCompanyeList.get(position);
                       mCompanyId = companyData.getCompany_id();
                       companyListAdapter.setItemId(companyData.getCompany_id());
                       companyListAdapter.notifyDataSetChanged();
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
     * 获取用户所属企业列表
     */
    private void getCompanyListByUserId(int page) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("page", page+"");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_COMPANY_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CompanyListsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myCompanyDataList = gson.fromJson(data, new TypeToken<ArrayList<CompanyData>>() {
                                }.getType());
                                showData(myCompanyDataList);
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
                    UIUtils.showToast(CompanyListsActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 设置默认企业接口
     */
    private void setDefaultCompany() {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("company_id",mCompanyId);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_POST_SET_DEFAULT, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CompanyListsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myCompanyDataList = gson.fromJson(data, new TypeToken<ArrayList<CompanyData>>() {
                                }.getType());
                                showData(myCompanyDataList);
                                CompanyListsActivity.this.finish();
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
                    UIUtils.showToast(CompanyListsActivity.this, errorMsg);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        myCompanyDataList = null;
        totalCompanyeList= null;
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<CompanyData> myCompanyDataList){
        if(myCompanyDataList!=null && myCompanyDataList.size()>0){
            if(page==1){
                totalCompanyeList.clear();
            }
            for (CompanyData companyData : myCompanyDataList) {
                totalCompanyeList.add(companyData);
            }
            //给适配器赋值
            companyListAdapter.setData(totalCompanyeList);
        }
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m_btn_confirm:
            setDefaultCompany();
            break;

        default:
            break;
        }
        
    }
}
