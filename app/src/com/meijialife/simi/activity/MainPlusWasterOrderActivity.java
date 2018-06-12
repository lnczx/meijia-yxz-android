package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.AddressData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---企业内训--下单
 * @author： kerryg
 * @date:2016年3月4日 
 */
public class MainPlusWasterOrderActivity extends BaseActivity implements OnClickListener {
 
  
    private UserInfo userInfo;
    
    private TextView mWasterAddr;//送水地址
    private EditText mWasterLinkMan;//联系人
    private EditText mWasterLinkTel;//联系电话
    private TextView mWasterRemark;//备注
    private TextView mWaterBandName;
    
    private String mLinkMan;
    private String mLinkTel;//
    private String mRemark;
    private String mServiceAddrId;
    private String mServiceAddrName;
    
    private String wasterTypeName;
    private String wasterTypeId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_waster);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        initView();

    }

    private void initView() {
        
        requestBackBtn();
        setTitleName("下单");
        
        mWasterAddr = (TextView)findViewById(R.id.m_tv_water_addr);
        mWasterLinkMan = (EditText)findViewById(R.id.m_et_water_link_man);
        mWasterLinkTel = (EditText)findViewById(R.id.m_et_water_link_tel);
        mWasterRemark = (TextView)findViewById(R.id.m_tv_water_remark);
        mWaterBandName=(TextView)findViewById(R.id.m_tv_band_title);
        
        
        findViewById(R.id.m_rl_remark).setOnClickListener(this);
        findViewById(R.id.m_iv_plus).setOnClickListener(this);
        findViewById(R.id.m_iv_min).setOnClickListener(this);
        findViewById(R.id.m_rl_addr).setOnClickListener(this);
        findViewById(R.id.bt_create_water).setOnClickListener(this);
        findViewById(R.id.layout_select_band).setOnClickListener(this);

        getAddList();
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case RESULT_OK:
            mServiceAddrId= data.getExtras().getString("addr_id");
            mServiceAddrName = data.getExtras().getString("addr_name");
            Constants.WATER_ADD_ADDRESS = mServiceAddrName;
            break;
        case RESULT_FIRST_USER:
            wasterTypeId= data.getExtras().getString("wasterTypeId");
            wasterTypeName= data.getExtras().getString("wasterTypeName");
            Constants.WATER_TYPE_NAME = wasterTypeName;
            break;
        default:
            break;
        }
    }


    /**
     * 送水下单接口
     */
    private void postWasterAdd() {
        showDialog();
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("addr_id", mServiceAddrId);
        map.put("recycle_type",wasterTypeId);//0=企业培训定制化服务 1=内训体系设计与建设 2=管理咨询与解决方案 3=其他
        map.put("link_man", mLinkMan);
        map.put("link_tel", mLinkTel);
        map.put("remarks", Constants.WATER_ADD_REMARK);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_WASTER_ORDER_URL, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
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
                            Toast.makeText(MainPlusWasterOrderActivity.this, "下单成功了", Toast.LENGTH_SHORT).show();
                            MainPlusWasterOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusWasterOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK="";
        Constants.WATER_ADD_ADDRESS="";
        Constants.WATER_TYPE_NAME="";
      
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.layout_select_band:
            intent.putExtra("flag","99");
            intent.setClass(MainPlusWasterOrderActivity.this, MainPlusWasterTypeActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.m_rl_remark:
            Intent intent2 = new Intent(MainPlusWasterOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.m_rl_addr:
            intent.putExtra("flag",99);// 1=表示从支付页面进入优惠券列表
            intent.setClass(MainPlusWasterOrderActivity.this, AddressActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.bt_create_water:
            mLinkMan = mWasterLinkMan.getText().toString();
            mLinkTel = mWasterLinkTel.getText().toString();
            mRemark = mWasterRemark.getText().toString();
            if (StringUtils.isEmpty(mLinkMan)) {
                UIUtils.showToast(MainPlusWasterOrderActivity.this, "请输入联系人");
                dismissDialog();
                return;
            }
            if (StringUtils.isEmpty(mLinkTel)) {
                UIUtils.showToast(MainPlusWasterOrderActivity.this, "请输入联系电话");
                return;
            }
           /* if (StringUtils.isEmpty(mRemark)) {
                UIUtils.showToast(MainPlusWasterOrderActivity.this, "请输入备注");
                return;
            }*/
            postWasterAdd();                
            break;
        default:
            break;
        }
    }


    private ArrayList<AddressData> addressList;

    /**
     * 获取地址列表
     */
    private void getAddList() {
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADDRS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(MainPlusWasterOrderActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                addressList = gson.fromJson(data, new TypeToken<ArrayList<AddressData>>() {
                                }.getType());
                                showDefaultData(addressList);
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
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(MainPlusWasterOrderActivity.this, errorMsg);
                }
            }
        });
    }

    public void showDefaultData(ArrayList<AddressData> addressList){
        for (AddressData  addressData:  addressList) {
            if(addressData.getIs_default()==1){
                mServiceAddrId= addressData.getId();
                mServiceAddrName = addressData.getName();
                mWasterAddr.setText(mServiceAddrName);;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 mWasterRemark.setText(Constants.WATER_ADD_REMARK);
                mWasterAddr.setText(Constants.WATER_ADD_ADDRESS);
                mWaterBandName.setText(Constants.WATER_TYPE_NAME);
            }
        });
    }
}
