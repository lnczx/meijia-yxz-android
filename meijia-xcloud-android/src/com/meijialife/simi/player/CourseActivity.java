package com.meijialife.simi.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.BindMobileActivity;
import com.meijialife.simi.activity.CommentForNewFrgActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.adapter.VideoRelateListAdapter;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoAliData;
import com.meijialife.simi.bean.VideoData;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.VideoPopWindow;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

import net.tsz.afinal.FinalBitmap;
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
 * <p>
 * Created by Lenovo on 2016/9/21.
 */
public class CourseActivity extends PlayAliyunActivity implements View.OnClickListener {

    private ImageView iv_thum;//播放器缩略图

    private LinearLayout ll_all;
    private TextView tv_vname;//课程名称
    private TextView tv_tname;//讲师
    private TextView tv_count;//阅读数量
    private TextView tv_price;//价格
    private TextView tv_orig_price;//原价
    private TextView tv_detail;//概述

    private TextView btn_take;//参加该课程按钮
    private EditText comment_content;// 评论输入框
    private Button m_btn_confirm;// 发表评论按钮
    private ImageView m_iv_zan;// 点赞

    /**
     * 相关视频列表
     */
    private List<VideoList> videoDatas;
    private VideoRelateListAdapter adapter;
    private ListView listView;

    private VideoList videoListData;//上一页传来的item数据
    private VideoData video;//视频详细信息

    private User user;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
        initBottomView();
        initListView();

