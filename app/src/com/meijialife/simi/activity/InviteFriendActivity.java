package com.meijialife.simi.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CityListAdapter;
import com.meijialife.simi.bean.CompanyDetail;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.database.bean.City;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常用地址
 */
public class InviteFriendActivity extends BaseActivity {


    private TextView m_tv_company_name;
    private Button m_btn_invite;
    private ImageView m_iv_rq_code;
    private FinalBitmap finalBitmap;
    private String company_id = "0";
    private View layout_mask;
    private CompanyDetail companyDetail;
    private String invitation_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_invite_friend);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("邀请成员加入");
        requestBackBtn();
        company_id = getIntent().getStringExtra("company_id");
        invitation_code = getIntent().getStringExtra("invitation_code");
        finalBitmap = FinalBitmap.create(this);
        m_btn_invite = (Button) findViewById(R.id.m_btn_invite);
        m_tv_company_name = (TextView) findViewById(R.id.m_tv_company_name);
        m_iv_rq_code = (ImageView) findViewById(R.id.iv_rq_code);
        layout_mask = (View) findViewById(R.id.layout_mask);

        getMyRqCode();
        setClick();
    }

    private void setClick() {
        m_btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = DBHelper.getUser(InviteFriendActivity.this).getId();
                ShareConfig.getInstance().init(InviteFriendActivity.this,user_id,invitation_code);
                postShare();
            }
        });
    }

    private void postShare() {
        showMask();
        CustomShareBoard shareBoard = new CustomShareBoard(InviteFriendActivity.this);
        shareBoard.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                GoneMask();
            }
        });
        shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    private void getMyRqCode() {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("company_id", company_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_COMPANY_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(InviteFriendActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                CompanyDetail companyDetail = gson.fromJson(data, CompanyDetail.class);
                                showData(companyDetail);
                            } else {
                                Toast.makeText(InviteFriendActivity.this, "二维码还没有生成", Toast.LENGTH_SHORT).show();
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
                    UIUtils.showToast(InviteFriendActivity.this, errorMsg);
                }
            }
        });
    }

    private void showData(CompanyDetail companyDetail) {
        String rq_url = companyDetail.getQrCode();
        finalBitmap.display(m_iv_rq_code, rq_url);
        m_tv_company_name.setText(companyDetail.getCompanyName());
    }

}