package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MainPlusAssetAdapter;
import com.meijialife.simi.adapter.MainPlusAssetUseAdapter;
import com.meijialife.simi.bean.AssetData;
import com.meijialife.simi.bean.AssetUseData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---资产列表
 * @author： kerryg
 * @date:2015年12月3日
 */
public class MainPlusAssetListActivity extends Activity {

    // 布局控件定义
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private PullToRefreshListView mPullRefreshUseListView;// 上拉刷新的控件
    private int page = 1;

    //入库
    private ArrayList<AssetData> myAssetList;
    private ArrayList<AssetData> totalAssetList;
    private MainPlusAssetAdapter assetAdapter;//入库适配器
    //领用
    private ArrayList<AssetUseData> myAssetUseList;
    private ArrayList<AssetUseData> totalUseList;
    private MainPlusAssetUseAdapter assetUseAdapter;

    private Button mButtonTool;
    private Button mButtonMoney;

    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mLlBottom;//布局底部控件
    private LinearLayout mAffairCardTitle;


    private LinearLayout mLlNoSigns1;
    private LinearLayout mLlNoSigns2;

    private LinearLayout m_ll_asset_use;//领用记录
    private LinearLayout m_ll_no_signs;//入库记录


    private int leave_from = 0;// 0=领用记录；1=入库记录
    private String titleName = "";
    //创建卡片
    private TextView mTv1;
    private TextView mTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main_plus_asset_list);
        super.onCreate(savedInstanceState);

        isLogin();
        initView();
    }

    /**
     * 是否登录
     */
    private void isLogin() {
        Boolean login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (!login) {
            startActivity(new Intent(MainPlusAssetListActivity.this, LoginActivity.class));
            finish();
            return;
        }
    }

    private void initView() {

        myAssetList = new ArrayList<AssetData>();
        totalAssetList = new ArrayList<AssetData>();
        assetAdapter = new MainPlusAssetAdapter(this);

        myAssetUseList = new ArrayList<AssetUseData>();
        totalUseList = new ArrayList<AssetUseData>();
        assetUseAdapter = new MainPlusAssetUseAdapter(this);
        titleName = getIntent().getStringExtra("title");

        // 按钮标题
        mButtonTool = (Button) findViewById(R.id.m_btn_tools);
        mButtonMoney = (Button) findViewById(R.id.m_btn_money);
        mButtonTool.setOnClickListener(new AppCenterClickListener());
        mButtonMoney.setOnClickListener(new AppCenterClickListener());
        mButtonTool.setSelected(true);
        mButtonMoney.setSelected(false);

        //新建(控件)
        mTv1 = (TextView) findViewById(R.id.m_tv1);
        mTv2 = (TextView) findViewById(R.id.m_tv2);

        mTv1.setText("领用登记");
        mTv2.setText("入库登记");
        //标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout) findViewById(R.id.m_affair_card_title);
        mLlNoSigns1 = (LinearLayout) findViewById(R.id.m_ll_no_signs);
        mLlNoSigns2 = (LinearLayout) findViewById(R.id.m_no_sings);

        //标题背景
        mLlCard = (LinearLayout) findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout) findViewById(R.id.view_card_title_bar);
        mLlBottom = (LinearLayout) findViewById(R.id.m_ll_bottom);

        m_ll_asset_use = (LinearLayout) findViewById(R.id.m_ll_asset_use);
        m_ll_no_signs = (LinearLayout) findViewById(R.id.m_ll_no_signs);

        setOnClick();
        setCardTitleColor();
        setTitleBarColor();

        initLeaveView();

    }

    /*
    * 设置沉浸栏样式
    */
    private void setTitleBarColor() {
        /**
         * 沉浸栏方式实现(android4.4以上)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /*
     * 设置点击事件
     */
    private void setOnClick() {

        mCardBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //领用登记
        mTv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusAssetListActivity.this, AssetConsumeActivity.class);
                startActivity(intent);
            }
        });
        //入库登记
        mTv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusAssetListActivity.this, MainPlusAssetOrderActivity.class);
                startActivity(intent);
            }
        });
        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusAssetListActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.CARD_LAEVE_PASS_HELP_URL);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置标题颜色
     */
    private void setCardTitleColor() {
        mCardTitle.setText(titleName);
        mLlBottom.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
        mLlCard.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
        mRlCard.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));

    }

    /**
     * 标题按钮点击事件
     */
    class AppCenterClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.m_btn_tools:
                    leave_from = 0;//领用记录
                    m_ll_no_signs.setVisibility(View.GONE);
                    m_ll_asset_use.setVisibility(View.VISIBLE);

                    page = 1;
                    mButtonTool.setSelected(true);
                    mButtonMoney.setSelected(false);
                    getAssetUseList(page);
                    break;
                case R.id.m_btn_money://入库记录
                    leave_from = 1;
                    m_ll_no_signs.setVisibility(View.VISIBLE);
                    m_ll_asset_use.setVisibility(View.GONE);

                    page = 1;
                    mButtonMoney.setSelected(true);
                    mButtonTool.setSelected(false);
                    getAssetInList(page);
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 初始化下拉刷新ListView
     */
    private void initLeaveView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_discount_list);
        mPullRefreshListView.setAdapter(assetAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);

        mPullRefreshUseListView = (PullToRefreshListView) findViewById(R.id.m_asset_use_listview);
        mPullRefreshUseListView.setAdapter(assetUseAdapter);
        mPullRefreshUseListView.setMode(Mode.BOTH);

        initIndicator();
        initUseIndicator();
        getAssetUseList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getAssetInList(page);
                assetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myAssetList != null && myAssetList.size() >= 10) {
                    page = page + 1;
                    getAssetInList(page);
                    assetAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainPlusAssetListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });


        mPullRefreshUseListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getAssetUseList(page);
                assetUseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myAssetUseList != null && myAssetUseList.size() >= 10) {
                    page = page + 1;
                    getAssetUseList(page);
                    assetUseAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainPlusAssetListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshUseListView.onRefreshComplete();
                }
            }
        });


    }

    /**
     * 设置入库记录下拉刷新提示
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

    /**
     * 设置领用记录下拉刷新提示
     */
    private void initUseIndicator() {
        ILoadingLayout startLabels = mPullRefreshUseListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshUseListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }


    /**
     * 公司资产登记列表接口--入库列表
     *
     * @param page
     */
    private void getAssetInList(int page) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo userInfo = DBHelper.getUserInfo(this);
        if (userInfo != null) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", userInfo.getUser_id());
            map.put("page", page + "");
            map.put("company_id", userInfo.getCompany_id());

            AjaxParams param = new AjaxParams(map);
            //showDialog();
            new FinalHttp().get(Constants.GET_ASSET_IN_LIST_URL, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    // dismissDialog();
                    Toast.makeText(MainPlusAssetListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String errorMsg = "";
                    //dismissDialog();
                    try {
                        if (StringUtils.isNotEmpty(t.toString())) {
                            JSONObject obj = new JSONObject(t.toString());
                            int status = obj.getInt("status");
                            String msg = obj.getString("msg");
                            String data = obj.getString("data");
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                if (StringUtils.isNotEmpty(data)) {
                                    Gson gson = new Gson();
                                    myAssetList = gson.fromJson(data, new TypeToken<ArrayList<AssetData>>() {
                                    }.getType());
                                    showDataIn(myAssetList);
                                } else {
                                    showDataIn(new ArrayList<AssetData>());
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
                        UIUtils.showToast(MainPlusAssetListActivity.this, errorMsg);
                    }
                }
            });
        } else {
            startActivity(new Intent(MainPlusAssetListActivity.this, LoginActivity.class));
            finish();
        }
    }

    /**
     * 公司资产领用列表接口
     *
     * @param page
     */
    private void getAssetUseList(int page) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo userInfo = DBHelper.getUserInfo(this);
        if (userInfo != null) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("user_id", userInfo.getUser_id());
            map.put("page", page + "");
            map.put("company_id", userInfo.getCompany_id());

            AjaxParams param = new AjaxParams(map);
            //showDialog();
            new FinalHttp().get(Constants.GET_ASSET_USER_LIST_URL, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    // dismissDialog();
                    Toast.makeText(MainPlusAssetListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String errorMsg = "";
                    //dismissDialog();
                    try {
                        if (StringUtils.isNotEmpty(t.toString())) {
                            JSONObject obj = new JSONObject(t.toString());
                            int status = obj.getInt("status");
                            String msg = obj.getString("msg");
                            String data = obj.getString("data");
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                if (StringUtils.isNotEmpty(data)) {
                                    Gson gson = new Gson();
                                    myAssetUseList = gson.fromJson(data, new TypeToken<ArrayList<AssetUseData>>() {
                                    }.getType());
                                    showDataUse(myAssetUseList);
                                } else {
                                    showDataUse(new ArrayList<AssetUseData>());
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
                        UIUtils.showToast(MainPlusAssetListActivity.this, errorMsg);
                    }
                }
            });
        } else {
            startActivity(new Intent(MainPlusAssetListActivity.this, LoginActivity.class));
            finish();
        }
    }

    /**
     * 处理数据加载的方法
     *
     * @param myAssetList
     */
    private void showDataIn(ArrayList<AssetData> myAssetList) {
        if (page == 1) {
            totalAssetList.clear();
            for (AssetData assetData : myAssetList) {
                totalAssetList.add(assetData);
            }
        }
        if (page >= 2) {
            for (AssetData assetData : myAssetList) {
                totalAssetList.add(assetData);
            }
        }
        assetAdapter.setData(totalAssetList);
        mPullRefreshListView.onRefreshComplete();
    }

    /**
     * 领用资产记录显示
     *
     * @param myAssetUseList
     */
    private void showDataUse(ArrayList<AssetUseData> myAssetUseList) {
        if (page == 1) {
            totalUseList.clear();
            for (AssetUseData assetUseData : myAssetUseList) {
                totalUseList.add(assetUseData);
            }
        }
        if (page >= 2) {
            for (AssetUseData assetUseData : myAssetUseList) {
                totalUseList.add(assetUseData);
            }
        }
        assetUseAdapter.setData(totalUseList);
        mPullRefreshUseListView.onRefreshComplete();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (leave_from == 0) {
            getAssetUseList(page);
        } else {
            getAssetInList(page);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        myAssetList = new ArrayList<AssetData>();
        totalAssetList = new ArrayList<AssetData>();
    }
}