        getVideoDetail(videoListData.getArticle_id());
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = DBHelper.getUser(CourseActivity.this);
    }

    private void init() {
        videoListData = (VideoList) getIntent().getSerializableExtra("videoListData");
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);
    }

    /**
     * 初始化View
     */
    private void initView() {
        ((TextView) findViewById(R.id.header_tv_name)).setText("课程详情");
        findViewById(R.id.title_btn_left).setVisibility(View.VISIBLE);
        findViewById(R.id.title_btn_left).setOnClickListener(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        iv_thum = (ImageView) findViewById(R.id.iv_thum);
        FrameLayout.LayoutParams mPortraitPs = new FrameLayout.LayoutParams(screenWidth, screenWidth * 9 / 16);
        iv_thum.setLayoutParams(mPortraitPs);

        ll_all = (LinearLayout) findViewById(R.id.ll_all);
        tv_vname = (TextView) findViewById(R.id.tv_vname);
        tv_tname = (TextView) findViewById(R.id.tv_tname);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_orig_price = (TextView) findViewById(R.id.tv_orig_price);
        tv_orig_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
    }

    /**
     * 初始化底部功能条
     */
    private void initBottomView() {
        findViewById(R.id.layout_mask).setOnClickListener(this);
        findViewById(R.id.m_btn_send_comment).setOnClickListener(this);
        btn_take = (TextView) findViewById(R.id.btn_take);
        comment_content = (EditText) findViewById(R.id.comment_content);
        comment_content.addTextChangedListener(tw);
        m_btn_confirm = (Button) findViewById(R.id.m_btn_confirms);
        m_iv_zan = (ImageView) findViewById(R.id.m_iv_zan);

        btn_take.setOnClickListener(this);
        m_btn_confirm.setOnClickListener(this);
        m_iv_zan.setOnClickListener(this);
        findViewById(R.id.m_iv_comment).setOnClickListener(this);
        findViewById(R.id.m_iv_share).setOnClickListener(this);
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

    private void initListView() {
        videoDatas = new ArrayList<VideoList>();
        adapter = new VideoRelateListAdapter(this);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        UIUtils.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*player.destroyVideo();
                getVideoDetail(videoDatas.get(position).getArticle_id());*/
                Intent intent = new Intent(CourseActivity.this, CourseActivity.class);
                intent.putExtra("videoListData", videoDatas.get(position));
                startActivity(intent);
            }
        });
    }

    private void showData() {
        if (video == null) {
            return;
        }
        tv_vname.setText(video.getTitle());
        tv_tname.setText("讲师:" + video.getTeacher());
        tv_count.setText(video.getTotal_view() + " 人学过");
        tv_price.setText("￥" + video.getDis_price());
        tv_orig_price.setText("￥" + video.getPrice());
        if (isHtml(video.getContent())) {
            tv_detail.setText(Html.fromHtml(video.getContent()));
        } else if (StringUtils.isNotEmpty(video.getContent())) {
            tv_detail.setText(video.getContent());
        }

        if (video.getCategory() != null && video.getCategory().trim().equals("h5")) {
            //弹窗
            VideoPopWindow popWindow = new VideoPopWindow(CourseActivity.this, "提醒", video.getContent_desc(), video.getGoto_url(), video.getArticle_id());
            popWindow.showPopupWindow(ll_all);
        }

        if (video.getIs_join() == 1) {//已参加该课程，直接播放
            btn_take.setVisibility(View.GONE);
            iv_thum.setVisibility(View.GONE);
            if (video.getNeed_playauth() == "1") {
                getVideoAuth(video.getArticle_id(), video.getVideo_url());
            } else {
                playAliyunLocalSource(video.getVideo_url());
            }
        } else {//未参加过
            btn_take.setVisibility(View.VISIBLE);
            iv_thum.setVisibility(View.VISIBLE);
            //显示缩略图
            finalBitmap.display(iv_thum, video.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        }
        if (video.getIs_zan() == 1) {//点过赞
            m_iv_zan.setSelected(true);
        } else {
            m_iv_zan.setSelected(false);
        }
    }

    /**
     * 判断是否包含html代码
     *
     * @param text
     * @return
     */
    private boolean isHtml(String text) {
        if (text.contains("<div") || text.contains("<span") || text.contains("<h1")
                || text.contains("style=") || text.contains("font-size") || text.contains("color:")
                || text.contains("<p") || text.contains("<br") || text.contains("</") || text.contains("<pre")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        switch (v.getId()) {
            case R.id.title_btn_left:
                onBackClicked();
                break;
            case R.id.btn_take://参加该课程
                join();
                break;
            case R.id.layout_mask:// 写评论时的遮罩
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
                if (video != null) {
                    Intent intent = new Intent(CourseActivity.this, CommentForNewFrgActivity.class);
                    intent.putExtra("p_id", video.getArticle_id());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.m_iv_zan:// 点赞
                if (user == null) {
                    startActivity(new Intent(CourseActivity.this, LoginActivity.class));
                } else {
                    postZan("add");
                }
                break;
            case R.id.m_iv_share:// 分享
                if (video != null) {
                    ShareConfig.getInstance().inits(CourseActivity.this, video.getVideo_url(), video.getTitle(), video.getImg_url());
                    postShare();
                } else {
                    Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void postShare() {
        // layout_mask.setVisibility(View.VISIBLE);
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // layout_mask.setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
            }
        });
        shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示输入评论View
     */
    private void showCommentView() {
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
    private void goneCommentView() {
        findViewById(R.id.rl_comment).setVisibility(View.GONE);
        findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
        // 关闭软件盘
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 获得视频详情
     *
     * @param article_id 文章id
     */
    public void getVideoDetail(String article_id) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        /*if(user == null){
            Toast.makeText(CourseActivity.this, "未登录", Toast.LENGTH_SHORT).show();
            return;
        }*/

        user = DBHelper.getUser(CourseActivity.this);

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", article_id);//文章id
        if (user != null) {
            map.put("user_id", user.getId());
        }
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_VIDEO_ALI_DETAIL, param, new AjaxCallBack<Object>() {
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
     * 获得视频认证
     *
     * @param article_id 文章id
     */
    public void getVideoAuth(String article_id, final String url) {

        user = DBHelper.getUser(CourseActivity.this);

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", article_id);//文章id
        if (user != null) {
            map.put("user_id", user.getId());
        }
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.POST_VIDEO_GET_AUTH, param, new AjaxCallBack<Object>() {
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
                            playAliyunPlayAuth(data, url);
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
        if (video == null) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", video.getArticle_id());//文章id
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
                                UIUtils.setListViewHeightBasedOnChildren(listView);
                            } else {
                                //无相关课程
//                                errorMsg = getString(R.string.servers_error);
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
     * 参加课程
     */
    private void join() {
        if (video == null) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user == null) {
            startActivity(new Intent(CourseActivity.this, LoginActivity.class));
            return;
        }
        float price = Float.parseFloat(video.getDis_price());
        if (price > 0) {//去支付
            toPay();
        } else {//免费
            postJoin();
        }
    }

    /**
     * 去支付
     */
    private void toPay() {
        user = DBHelper.getUser(CourseActivity.this);
        if (user.getMobile() == null || user.getMobile().trim().length() < 1) {
            startActivity(new Intent(CourseActivity.this, BindMobileActivity.class));
            return;
        }

        PartnerDetail partnerDetail = new PartnerDetail();
        partnerDetail.setUser_id(Integer.parseInt(video.getPartner_user_id()));
        partnerDetail.setService_type_id(Integer.parseInt(video.getService_type_id()));
        ServicePrices prices = new ServicePrices(Long.parseLong(video.getService_price_id()), Double.parseDouble(video.getPrice()), Double.parseDouble(video.getDis_price()), Long.parseLong(video.getService_price_id()), video.getTitle());

        Intent intent = new Intent(CourseActivity.this, PayOrderActivity.class);
//        intent.putExtra("from",PayOrderActivity.FROM_MEMBER);
        intent.putExtra("name", "购买视频");
        intent.putExtra("PartnerDetail", partnerDetail);
        intent.putExtra("flag", PayOrderActivity.FROM_FIND);
        intent.putExtra("from", PayOrderActivity.FROM_MISHU);
        intent.putExtra("servicePrices", prices);
        startActivityForResult(intent, 0);
    }

    /**
     * * 参加课程接口
     */
    private void postJoin() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", video.getArticle_id());//文章id
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_VIDEO_JOIN_ALI, param, new AjaxCallBack<Object>() {

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
                        LogOut.i("onSuccess", t.toString());
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            btn_take.setVisibility(View.GONE);
                            iv_thum.setVisibility(View.GONE);

                            VideoAliData videoAliData = new Gson().fromJson(data, VideoAliData.class);
                            if (videoAliData.getNeed_playauth() == 1) {//auth
                                getVideoAuth(video.getArticle_id(), videoAliData.getVideo_url());
                            } else if (videoAliData.getNeed_playauth() == 0) {
                                playAliyunLocalSource(videoAliData.getVideo_url());
                            }
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

    /**
     * * 发表评论接口
     */
    private void postComment() {
        if (video == null) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }

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
        map.put("fid", video.getArticle_id());//文章id
        map.put("user_id", user.getId());
        map.put("feed_type", "1");//类型 1 = 文章
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
                            Toast.makeText(CourseActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
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

    /**
     * 点赞接口
     *
     * @param action 操作标识  add = 点赞   del = 取消点赞
     */
    private void postZan(String action) {
        if (video == null) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", video.getArticle_id());//文章id
        map.put("user_id", user.getId());
        map.put("feed_type", "1");//类型 1 =文章
        map.put("action", action);//操作标识  add = 点赞   del = 取消点赞
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_FEED_POST_ZAN, param, new AjaxCallBack<Object>() {

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
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            m_iv_zan.setSelected(true);
                            Toast.makeText(CourseActivity.this, "已点赞", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(resultCode == PayOrderActivity.RESULT_CODE_PAY_OK){//购买课程回来后
//        }
        getVideoDetail(videoListData.getArticle_id());
    }
}
