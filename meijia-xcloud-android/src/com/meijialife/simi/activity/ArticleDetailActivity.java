package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.HomePost;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * 首页文章详情
 * 
 */
public class ArticleDetailActivity extends BaseActivity implements OnClickListener {

    private ProgressBar mProgressBar; // webView进度条
    private View layout_mask;// 遮罩
    private WebView webview;

    // 下面评论按钮
    private LinearLayout webview_comment;
    private boolean is_show = false;// 是否显示底部评论按钮

    private EditText comment_content;// 评论输入框
    private Button m_btn_confirm;// 发表评论按钮
    private ImageView m_iv_zan;// 点赞
    private String pId;// 文章Id
    private String url;

    private HomePost homePost;
    private String pTitle = "";// 文章标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article_detail);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        url = getIntent().getStringExtra("url");
        pId = getIntent().getStringExtra("p_id");
        is_show = getIntent().getBooleanExtra("is_show", false);

        // setTitleName(homePost.getTitle());
        requestBackBtn();

        initWebView();
        getMsgList(pId);
        setOnClick();

    }

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    private void initWebView() {
        mProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        webview = (WebView) findViewById(R.id.webview);
        layout_mask = findViewById(R.id.layout_mask);
        webview_comment = (LinearLayout) findViewById(R.id.webview_comment);
        if (is_show) {
            webview_comment.setVisibility(View.VISIBLE);
        }
       
        // 设置WebChromeClinent对象
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setBackgroundColor(0);
        webview.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//解决图文混排滑动闪烁
        WebSettings webSettings = webview.getSettings();
        
        // 支持内容重新布局
        webSettings.setDefaultTextEncodingName("utf-8");
//        webview.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
//        webSettings.setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        // popwindow显示webview不能设置缩放按钮，否则触屏就会报错。
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(false);// 是否使用缓存
        webSettings.setDomStorageEnabled(true);// DOM Storage
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        //禁止webView缩放
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        
        webSettings.setDatabaseEnabled(true);// webview设置启用数据库
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();// 设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDomStorageEnabled(true); // 开启DomStorage缓存
        webSettings.setDefaultFontSize(40);//设置默认字体大小
        webview.setWebViewClient(new MyWebViewClient());

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
    @SuppressLint("SetJavaScriptEnabled")
	public class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);  
            super.onPageStarted(view, url, favicon);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true); 
            addImageClickListner();
            super.onPageFinished(view, url);
        }
    }
    
    
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitleName(title);
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
    //定义JavaScript方法调用接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.setClass(context, ShowWebImageActivity.class);
            context.startActivity(intent);
        }
    }
    //增加图片点击事件
    private void addImageClickListner() {
     /*   webview.loadUrl("javascript:(function(){" +
                "document.getElementsByTagName(\"a\").href='#'; " 
                +"})()");*/
      /*  webview.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " + 
                "for(var i=0;i<objs.length;i++)  " + 
                "{"
                + "    objs[i].onclick=function()  " + 
                "    {  " 
                + "    window.imagelistner.openImage(this.src);  " + 
                "    }  " + 
                "}" + 
                "})()");*/
    }

    
    
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
        boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (is_login) {
            getZan();//是否点赞接口      
        }
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
                startActivity(new Intent(ArticleDetailActivity.this, LoginActivity.class));
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
            findViewById(R.id.layout_mask).setVisibility(View.GONE);
            if (!is_login) {
                startActivity(new Intent(ArticleDetailActivity.this, LoginActivity.class));
            } else {
                Intent intent = new Intent(ArticleDetailActivity.this, CommentForNewFrgActivity.class);
                intent.putExtra("p_id", pId);
                startActivity(intent);
            }
            break;
        case R.id.m_iv_zan:// 点赞
            if (!is_login) {
                startActivity(new Intent(ArticleDetailActivity.this, LoginActivity.class));
            } else {
                if (m_iv_zan.isSelected()) {
                    postZan("del");// 取消点赞
                } else {
                    postZan("add");// 点赞
                }
            }
            break;
        case R.id.m_iv_share:// 分享
            findViewById(R.id.m_webview_comment).setVisibility(View.GONE);
            layout_mask.setVisibility(View.VISIBLE);
            findViewById(R.id.webview_comment).setVisibility(View.GONE);
            ShareConfig.getInstance().inits(ArticleDetailActivity.this, url, pTitle);
            postShare();
            break;
        default:
            break;
        }
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
        map.put("fid", pId + "");
        map.put("user_id", user_id);
        map.put("action", action);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_FEED_POST_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(ArticleDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(ArticleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
     * 获取是否点赞
     */
    private void getZan() {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", pId);
        map.put("user_id", user_id);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_FEED_GET_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(ArticleDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isEmpty(data)) {// 表示未点赞
                                m_iv_zan.setSelected(false);
                                // m_iv_zan.setEnabled(true);//可点击
                            } else {
                                m_iv_zan.setSelected(true);
                                // m_iv_zan.setEnabled(false);//不可点击
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(ArticleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
     ** 发表评论接口
     */
    private void postComment() {

        String comment = comment_content.getText().toString();
        if (StringUtils.isEmpty(comment.trim())) {
            Toast.makeText(this, "还没有输入评论内容哦~", Toast.LENGTH_SHORT).show();
            return;
        }

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", pId);
        map.put("user_id", user_id);
        map.put("comment", comment);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_FEED_COMMENT, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(ArticleDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(ArticleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ArticleDetailActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(ArticleDetailActivity.this, "网络错误,请稍后重试");
                }
            }
        });
    }

    private void postShare() {
        // layout_mask.setVisibility(View.VISIBLE);
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // layout_mask.setVisibility(View.GONE);
                findViewById(R.id.webview_comment).setVisibility(View.VISIBLE);
            }
        });
        shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void getMsgList(String pId) {
        if (!NetworkUtils.isNetworkConnected(ArticleDetailActivity.this)) {
            Toast.makeText(ArticleDetailActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", "get_post");
        map.put("post_id", pId);
        map.put("include", "id,title,modified,url,thumbnail,custom_fields,content");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME1_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ArticleDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String post = obj.getString("post");
                        if (StringUtils.isEquals(status, "ok")) { // 正确
                            if (StringUtils.isNotEmpty(post)) {
                                Gson gson = new Gson();
                                homePost = gson.fromJson(post, HomePost.class);
                                String a =homePost.getContent() .replaceAll("<a\\s+href\\s*=\\s*\".*?\">(.*?)</a>", "$1");

                                String temp = "<html><head><meta content='initial-scale=1.0, user-scalable=no'/><style>img{margin:10px;width:95%;height:auto !important;max-width:100%;max-height:auto;vertical-align:middle;}p{text-align:left;margin:10px;}</style><title>" +homePost.getTitle()+ "</title></head><body>" + a + "</body></html>";
                                String url = "<p>当年剧中的主演的现状又是怎样的呢？</p><p><img src='http://p3.pstatp.com/large/71e00015aeb216de9a6' img_width='640' img_height='506' alt='《情深深雨濛濛》3位主演相继去世，最小才33岁！看完我沉默了'onerror='javascript:errorimg.call(this);' ></p>"; 
                                webview.loadData(temp, "text/html; charset=UTF-8", null);// 设置中文乱码

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
                    UIUtils.showToast(ArticleDetailActivity.this, errorMsg);
                }
            }
        });
    }
}