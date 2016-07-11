package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CompanySettingAdapter;
import com.meijialife.simi.bean.CompanySetting;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 首页常用工具页面
 */
public class CommonUtilActivity extends BaseActivity {

    private ListView listview;
    private CompanySettingAdapter adapter;
    private ArrayList<CompanySetting> companySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_city_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("常用工具");
        requestBackBtn();
        
        adapter = new CompanySettingAdapter(this);
        listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CompanySetting setting = companySettings.get(position);
                Intent intent  = new Intent(CommonUtilActivity.this,WebViewActivity.class);
                intent.putExtra("url",setting.getSetting_json());
                startActivity(intent);
            }
        });
        
    }

    @Override
    protected void onStart() {
        getSetting();
        super.onStart();
    }

    public void getSetting() {
        // 判断是否有网络
        if (!NetworkUtils.isNetworkConnected(CommonUtilActivity.this)) {
            Toast.makeText(CommonUtilActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams params = new AjaxParams(map);
        params.put("setting_type", "common-tools");
        new FinalHttp().get(Constants.URL_GET_USER_SETTING, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(CommonUtilActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                companySettings = gson.fromJson(data, new TypeToken<ArrayList<CompanySetting>>() {
                                }.getType());
                                adapter.setData(companySettings);
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
                    UIUtils.showToast(CommonUtilActivity.this, errorMsg);
                }
            }
        });
    }

}