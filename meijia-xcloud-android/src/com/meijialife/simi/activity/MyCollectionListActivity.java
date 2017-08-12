package com.meijialife.simi.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.ParamsBean;
import com.meijialife.simi.bean.VideoChannel;
import com.meijialife.simi.fra.MyCollectionNewsFra;
import com.meijialife.simi.fra.MyCollectionVideoFra;
import com.meijialife.simi.inter.ListItemClickHelps;
import com.meijialife.simi.ui.IndicatorTabBarForTrial;

import java.util.ArrayList;
import java.util.List;

/**
 *  我的收藏
 */
public class MyCollectionListActivity extends FragmentActivity implements OnClickListener, ListItemClickHelps {

    private IndicatorTabBarForTrial mIndicatorTabBar;
    private List<VideoChannel> channels;
    private VideoChannel currentChannel;//当前选中的频道

    private FragmentManager mFM = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_collection);
        super.onCreate(savedInstanceState);

        initView();
        getChannelList();

        change(new MyCollectionNewsFra());
    }

    private void initView() {
        mIndicatorTabBar = (IndicatorTabBarForTrial) findViewById(R.id.m_trial_tabBar);
        mIndicatorTabBar.setCallBack(this);

        findViewById(R.id.title_btn_left).setVisibility(View.VISIBLE);
        findViewById(R.id.title_btn_left).setOnClickListener(this);
        TextView tvHeader = (TextView) findViewById(R.id.header_tv_name);
        tvHeader.setText("我的收藏");

    }

    public void getChannelList() {
        channels = new ArrayList<>();
        channels.add(new VideoChannel("0", "文章"));
        channels.add(new VideoChannel("1", "视频"));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(ParamsBean params, int index, boolean flag) {
        currentChannel = channels.get(index);
        if(currentChannel.getChannel_id().equals("0")){
            //文章
            change(new MyCollectionNewsFra());
        }else{
            //视频
            change(new MyCollectionVideoFra());
        }
    }

}