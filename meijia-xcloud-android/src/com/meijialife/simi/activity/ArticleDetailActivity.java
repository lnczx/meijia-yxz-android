package com.meijialife.simi.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.easeui.EaseConstant;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AddressListAdapter;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.AddressData;
import com.meijialife.simi.bean.Categories;
import com.meijialife.simi.bean.CustomFields;
import com.meijialife.simi.bean.HomeAboutNewsBean;
import com.meijialife.simi.bean.HomePost;
import com.meijialife.simi.bean.HomePostes;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.PopupMenu;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.ToActivityUtil;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.utils.Utils;
import com.simi.easemob.ui.ChatActivity;
import com.simi.easemob.utils.ShareConfig;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 首页文章详情
 */
public class ArticleDetailActivity extends BaseActivity implements OnClickListener {

    private View layout_mask;// 遮罩
    private ImageView m_iv_article;//图片显示

    // 下面评论按钮
    private LinearLayout webview_comment;
    private boolean is_show = false;// 是否显示底部评论按钮

    private EditText comment_content;// 评论输入框
    private Button m_btn_confirm;// 发表评论按钮
    private ImageView m_iv_zan;// 点赞
    private String pId;// 文章Id
    private String url;
    private String from;

    private HomePost homePost;
    private HomePostes homePostes;
    private String pTitle = "";// 文章标题
    private String pImgUrl = ""; //文章题图

    private TextView m_tv_article_title;//文章标题
    private TextView m_article_content;//文章内容
    private TextView m_tv_from_name;//文章来源
    private TextView m_tv_update_time;//更新时间


    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private View view;

    private LinearLayout layout_richtext;
    private RelativeLayout layout_more_news;
    private TextView txt_content_title;
    private TextView txt_publish_from;
    private TextView txt_publish_source;
    private SimpleDraweeView thumbnail_images;
    private ScrollView layout_new_show_data;
    public static final String fromTrial = "fromTrial";
    private TextView tv_person_title;
    private String title;

