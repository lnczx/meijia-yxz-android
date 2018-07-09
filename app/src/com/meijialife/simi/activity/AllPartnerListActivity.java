package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.FindAllAdapter;
import com.meijialife.simi.bean.AdData;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.ui.BannerLayout;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * zhuanjiamianshou
 * 
 */
public class AllPartnerListActivity extends BaseActivity implements OnClickListener {

  
    private ArrayList<FindBean> myFindBeanList;
    private FindAllAdapter appToolsAdapter;
    private LinearLayout find_all_list;
    private GridView gv_application1;
    private List<String> urls;
    private BannerLayout bannerLayout;
    private ArrayList<AdData> adBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_all_partner_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("专家面授");
        requestBackBtn();
        
        myFindBeanList = new ArrayList<FindBean>();
        appToolsAdapter = new FindAllAdapter(this);

        find_all_list = (LinearLayout)findViewById(R.id.find_all_list);
        gv_application1 = (GridView)findViewById(R.id.gv_application1);
        find_all_list.setVisibility(View.VISIBLE);
        gv_application1.setAdapter(appToolsAdapter);
        setOnClick();

        bannerLayout = (BannerLayout) findViewById(R.id.m_top_banner);
        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AdData adBean = adBeanList.get(position);
                RouteUtil routeUtil = new RouteUtil(AllPartnerListActivity.this);
                routeUtil.Routing(adBean.getGoto_type(), adBean.getAction(), adBean.getGoto_url(), adBean.getParams(), adBean.getService_type_ids());
            }
        });
    }

    public void getAdList() {
        if (!NetworkUtils.isNetworkConnected(AllPartnerListActivity.this)) {
            Toast.makeText(AllPartnerListActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "0");
        map.put("app_type", "xcloud");
        AjaxParams param = new AjaxParams(map);
        // showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                // dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                adBeanList = gson.fromJson(data, new TypeToken<ArrayList<AdData>>() {
                                }.getType());
                                showBanner(adBeanList);
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
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(AllPartnerListActivity.this, errorMsg);
                }
            }
        });
    }


    protected void showBanner(List<AdData> adList) {
        urls = new ArrayList<>();
        for (Iterator iterator = adList.iterator(); iterator.hasNext(); ) {
            AdData adBean = (AdData) iterator.next();
            urls.add(adBean.getImg_url());
        }
        bannerLayout.setViewUrls(urls);
    }

    @Override
    protected void onStart() {
        getFind2List();
        getAdList();
        super.onStart();
    }
    private void setOnClick(){
        gv_application1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindBean findBean = myFindBeanList.get(position);
              
                String title_name = findBean.getTitle().trim();
                String goto_type = findBean.getGoto_type().trim();
                String goto_url = findBean.getGoto_url().trim();
                String service_type_ids = findBean.getService_type_ids().toString().trim();
                String sub_service_type_ids = findBean.getSub_service_type_ids().toString().trim();
                if (goto_type.equals("h5")) {
                    Intent intent = new Intent(AllPartnerListActivity.this, WebViewDetailsActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", "");
                    intent.putExtra("service_type_ids", "");
                    intent.putExtra("sub_service_type_ids", "");
                    startActivity(intent);
                } else if (goto_type.equals("app")) {
                   boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
                    if(!is_login){
                        startActivity(new Intent(AllPartnerListActivity.this,LoginActivity.class));
                    }else{
                        Intent intent = new Intent(AllPartnerListActivity.this, FindServerDetailActivity.class);
                        intent.putExtra("service_type_ids", service_type_ids);
                        intent.putExtra("sub_service_type_ids", sub_service_type_ids);
                        intent.putExtra("title_name", title_name);
                        startActivity(intent);
                    }
                } else if (goto_type.equals("h5+list")) {
                    Intent intent = new Intent(AllPartnerListActivity.this, WebViewDetailsActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", title_name);
                    intent.putExtra("service_type_ids", service_type_ids);
                    intent.putExtra("sub_service_type_ids", sub_service_type_ids);
                    startActivity(intent);
                }
            }
        });
        
        findViewById(R.id.rl_total_search).setOnClickListener(this);
    }
    /**
     * 展示服务大厅
     */
    public void getFind2List() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "99");
        map.put("service_type_id", "317");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(AllPartnerListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFindBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                   appToolsAdapter.setData(myFindBeanList);
                            } else {
                                    appToolsAdapter.setData(new ArrayList<FindBean>());
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
                    UIUtils.showToast(AllPartnerListActivity.this, errorMsg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        switch (v.getId()) {
        case R.id.rl_total_search:
            if (!is_login) {
                startActivity(new Intent(AllPartnerListActivity.this.getApplication(), LoginActivity.class));
            } else {
                startActivity(new Intent(AllPartnerListActivity.this, SearchViewActivity.class));
            }
            break;

        default:
            break;
        }
    }

}