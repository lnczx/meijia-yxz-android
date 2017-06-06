package com.meijialife.simi.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AlarmListAdapter;
import com.meijialife.simi.bean.AlarmData;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.database.bean.City;
import com.meijialife.simi.ui.wheelview.ArrayWheelAdapter;
import com.meijialife.simi.ui.wheelview.WheelView;
import com.meijialife.simi.ui.wheelview.WheelView.ItemScroListener;
import com.meijialife.simi.utils.AssetsDatabaseManager;
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

//创建企业
public class CompanyRegisterActivity extends BaseActivity implements OnClickListener {


    public static final int START_CITY_FLAG = 1001;

    //企业规模
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    private PopupWindow mTimePopup;
    private View view_mask;

    private EditText m_et_company_name;//名称
    private EditText m_et_company_short_name;//简称
    private TextView m_et_company_location;//地区
    private TextView m_et_company_scale;//规模


    private int cityId = 0;
    private String companyName = "";
    private String shortName = "";
    private int companySize = 0;//0=49人及以下；1=50-99人；2=100-499人；3=500-999人；4=1000人及以上

    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 1000*60;
    private List<City> cityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_company);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        requestBackBtn();
        setTitleName("创建企业");
        m_et_company_name = (EditText) findViewById(R.id.m_et_company_name);
        m_et_company_short_name = (EditText) findViewById(R.id.m_et_company_short_name);
        m_et_company_location = (TextView) findViewById(R.id.m_et_company_location);
        m_et_company_scale = (TextView) findViewById(R.id.m_et_company_scale);
        view_mask = (View) findViewById(R.id.view_mask);

        findViewById(R.id.m_rl_item1).setOnClickListener(this);
        findViewById(R.id.m_rl_item2).setOnClickListener(this);
        findViewById(R.id.bt_register_company).setOnClickListener(this);

        cityList = new ArrayList<City>();
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("simi01.db");
        cityList = AssetsDatabaseManager.searchAllCity(db);
        initLocation(cityList);
    }


    /**
     * 获取用户当前位置的经纬度
     */
    private void initLocation(final List<City> cityList) {
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
        option.setProdName("Secretary"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
        option.setIsNeedAddress(true);// 设置返回城市
        option.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                m_et_company_location.setText(location.getCity());
                for (City city : cityList) {
                    if (StringUtils.isEquals(location.getCity(), city.getName())) {
                        cityId = new Integer(city.getCity_id()).intValue();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_rl_item1://地区rl
                startActivityForResult(new Intent(CompanyRegisterActivity.this, CityListActivity.class), START_CITY_FLAG);
                break;
            case R.id.m_rl_item2://规模rl
                // 关闭软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                showScaleWindow();
                break;
            case R.id.bt_register_company://注册公司
                companyName = m_et_company_name.getText().toString();
                shortName = m_et_company_short_name.getText().toString();
                String scale = m_et_company_scale.getText().toString();
                String location = m_et_company_location.getText().toString();
                if (StringUtils.isEmpty(companyName)) {
                    UIUtils.showToast(CompanyRegisterActivity.this, "请输入企业/团队名称");
                    return;
                }
                if (StringUtils.isEmpty(shortName)) {
                    UIUtils.showToast(CompanyRegisterActivity.this, "请输入企业/团队简称");
                    return;
                }
                if (StringUtils.isEmpty(scale) || companySize < 0) {
                    UIUtils.showToast(CompanyRegisterActivity.this, "请选择企业/团队规模");
                    return;
                }
                if (StringUtils.isEmpty(location) || cityId <= 0) {
                    UIUtils.showToast(CompanyRegisterActivity.this, "请选择企业/团队地区");
                    return;
                }
                postRegCompany();
                break;
        }
    }

    public void showScaleWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_remind, null, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("规模选择");
        InitTimeRemind(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_mask, Gravity.BOTTOM, 0, 0);
        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }

    private View InitTimeRemind(View view) {

        remind = (WheelView) view.findViewById(R.id.remind);
        String[] items = new String[5];
        items[0] = "49人以下";
        items[1] = "50-99人";
        items[2] = "100-400人";
        items[3] = "500-999人";
        items[4] = "1000人及以上";


        arryadapter = new ArrayWheelAdapter<>(this, items);
        remind.setViewAdapter(arryadapter);
        remind.setCyclic(false);// 是否可循环滑动
        remind.setVisibleItems(items.length);// 设置显示行数
        remind.setCurrentItem(0);
        arryadapter.setTextColor(getResources().getColor(R.color.simi_color_black));
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = remind.getCurrentItem();
                companySize = currentItem;
                String itemText = (String) arryadapter.getItemText(currentItem);
                m_et_company_scale.setText(itemText);
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_CITY_FLAG:
                    Bundle extras = data.getExtras();
                    String start_cityname = extras.getString("city_name");
                    cityId = new Integer(extras.getString("city_id")).intValue();
                    m_et_company_location.setText(start_cityname);
                    break;
            }
        }
    }

    /**
     * 用户-公司配置信息接口
     */
    public void postRegCompany() {

        showDialog();
        String user_id = DBHelper.getUser(CompanyRegisterActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected((CompanyRegisterActivity.this))) {
            Toast.makeText(CompanyRegisterActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("city_id", cityId + "");
        map.put("company_name", companyName);
        map.put("short_name", shortName);
        map.put("company_size", companySize + "");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().post(Constants.URL_POST_REG_COMPANY, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CompanyRegisterActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(CompanyRegisterActivity.this, errorMsg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }
}
