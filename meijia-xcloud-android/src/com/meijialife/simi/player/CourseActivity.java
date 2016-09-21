package com.meijialife.simi.player;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoChannel;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 课程详情
 *
 * Created by Lenovo on 2016/9/21.
 */
public class CourseActivity extends PlayVodActivity implements View.OnClickListener{

    private TextView tv_vname;//课程名称
    private TextView tv_tname;//讲师
    private TextView tv_count;//阅读数量
    private TextView tv_price;//价格
    private TextView tv_orig_price;//原价
    private TextView tv_detail;//概述

    private ListView listView;

    private VideoList videoListData;//上一页传来的item数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();

        getVideoDetail();
    }

    private void init(){
        videoListData = (VideoList) getIntent().getSerializableExtra("videoListData");
    }

    private void initView(){
        ((TextView)findViewById(R.id.header_tv_name)).setText("课程详情");
        findViewById(R.id.title_btn_left).setVisibility(View.VISIBLE);
        findViewById(R.id.title_btn_left).setOnClickListener(this);

        tv_vname = (TextView)findViewById(R.id.tv_vname);
        tv_tname = (TextView)findViewById(R.id.tv_tname);
        tv_count = (TextView)findViewById(R.id.tv_count);
        tv_price = (TextView)findViewById(R.id.tv_price);
        tv_orig_price = (TextView)findViewById(R.id.tv_orig_price);
        tv_orig_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_detail = (TextView)findViewById(R.id.tv_detail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_btn_left:
                finish();
                break;
        }
    }

    /**
     * 获得视频详情
     */
    public void getVideoDetail() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = DBHelper.getUser(CourseActivity.this);
        if(user == null){
            Toast.makeText(CourseActivity.this, "未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", videoListData.getArticle_id());//文章id
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_VIDEO_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(CourseActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        LogOut.i("onSuccess", t.toString());
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (StringUtils.isEquals(status, "0")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
//                                channels = gson.fromJson(data, new TypeToken<ArrayList<VideoChannel>>() {
//                                }.getType());

                            } else {
                                errorMsg = getString(R.string.servers_error);
                            }
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
                    UIUtils.showToast(CourseActivity.this, errorMsg);
                }
            }
        });
    }
}
