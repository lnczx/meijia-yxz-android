package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.easeui.EaseConstant;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.PopupMenu;
import com.meijialife.simi.ui.PopupMenu.MENUITEM;
import com.meijialife.simi.ui.PopupMenu.OnItemClickListener;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.ui.ChatActivity;
import com.simi.easemob.utils.ShareConfig;

/**
 * @description：工具箱--更多--应用中心专属webView
 * @author： kerryg
 * @date:2015年12月3日
 */
public class WebViewsActivity extends Activity implements OnClickListener {

    private String url;

    private WebView webview;
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;
    private ProgressBar mProgressBar; // webView进度条
    // 右边菜单
    private ImageView iv_menu;
    private PopupMenu popupMenu;
    private View layout_mask;
    private String titles;// 页面title

    // 下面评论按钮
    private LinearLayout webview_comment;
    private boolean is_show = false;// 是否显示底部评论按钮

    private EditText comment_content;// 评论输入框
    private Button m_btn_confirm;// 发表评论按钮

    private ImageView m_iv_zan;// 点赞
    private int m_p_id;// 文章Id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.webview_personal_fragment);
        super.onCreate(savedInstanceState);
        init();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"JavascriptInterface", "NewApi", "SetJavaScriptEnabled"})
    private void init() {
        url = getIntent().getStringExtra("url");
        m_p_id = getIntent().getIntExtra("p_id", 0);
        is_show = getIntent().getBooleanExtra("is_show", false);

        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView) findViewById(R.id.iv_person_close);
        tv_person_title = (TextView) findViewById(R.id.tv_person_title);
        mProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        webview = (WebView) findViewById(R.id.webview);
        iv_menu = (ImageView) findViewById(R.id.iv_person_more);
        layout_mask = findViewById(R.id.layout_mask);
        webview_comment = (LinearLayout) findViewById(R.id.webview_comment);
        if (is_show) {
            webview_comment.setVisibility(View.VISIBLE);
        }
        popupMenu = new PopupMenu(this);

        setOnClick();

        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
            return;
        }

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title)  {
                super.onReceivedTitle(view, title);
                titles = title;
                tv_person_title.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        };
        // 设置WebChromeClinent对象

        webview.setWebChromeClient(wvcc);
        WebSettings webSettings = webview.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webview.addJavascriptInterface(this, "Koolearn");
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);// 是否可以缩放，默认true
        // popwindow显示webview不能设置缩放按钮，否则触屏就会报错。
        // webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(false);// 是否使用缓存
        webSettings.setDomStorageEnabled(true);// DOM Storage
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setInitialScale(100);
        if (Build.VERSION.SDK_INT >= 21) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // webSettings.setUseWideViewPort(true);
        // webSettings.setLoadWithOverviewMode(true);
        // webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // webSettings.setLoadWithOverviewMode(true);

        // webview设置启用数据库
        webSettings.setDatabaseEnabled(true);
        // 设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        // 启用地理定位
        webSettings.setGeolocationEnabled(true);
        // 开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        iv_person_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_person_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null && webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
        iv_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.showLocation(R.id.iv_person_more);
                popupMenu.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(MENUITEM item, String str) {

                        switch (item) {
                            case ITEM1:// 刷新
                                webview.reload();
                                break;
                            case ITEM2:// 分享
                                ShareConfig.getInstance().inits(WebViewsActivity.this, url, titles);
                                postShare();
                                break;
                            case ITEM3:// 吐槽
                                Intent intent = new Intent(WebViewsActivity.this, ChatActivity.class);
                                intent.putExtra(EaseConstant.EXTRA_USER_ID, "simi-user-366");
                                intent.putExtra(EaseConstant.EXTRA_USER_NAME, "云小秘");
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
        webview.loadUrl(url);
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

    private void setOnClick() {

        comment_content = (EditText) findViewById(R.id.comment_content);
        m_btn_confirm = (Button) findViewById(R.id.m_btn_confirms);
        m_iv_zan = (ImageView) findViewById(R.id.m_iv_zan);
        findViewById(R.id.m_btn_send_comment).setOnClickListener(this);
        findViewById(R.id.m_btn_confirms).setOnClickListener(this);
        findViewById(R.id.layout_mask).setOnClickListener(this);

        findViewById(R.id.m_iv_comment).setOnClickListener(this);
        findViewById(R.id.m_iv_zan).setOnClickListener(this);
        findViewById(R.id.m_iv_share).setOnClickListener(this);

        comment_content.addTextChangedListener(tw);
      /*  boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (!is_login) {
            startActivity(new Intent(WebViewsActivity.this, LoginActivity.class));
        } else {
            getZan();//是否点赞接口
        }*/
    }

    private void postShare() {
        layout_mask.setVisibility(View.VISIBLE);
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                layout_mask.setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
            }
        });
        shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (webview != null && (keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            // Toast.makeText(WebViewsActivity.this,"第二次返回",Toast.LENGTH_LONG).show();
            findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
            findViewById(R.id.m_webview_comment).setVisibility(View.GONE);
            layout_mask.setVisibility(View.GONE);
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        switch (v.getId()) {
            case R.id.layout_mask:// 遮罩
                findViewById(R.id.m_webview_comment).setVisibility(View.GONE);
                layout_mask.setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
                // 关闭软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;
            case R.id.m_btn_confirms:// 发表按钮
                findViewById(R.id.m_webview_comment).setVisibility(View.GONE);
                layout_mask.setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
                // 关闭软件盘
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (!is_login) {
                    startActivity(new Intent(WebViewsActivity.this, LoginActivity.class));
                } else {
                    postComment(); // 发表评论
                }
                break;
            case R.id.m_btn_send_comment:// 写评论按钮
                findViewById(R.id.m_webview_comment).setVisibility(View.VISIBLE);
                findViewById(R.id.webview_comment).setVisibility(View.GONE);
                layout_mask.setVisibility(View.VISIBLE);

                // 弹出软键盘
                comment_content.setFocusable(true);
                comment_content.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) comment_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.m_iv_comment:// 评论
                if (!is_login) {
                    startActivity(new Intent(WebViewsActivity.this, LoginActivity.class));
                } else {
                    Intent intent = new Intent(WebViewsActivity.this, CommentForNewFrgActivity.class);
                    intent.putExtra("p_id", m_p_id);
                    startActivity(intent);
                }
                break;
            case R.id.m_iv_zan:// 点赞
                if (!is_login) {
                    startActivity(new Intent(WebViewsActivity.this, LoginActivity.class));
                } else {
                    if (m_iv_zan.isSelected()) {
                        postZan("del");//取消点赞
                    } else {
                        postZan("add");//点赞
                    }
                }
                break;
            case R.id.m_iv_share:// 分享
                findViewById(R.id.m_webview_comment).setVisibility(View.GONE);
                findViewById(R.id.layout_mask).setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.GONE);
                ShareConfig.getInstance().inits(WebViewsActivity.this, url, titles);
                postShare();
                break;
            default:
                break;
        }
    }

    /**
     * *发表评论接口
     */
    private void postComment() {

        String comment = comment_content.getText().toString();
        if (StringUtils.isEmpty(comment.trim())) {
            Toast.makeText(this, "还没有输入评论内容哦~", Toast.LENGTH_SHORT).show();
            return;
        }

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", m_p_id + "");
        map.put("user_id", user_id);
        map.put("comment", comment);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_FEED_COMMENT, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(WebViewsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(WebViewsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(WebViewsActivity.this, "网络错误,请稍后重试");
                }
            }
        });
    }

    /**
     * 点赞接口
     */
    private void postZan(String action) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", m_p_id + "");
        map.put("user_id", user_id);
        map.put("action", action);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_FEED_POST_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(WebViewsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            getZan();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(WebViewsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(WebViewsActivity.this, "网络错误,请稍后重试");
                }
            }
        });
    }

    /**
     * 获取是否点赞
     */
    private void getZan() {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", m_p_id + "");
        map.put("user_id", user_id);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_FEED_GET_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(WebViewsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isEmpty(data)) {//表示未点赞
                                m_iv_zan.setSelected(false);
//                               m_iv_zan.setEnabled(true);//可点击
                            } else {
                                m_iv_zan.setSelected(true);
//                               m_iv_zan.setEnabled(false);//不可点击
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(WebViewsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(WebViewsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WebViewsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(WebViewsActivity.this, "网络错误,请稍后重试");
                }
            }
        });
    }
}
