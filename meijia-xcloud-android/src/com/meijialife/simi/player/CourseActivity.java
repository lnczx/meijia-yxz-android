package com.meijialife.simi.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CommentForNewFrgActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.adapter.VideoRelateListAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoData;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private EditText comment_content;// 评论输入框
    private Button m_btn_confirm;// 发表评论按钮

    /**
     * 相关视频列表
     */
    private List<VideoList> videoDatas;
    private VideoRelateListAdapter adapter;
    private ListView listView;

    private VideoList videoListData;//上一页传来的item数据
    private VideoData video;//视频详细信息

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
        initBottomView();
        initListView();

        getVideoDetail();
    }

    private void init(){
        user = DBHelper.getUser(CourseActivity.this);
        videoListData = (VideoList) getIntent().getSerializableExtra("videoListData");
    }

    /**
     * 初始化View
     */
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

    /**
     * 初始化底部功能条
     */
    private void initBottomView(){
        findViewById(R.id.layout_mask).setOnClickListener(this);
        findViewById(R.id.m_btn_send_comment).setOnClickListener(this);
        comment_content = (EditText) findViewById(R.id.comment_content);
        comment_content.addTextChangedListener(tw);
        m_btn_confirm = (Button) findViewById(R.id.m_btn_confirms);

        m_btn_confirm.setOnClickListener(this);
        findViewById(R.id.m_iv_comment).setOnClickListener(this);
    }

    TextWatcher tw = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
            m_btn_confirm.setEnabled(false);
            m_btn_confirm.setSelected(false);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            m_btn_confirm.setEnabled(false);
            m_btn_confirm.setSelected(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String content = comment_content.getText().toString();
            if (StringUtils.isNotEmpty(content) && !StringUtils.isEquals("写下您的看法与见解……", content.trim())) {
                m_btn_confirm.setEnabled(true);
                m_btn_confirm.setSelected(true);
            } else {
                m_btn_confirm.setEnabled(false);
                m_btn_confirm.setSelected(false);
            }
        }
    };

    private void initListView(){
        videoDatas = new ArrayList<VideoList>();
        adapter = new VideoRelateListAdapter(this);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        UIUtils.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CourseActivity.this, position + " 被点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showData(){
        if(video == null){
            return;
        }
        tv_vname.setText(video.getTitle());
        tv_count.setText(video.getTotal_view() + " 人学过");
        tv_price.setText("￥" + video.getDis_price());
        tv_orig_price.setText("￥" + video.getPrice());
        tv_detail.setText(video.getContent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.layout_mask:// 遮罩
                goneCommentView();
                break;
            case R.id.m_btn_send_comment://写评论
                showCommentView();
                break;
            case R.id.m_btn_confirms://发表评论
                goneCommentView();

                if (user == null) {
                    startActivity(new Intent(CourseActivity.this, LoginActivity.class));
                } else {
                    postComment();
                }
                break;
            case R.id.m_iv_comment:// 评论页面
                Intent intent = new Intent(CourseActivity.this, CommentForNewFrgActivity.class);
                intent.putExtra("p_id", videoListData.getArticle_id());
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示输入评论View
     */
    private void showCommentView(){
        findViewById(R.id.rl_comment).setVisibility(View.VISIBLE);
        findViewById(R.id.webview_comment).setVisibility(View.GONE);
        // 弹出软键盘
        comment_content.setFocusable(true);
        comment_content.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) comment_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入评论View
     */
    private void goneCommentView(){
        findViewById(R.id.rl_comment).setVisibility(View.GONE);
        findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
        // 关闭软件盘
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 获得视频详情
     */
    public void getVideoDetail() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
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
                                video = gson.fromJson(data, VideoData.class);

                                showData();
                                getVideoRelateList();

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

    /**
     * 获得相关视频列表
     */
    public void getVideoRelateList() {
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
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_VIDEO_RELATE, param, new AjaxCallBack<Object>() {
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
                                videoDatas = gson.fromJson(data, new TypeToken<ArrayList<VideoList>>() {
                                }.getType());
                                adapter.setData(videoDatas);
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

    /**
     * * 发表评论接口
     */
    private void postComment() {

        String comment = comment_content.getText().toString();
        if (StringUtils.isEmpty(comment.trim())) {
            Toast.makeText(this, "还没有输入评论内容哦~", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", videoListData.getArticle_id());//文章id
        map.put("user_id", user.getId());
        map.put("feed_type", "");//类型 1 = 文章
        map.put("comment", comment);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_FEED_COMMENT, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(CourseActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            comment_content.setText("");
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(CourseActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(CourseActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(CourseActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(CourseActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CourseActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(ArticleDetailActivity.this, "网络错误,请稍后重试");
                }
            }
        });
    }
}
