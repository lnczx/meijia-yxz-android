package com.meijialife.simi.player.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.R;
import com.meijialife.simi.utils.StringUtils;

public class WebViewFragment extends BaseFragment {

    public static final String INTENT_KEY_URL = "url";
    private String url;

    private WebView webview;
    private ProgressBar mProgressBar; //webView进度条

    public static WebViewFragment getInstace(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = View.inflate(getActivity(),
                R.layout.fragment_webview, null);
        if (getArguments() != null) {
            url = getArguments().getString(INTENT_KEY_URL);
        }

        initView(mView);
        initWebView(mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view){
        mProgressBar = view.findViewById(R.id.myProgressBar);
    }

    /**
     * 初始化webview
     *
     * @param rootView
     */
    @SuppressLint("JavascriptInterface")
    private void initWebView(View rootView) {
        if(StringUtils.isEmpty(url)){
            return;
        }
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
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
        };

        webview = rootView.findViewById(R.id.webview);
        webview.setWebChromeClient(wvcc);//负责显示页面title
        webview.loadUrl(url);
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);// 是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);// DOM Storage
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setInitialScale(100);
        if (Build.VERSION.SDK_INT >= 21) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //添加打电话和发短信的意图，拦截网页事件
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (StringUtils.isEmpty(url)) {
                    return true;
                }
                Uri uri = Uri.parse(url);
                if (url.startsWith("tel:") || url.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.startsWith("sms")) {
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(sendIntent);
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }


}