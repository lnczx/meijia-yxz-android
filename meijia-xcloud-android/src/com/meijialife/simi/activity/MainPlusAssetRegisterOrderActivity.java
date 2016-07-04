package com.meijialife.simi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.AssetJsons;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description：加号--资产管理---下单
 * @author： kerryg
 * @date:2016年3月4日
 */
public class MainPlusAssetRegisterOrderActivity extends BaseActivity implements OnClickListener {


    // 控件声明---领用登记
    private EditText mAssetUserName;
    private EditText mAssetUserMobile;
    private TextView mAssetRemarks;
    private TextView m_tv_total;
    private LinearLayout mAssetCategory;
    private ListView m_lv_category;

    //变量声明---领用登记
    private String assetUserNamelStr;// 用户名称
    private String assetUserMobileStr;// 用户手机号
    private String assetRemarksStr;// 领用用途



    private List<Map<String, Object>> listems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_asset_consue);
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {

        requestBackBtn();
        findViewById();
        setOnClick();

    }

    private void setOnClick() {
        findViewById(R.id.m_ll_asset).setOnClickListener(this);
        findViewById(R.id.bt_confirm_asset).setOnClickListener(this);

    }

    private void findViewById() {

        setTitleName("确认领用");
        // 资产领用登记
        mAssetUserName = (EditText) findViewById(R.id.m_asset_user_name);
        mAssetUserMobile = (EditText) findViewById(R.id.m_asset_user_mobile);
        mAssetRemarks = (TextView) findViewById(R.id.tv_beizu_content);
        m_lv_category = (ListView) findViewById(R.id.m_lv_category);
        m_tv_total = (TextView) findViewById(R.id.m_tv_total);
        m_tv_total.setText("总计"+Constants.ASSET_COUNT+"件");
        listems =new ArrayList<Map<String, Object>>();

        for (Iterator asset = Constants.ASSET_MAP_JSON.keySet().iterator(); asset.hasNext();) {
            Long key = (Long) asset.next();
            HashMap<String,Object> item = new HashMap<String,Object>();
            item.put("name",Constants.ASSET_MAP_JSON.get(key).getAsset_name());
            item.put("count",Constants.ASSET_MAP_JSON.get(key).getTotal());
            listems.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,listems,R.layout.asset_type_item,
                new String[]{"name","count"},new int[]{R.id.m_tv_asset_type,R.id.m_tv_asset_count});
        m_lv_category.setAdapter(simpleAdapter);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m_ll_asset:
            Intent intent2 = new Intent(MainPlusAssetRegisterOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.bt_confirm_asset:
                assetUserNamelStr = mAssetUserName.getText().toString().trim();
                assetUserMobileStr = mAssetUserMobile.getText().toString().trim();
               if(Constants.ASSET_MAP_JSON!=null && Constants.ASSET_MAP_JSON.size()>0){
                   postAssetUse();
               }else {
                   UIUtils.showToast(MainPlusAssetRegisterOrderActivity.this, "请选择资产类型");
            }
            break;
        default:
            break;
        }
    }

    /**
     * 公司资产领用记录接口
     */
    private void postAssetUse() {
        
        UserInfo userInfo = DBHelper.getUserInfo(this);
        ArrayList<AssetJsons> list = new ArrayList<>();
        for (Iterator asset = Constants.ASSET_MAP_JSON.keySet().iterator(); asset.hasNext();) {
            Long  key=  (Long) asset.next();
            list.add(Constants.ASSET_MAP_JSON.get(key));
        }
        String  mJson = new Gson().toJson(list);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_id", userInfo.getCompany_id());
        map.put("asset_json", mJson);
        map.put("name", assetUserNamelStr);
        map.put("mobile", assetUserMobileStr);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_ASSET_ASSET_USER_URL, param, new AjaxCallBack<Object>() {
            
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(getApplicationContext(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.debug("成功:" + t.toString());
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) {
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, "入库登记成功了", Toast.LENGTH_SHORT).show();
                            MainPlusAssetRegisterOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusAssetRegisterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusAssetRegisterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAssetRemarks.setText(Constants.WATER_ADD_REMARK);
            }
        });
    }
}