    private String newsUrl;
    private ListView myListview;
    private CategoriesListAdapter categoriesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article_detail);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        initView();

    }

    private void initView() {
        setTitleName("详情");

        url = getIntent().getStringExtra("url");
        pId = getIntent().getStringExtra("p_id");
        is_show = getIntent().getBooleanExtra("is_show", false);
        requestBackBtn();

        getMsgList(pId);
        setOnClick();


        layout_richtext = (LinearLayout) findViewById(R.id.layout_news_richtext);
        txt_content_title = (TextView) findViewById(R.id.txt_content_title);
        txt_publish_from = (TextView) findViewById(R.id.txt_publish_from);
        txt_publish_source = (TextView) findViewById(R.id.txt_publish_source);
        tv_person_title = (TextView) findViewById(R.id.tv_person_title);
        thumbnail_images = (SimpleDraweeView) findViewById(R.id.thumbnail_images);
        layout_new_show_data = (ScrollView) findViewById(R.id.layout_new_show_data);

        myListview = (ListView) findViewById(R.id.listview);
        layout_more_news = (RelativeLayout) findViewById(R.id.layout_more_news);

        categoriesListAdapter = new CategoriesListAdapter(this);
        myListview.setAdapter(categoriesListAdapter);


//        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
//        iv_person_close = (ImageView) findViewById(R.id.iv_person_close);
//        iv_person_left.setOnClickListener(this);
//        iv_person_close.setOnClickListener(this);

    }

    private void setOnClick() {

        comment_content = (EditText) findViewById(R.id.comment_content);
        m_btn_confirm = (Button) findViewById(R.id.m_btn_confirms);
        m_iv_zan = (ImageView) findViewById(R.id.m_iv_zan);
        m_tv_article_title = (TextView) findViewById(R.id.m_tv_article_title);
        m_article_content = (TextView) findViewById(R.id.m_article_content);
        m_tv_from_name = (TextView) findViewById(R.id.m_tv_from_name);
        m_tv_update_time = (TextView) findViewById(R.id.m_tv_update_time);
        findViewById(R.id.m_btn_send_comment).setOnClickListener(this);
        findViewById(R.id.m_btn_confirms).setOnClickListener(this);
        findViewById(R.id.layout_mask).setOnClickListener(this);

        findViewById(R.id.m_iv_comment).setOnClickListener(this);
        findViewById(R.id.m_iv_zan).setOnClickListener(this);
        findViewById(R.id.m_iv_share).setOnClickListener(this);
        comment_content.addTextChangedListener(new TextWatcher() {
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
        });
        boolean is_login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (is_login) {
            getZan();// 是否点赞接口
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
                ShareConfig.getInstance().inits(ArticleDetailActivity.this, url, pTitle, pImgUrl);
                postShare();
                break;
            case R.id.iv_person_close: // 返回
                finish();
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
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
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
     * * 发表评论接口
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
        showDialog();
        if (!NetworkUtils.isNetworkConnected(ArticleDetailActivity.this)) {
            Toast.makeText(ArticleDetailActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", "get_post");
        map.put("post_id", pId);
        map.put("include", "id,title,modified,url,thumbnail,custom_fields,content,categories");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME1_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ArticleDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                System.out.println("webview:" + t.toString());
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String post = obj.getString("post");
                        if (StringUtils.isEquals(status, "ok")) { // 正确
                            if (StringUtils.isNotEmpty(post)) {
                                Gson gson = new Gson();
                                homePostes = gson.fromJson(post, HomePostes.class);
                                title = homePostes.getTitle();
                                layout_new_show_data.setVisibility(View.VISIBLE);
                                fillContent(homePostes);//全新解析的方式  by andye 2016/07/22
                                List<Categories> categories = homePostes.getCategories();

                                if (null != categories && categories.size() > 0) {
                                    layout_more_news.setVisibility(View.VISIBLE);
                                    getAboutNewsList(categories.get(0).getId());
                                } else {
                                    layout_more_news.setVisibility(View.GONE);
                                }
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


    /**
     * 获取相关文章接口
     */
    public void getAboutNewsList(String id) {
        if (!NetworkUtils.isNetworkConnected(ArticleDetailActivity.this)) {
            Toast.makeText(ArticleDetailActivity.this, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", "get_category_posts");
        map.put("id", id);
        map.put("order", "DESC");
        map.put("count", "5");
        map.put("page", "1");
        map.put("include", "id,title,modified,url,thumbnail,custom_fields");
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
                        Gson gson = new Gson();
                        HomeAboutNewsBean homeAboutNewsBean = gson.fromJson(t.toString(), HomeAboutNewsBean.class);
                        List<HomeAboutNewsBean.PostsBean> postsBeen = homeAboutNewsBean.getPosts();
                        if (null != postsBeen && postsBeen.size() > 0) {
                            categoriesListAdapter.setData(postsBeen);
                        }

                    } else {
                        errorMsg = getString(R.string.servers_error);
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

    /**
     * 填充数据
     *
     * @param homePostes
     */
    private void fillContent(HomePostes homePostes) {
        pTitle = homePostes.getTitle();
        txt_content_title.setText(homePostes.getTitle());
        txt_publish_from.setText(homePostes.getCustomFields().getFromnameValue().get(0));
        txt_publish_source.setText(homePostes.getModified());

        try {
            pImgUrl = homePostes.getThumbnailImages().getFull().getUrl();
            thumbnail_images.setImageURI(Uri.parse(pImgUrl));
        } catch (Exception e) {
        }
        String content = homePostes.getContent();
        if (StringUtils.isNotEmpty(content)) {
            gennerateContent(content);
        }
    }

    //解析html数据
    private void gennerateContent(String richtext) {
        Document doc = Jsoup.parse(richtext);
        Elements elements = doc.getElementsByTag("p");
        for (Element element : elements) {
            Elements childElements = element.getElementsByTag("img");
            if (childElements != null && childElements.size() > 0) {
                //图片标签
                for (Element ele : childElements) {
                    Attributes attributes = ele.attributes();
                    String imgName = attributes.get("title");
                    String imgUri = attributes.get("src");
//                    String width = attributes.get("data-width");
//                    String height = attributes.get("data-height");
                    String height = "190";
                    String width = "330";
                    String style = attributes.get("style");

                    String widthRegex = "width: (.*)px; height:";
                    Pattern pattern = Pattern.compile(widthRegex);
                    Matcher matcher = pattern.matcher(style);
                    while (matcher.find()) {
                        width = matcher.group(1) + "";
                    }
                    String highRegex = "height: (.*)px";
                    Pattern pattern2 = Pattern.compile(highRegex);
                    Matcher matcher2 = pattern2.matcher(style);
                    while (matcher2.find()) {
                        height = matcher2.group(1) + "";
                    }

                    //判断是否为Gif
                    if (imgUri.endsWith("gif")) {
                        addGif(imgUri, imgName, Integer.valueOf(width), Integer.valueOf(height));
                    } else {
                        addImage(imgUri, imgName, Integer.valueOf(width), Integer.valueOf(height));

                    }
                }
                continue;
            }
            //文字部分 手工解析a标签 其余交给Html类处理

            List<Node> list = element.childNodes();
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
            for (Node childNode : list) {
                if (childNode instanceof Element && "a".equals(((Element) childNode).tagName())) {
                    String url = childNode.attr("href");
                    if (url != null) {
                        int a = ssBuilder.length();
                        ssBuilder.append(((Element) childNode).text());
                        ssBuilder.setSpan(new MyURLSpan(url), a, ssBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                } else {
                    ssBuilder.append(Html.fromHtml(childNode.outerHtml()));
                }
            }

            addText(ssBuilder);

        }


    }

    private void addText(CharSequence text) {
        TextView textview = (TextView) View.inflate(this, R.layout.richtext_txt_layout, null);
        textview.setText(text);
        if (text instanceof Spanned) {
            textview.setMovementMethod(LinkMovementMethod.getInstance());
        }
        layout_richtext.addView(textview);

    }

    //普通文本
    private void addNormalText(String text) {
        TextView textview = (TextView) View.inflate(this, R.layout.richtext_txt_layout, null);
        textview.setText(text);
        layout_richtext.addView(textview);

    }

    private void addImage(String imageUri, String imgName, int width, int height) {
        View view = View.inflate(this, R.layout.richtext_img_layout, null);
        SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.img);
//        TextView textview = (TextView) view.findViewById(R.id.img_name);
//        if (!TextUtils.isEmpty(imgName)) {
//            textview.setText(imgName);
//        } else {
//            textview.setVisibility(View.GONE);
//        }
        //设置imageview的长宽
        int showWidth;
        int showHeight;

        int deviceWidth = Utils.getWidth(this) - Utils.dip2px(this, 28);
        int imageWidth = Utils.dip2px(this, width);
        if (deviceWidth > imageWidth) {
            //原尺寸显示
            showWidth = imageWidth;
            showHeight = Utils.dip2px(this, height);
        } else {
            showWidth = deviceWidth;
            showHeight = (int) (height * 1.0 * deviceWidth / width);
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) img.getLayoutParams();
        params.width = showWidth;
        params.height = showHeight;
        img.setLayoutParams(params);
        img.setImageURI(Uri.parse(imageUri));

        layout_richtext.addView(view);

    }

    private void addGif(String imageUri, String imgName, int width, int height) {
        View view = View.inflate(this, R.layout.richtext_gif_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.img_name);
        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.img);

//        if (!TextUtils.isEmpty(imgName)) {
//            textView.setText(imgName);
//        } else {
//            textView.setVisibility(View.GONE);
//        }

        int showWidth;
        int showHeight;

        int deviceWidth = Utils.getWidth(this) - Utils.dip2px(this, 28);
        int imageWidth = Utils.dip2px(this, width);
        if (deviceWidth > imageWidth) {
            //原尺寸显示
            showWidth = imageWidth;
            showHeight = Utils.dip2px(this, height);
        } else {
            showWidth = deviceWidth;
            showHeight = (int) (height * 1.0 * deviceWidth / width);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.width = showWidth;
        params.height = showHeight;
        imageView.setLayoutParams(params);
        imageView.setAspectRatio(showWidth / showHeight);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(imageUri))
                .setAutoPlayAnimations(true)
                .build();
        imageView.setController(controller);

        layout_richtext.addView(view);
    }

    private class MyURLSpan extends ClickableSpan {
        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            ToActivityUtil.gotoWebPage(ArticleDetailActivity.this, "文章详情", mUrl);
        }
    }


    class CategoriesListAdapter extends BaseAdapter {

        private Context context;
        private List<HomeAboutNewsBean.PostsBean> datas;

        private LayoutInflater layoutInflater;

        public CategoriesListAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.datas = new ArrayList<HomeAboutNewsBean.PostsBean>();

            finalBitmap = FinalBitmap.create(context);
            //设置缓存路径和缓存大小
            finalBitmap.configDiskCachePath(context.getFilesDir().toString());
            finalBitmap.configDiskCacheSize(1024 * 1024 * 10);
            defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        }

        public void setData(List<HomeAboutNewsBean.PostsBean> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public HomeAboutNewsBean.PostsBean getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.home_list_item, null);//
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.msg_tv_title);
                holder.tv_summary = (TextView) convertView.findViewById(R.id.msg_tv_summary);
                holder.iv_iconUrl = (ImageView) convertView.findViewById(R.id.msg_iv_icon_url);
                holder.layout_home_item = (LinearLayout) convertView.findViewById(R.id.layout_home_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final HomeAboutNewsBean.PostsBean categories = getItem(position);
            holder.tv_title.setText(categories.getTitle());
            holder.tv_summary.setText(categories.getCustom_fields().getViews().get(0) + "人已看过");
            finalBitmap.display(holder.iv_iconUrl, categories.getThumbnail(), defDrawable.getBitmap(), defDrawable.getBitmap());
            holder.layout_home_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToActivityUtil.gotoArticleDetailActivity(ArticleDetailActivity.this, categories.getUrl(), categories.getId(), null);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_title; //标题
            TextView tv_summary; //摘要
            ImageView iv_iconUrl; //图片
            LinearLayout layout_home_item;

        }


    }

}