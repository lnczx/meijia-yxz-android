package com.meijialife.simi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ApplyAdapter;
import com.meijialife.simi.adapter.CompanyApplyAdapter;
import com.meijialife.simi.bean.CompanyApply;
import com.meijialife.simi.bean.FriendApplyData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelpers;
import com.meijialife.simi.inter.ListItemClickHelpes;
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

public class FriendApplyActivity extends BaseActivity implements ListItemClickHelpes,ListItemClickHelpers {

    private int applyPage = 1;
    private int companyApplyPage = 1;

    // 布局控件定义
    private PullToRefreshListView mFriendApplyListView;// 上拉刷动态的控件
    private ApplyAdapter applyAdapter;// 好友申请适配器
    private LinearLayout m_ll_list1;
    private LinearLayout m_ll_list2;

    private PullToRefreshListView mTeamApplyListView;// 上拉刷动态的控件
    private CompanyApplyAdapter companyApplyAdapter;// 企业申请适配器

    private ArrayList<FriendApplyData> myApplyList;
    private ArrayList<FriendApplyData> totalApplyList;

    private ArrayList<CompanyApply> myCompanyApplyList;
    private ArrayList<CompanyApply> totalCompanyApplyList;

    private User user;

    private Button mButtonFriend;
    private Button mButtoTeam;
    private Boolean flag =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_friend_apply);
        super.onCreate(savedInstanceState);
        isLogin();
        initView();
    }
    //是否处于登录状态
    private void isLogin(){
        user = DBHelper.getUser(this);
        if(user==null){
            startActivity(new Intent(FriendApplyActivity.this, LoginActivity.class));
            finish();
        }
    }
    private void initView() {

        requestBackBtn();
        setTitleName("成员申请");
        flag = getIntent().getBooleanExtra("flag",true);

        totalApplyList = new ArrayList<FriendApplyData>();
        myApplyList = new ArrayList<FriendApplyData>();
        mFriendApplyListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list_apply);
        applyAdapter = new ApplyAdapter(FriendApplyActivity.this, this);
        mFriendApplyListView.setAdapter(applyAdapter);
        mFriendApplyListView.setMode(Mode.BOTH);
        initIndicators();

        myCompanyApplyList = new ArrayList<CompanyApply>();
        totalCompanyApplyList = new ArrayList<CompanyApply>();
        mTeamApplyListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list_apply_team);
        companyApplyAdapter = new CompanyApplyAdapter(FriendApplyActivity.this, this);
        mTeamApplyListView.setAdapter(companyApplyAdapter);
        mTeamApplyListView.setMode(Mode.BOTH);
        initIndicatores();

        m_ll_list1 = (LinearLayout)findViewById(R.id.m_ll_list1);
        m_ll_list2 =(LinearLayout)findViewById(R.id.m_ll_list2);
        if(flag){//flag=true表示好友申请，=false表示公司申请
            getApplyList(applyPage);
            m_ll_list2.setVisibility(View.GONE);
            m_ll_list1.setVisibility(View.VISIBLE);
        }else{
            getCompanyApplyList(companyApplyPage);
            m_ll_list2.setVisibility(View.VISIBLE);
            m_ll_list1.setVisibility(View.GONE);
        }
        //按钮标题
        mButtonFriend = (Button)findViewById(R.id.m_btn_friend);
        mButtoTeam = (Button)findViewById(R.id.m_btn_team);
        mButtonFriend.setOnClickListener(new AppCenterClickListener());
        mButtoTeam.setOnClickListener(new AppCenterClickListener());
        mButtonFriend.setSelected(true);
        mButtoTeam.setSelected(false);

        mFriendApplyListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                applyPage = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getApplyList(applyPage);
                applyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myApplyList != null && myApplyList.size() >= 10) {
                    applyPage = applyPage + 1;
                    getApplyList(applyPage);
                    applyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FriendApplyActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mFriendApplyListView.onRefreshComplete();
                }
            }
        });


        mTeamApplyListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                companyApplyPage = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCompanyApplyList(companyApplyPage);
                companyApplyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myCompanyApplyList != null && myCompanyApplyList.size() >= 10) {
                    companyApplyPage = companyApplyPage + 1;
                    getCompanyApplyList(companyApplyPage);
                    companyApplyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FriendApplyActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mTeamApplyListView.onRefreshComplete();
                }
            }
        });
    }



    /**
     * 标题按钮点击事件
     */
    class AppCenterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.m_btn_friend:
                    m_ll_list1.setVisibility(View.VISIBLE);
                    m_ll_list2.setVisibility(View.GONE);
                    mButtonFriend.setSelected(true);
                    mButtoTeam.setSelected(false);
                    applyPage =1;
                    myApplyList.clear();
                    totalApplyList.clear();
                    getApplyList(applyPage);
                    break;
                case R.id.m_btn_team:
                    m_ll_list2.setVisibility(View.VISIBLE);
                    m_ll_list1.setVisibility(View.GONE);
                    mButtonFriend.setSelected(false);
                    mButtoTeam.setSelected(true);
                    companyApplyPage=1;
                    myCompanyApplyList.clear();
                    totalCompanyApplyList.clear();
                    getCompanyApplyList(companyApplyPage);
                    break;
                default:
                    break;
            }

        }
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicators() {
        ILoadingLayout startLabels = mFriendApplyListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mFriendApplyListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }
    private void initIndicatores() {
        ILoadingLayout startLabels = mTeamApplyListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mTeamApplyListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 好友申请列表接口
     * @param applyPage
     */
    public void getApplyList(int applyPage) {

        User  user = DBHelper.getUser(this);

        if(user!=null){
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("page", "" + applyPage);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_FRIEND_REQS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(FriendApplyActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                myApplyList = gson.fromJson(data, new TypeToken<ArrayList<FriendApplyData>>() {
                                }.getType());
                                showApplyData(myApplyList);
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
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(FriendApplyActivity.this, errorMsg);
                }
            }
        });}else {
            startActivity(new Intent(FriendApplyActivity.this,LoginActivity.class));
            finish();
        }
    }

    private void showApplyData(List<FriendApplyData> myApplyList) {
        if (applyPage == 1) {
            totalApplyList.clear();
        }
        for (FriendApplyData myApply : myApplyList) {
            totalApplyList.add(myApply);
        }
        // 给适配器赋值
        applyAdapter.setData(totalApplyList);
        mFriendApplyListView.onRefreshComplete();
    }
    private void showCompanyApplyData(List<CompanyApply> myCompanyApplyList) {
        if (companyApplyPage == 1) {
            totalCompanyApplyList.clear();
        }
        for (CompanyApply companyApply : myCompanyApplyList) {
            totalCompanyApplyList.add(companyApply);
        }
        // 给适配器赋值
        companyApplyAdapter.setData(totalCompanyApplyList);
        mTeamApplyListView.onRefreshComplete();
    }

    /**
     * 获取企业申请加入列表
     */
    public void getCompanyApplyList(int companyApplyPage) {

        User  user = DBHelper.getUser(this);

        if(user!=null){
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("page", companyApplyPage+"");
            AjaxParams param = new AjaxParams(map);

            new FinalHttp().get(Constants.URL_GET_COMPANY_PASS, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Toast.makeText(FriendApplyActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                if (StringUtils.isNotEmpty(data)) {
                                    Gson gson = new Gson();
                                    myCompanyApplyList = gson.fromJson(data, new TypeToken<ArrayList<CompanyApply>>() {
                                    }.getType());
                                    showCompanyApplyData(myCompanyApplyList);
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
                        errorMsg = getString(R.string.servers_error);

                    }
                    // 操作失败，显示错误信息
                    if (!StringUtils.isEmpty(errorMsg.trim())) {
                        UIUtils.showToast(FriendApplyActivity.this, errorMsg);
                    }
                }
            });}else {
            startActivity(new Intent(FriendApplyActivity.this,LoginActivity.class));
            finish();
        }
    }


    /**
     * 获取企业申请加入列表
     * @param companyApply
     */
    public void postCompanyApplyList(CompanyApply companyApply) {

        User  user = DBHelper.getUser(this);
        int status = 1;
        if(companyApply.getStatus()==0){
            status = 1;
        }

        if(user!=null){
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", user.getId());
            map.put("company_id",companyApply.getCompany_id());
            map.put("req_user_id", companyApply.getUser_id());
            map.put("status", status+"");
            AjaxParams param = new AjaxParams(map);

            new FinalHttp().post(Constants.URL_GET_PASS, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Toast.makeText(FriendApplyActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                getCompanyApplyList(companyApplyPage);
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
                        errorMsg = getString(R.string.servers_error);

                    }
                    // 操作失败，显示错误信息
                    if (!StringUtils.isEmpty(errorMsg.trim())) {
                        UIUtils.showToast(FriendApplyActivity.this, errorMsg);
                    }
                }
            });}else {
            startActivity(new Intent(FriendApplyActivity.this,LoginActivity.class));
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        applyPage = 1;
        companyApplyPage = 1;
        totalApplyList = new ArrayList<FriendApplyData>();
        myApplyList = new ArrayList<FriendApplyData>();
    }


    @Override
    public void onClick(String typeId) {
        if(StringUtils.isEquals("99",typeId)){
            getApplyList(applyPage);
        }
    }
    @Override
    public void onClick(CompanyApply companyApply) {
        postCompanyApplyList(companyApply);
    }

}
