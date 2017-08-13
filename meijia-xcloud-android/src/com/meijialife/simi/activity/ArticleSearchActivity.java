package com.meijialife.simi.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.ParamsBean;
import com.meijialife.simi.bean.VideoChannel;
import com.meijialife.simi.fra.SearchFeedFra;
import com.meijialife.simi.fra.SearchNewsFra;
import com.meijialife.simi.fra.SearchServiceFra;
import com.meijialife.simi.fra.SearchVideoFra;
import com.meijialife.simi.inter.ListItemClickHelps;
import com.meijialife.simi.ui.IndicatorTabBarForTrial;
import com.meijialife.simi.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description：发现---实现搜索列表展现和热搜词搜索功能
 * @author： kerryg
 * @date:2015年12月5日
 */
public class ArticleSearchActivity extends FragmentActivity implements ListItemClickHelps {

    private TextView tv_search;// 搜索按钮
    private EditText et_search_kw;// 编辑框

    private IndicatorTabBarForTrial mIndicatorTabBar;
    private List<VideoChannel> channels;
    private VideoChannel currentChannel;//当前选中的频道

    private FragmentManager mFM = null;

    private SearchNewsFra mNewsFra;
    private SearchVideoFra mVideoFra;
    private SearchFeedFra mFeedFra;
    private SearchServiceFra mServiceFra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.article_search_activity);
        super.onCreate(savedInstanceState);

        initView();
        getChannelList();

        mNewsFra = new SearchNewsFra();
        change(mNewsFra);
    }

    private void initView() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        et_search_kw = (EditText) findViewById(R.id.et_search_kw);
        // 搜索增加点击事件
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String kw = et_search_kw.getText().toString();
                if (!StringUtils.isEmpty(kw)) {
                    toSearch(kw);
                } else {
                    et_search_kw.setHint("请输入搜索内容");
                    return;
                }
            }
        });

        mIndicatorTabBar = (IndicatorTabBarForTrial) findViewById(R.id.m_trial_tabBar);
        mIndicatorTabBar.setCallBack(this);
    }

    public void getChannelList() {
        channels = new ArrayList<>();
        channels.add(new VideoChannel("0", "文章"));
        channels.add(new VideoChannel("1", "视频"));
        channels.add(new VideoChannel("2", "问答"));
        channels.add(new VideoChannel("3", "服务"));
        updateChannel(channels);
        currentChannel = channels.get(0);
    }

    /**
     * 更新顶部频道
     */
    private void updateChannel(List<VideoChannel> channels){
        mIndicatorTabBar.setMaxColumn(channels.size());
        mIndicatorTabBar.initViewForTrial(channels);
    }

    /**
     * 切换fragement
     */
    private void change(Fragment fragment) {
        if (null == mFM)
            mFM = getSupportFragmentManager();
        FragmentTransaction ft = mFM.beginTransaction();
        ft.replace(R.id.content_container, fragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toSearch(String kw){
        if(currentChannel.getChannel_id().equals("0")){
            //文章
            mNewsFra.search(kw);
        }else if(currentChannel.getChannel_id().equals("1")){
            //视频
            mVideoFra.search(kw);
        }else if(currentChannel.getChannel_id().equals("2")){
            //问答
        }else if(currentChannel.getChannel_id().equals("3")){
            //服务
        }
    }

    @Override
    public void onClick(ParamsBean params, int index, boolean flag) {
        currentChannel = channels.get(index);
        if(currentChannel.getChannel_id().equals("0")){
            //文章
            if(mNewsFra == null){
                mNewsFra = new SearchNewsFra();
            }
            change(mNewsFra);
        }else if(currentChannel.getChannel_id().equals("1")){
            //视频
            if(mVideoFra == null){
                mVideoFra = new SearchVideoFra();
            }
            change(mVideoFra);
        }else if(currentChannel.getChannel_id().equals("2")){
            //问答
            if(mFeedFra == null){
                mFeedFra = new SearchFeedFra();
            }
            change(mFeedFra);
        }else if(currentChannel.getChannel_id().equals("3")){
            //服务
            if(mServiceFra == null){
                mServiceFra = new SearchServiceFra();
            }
            change(mServiceFra);
        }
    }
}
