package com.meijialife.simi.player;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunPlayAuth;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.meijialife.simi.R;
import com.ykcloud.sdk.opentools.player.VODPlayer;

public class PlayAliyunActivity extends AppCompatActivity {

    private String TAG = "PlayVodActivity";

    /*public static final String CONSTANCE_VID = "vid";
    public static final String CONSTANCE_CLIENT_ID = "client_id";
    public static final String CONSTANCE_CLIENT_SECRET = "client_secret";
    private String vid;*/

    private final String client_id = "199b3f31e08d160c";
    private final String client_secret = "08865c02e2f9dd9c7f11a72a02ddda9a";

    RelativeLayout layout_player;
    VODPlayer player;
    private AliyunVodPlayerView mAliyunVodPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_aliyun_vod);
//        parseExtra();
//        initView();
        //找到播放器对象
        mAliyunVodPlayerView = (AliyunVodPlayerView) findViewById(R.id.video_view);
    }

    private void parseExtra() {
        /*vid = getIntent().getStringExtra(CONSTANCE_VID);
        client_id = getIntent().getStringExtra(CONSTANCE_CLIENT_ID);
        client_secret = getIntent().getStringExtra(CONSTANCE_CLIENT_SECRET);*/
//        initSdk();
    }

//    private void initSdk() {
//        //初始化播放sdk
//        YKAPIFactory.initSDK(this, client_id, client_secret);
//    }

    public void playAliyunLocalSource(String videourl) {

        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(videourl);
        mAliyunVodPlayerView.setLocalSource(asb.build());
        mAliyunVodPlayerView.setTitleBarCanShow(false);
        mAliyunVodPlayerView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                mAliyunVodPlayerView.start();
            }
        });
    }

    public void playAliyunPlayAuth(String authInfo, String videoId) {

        //auth方式
        AliyunPlayAuth.AliyunPlayAuthBuilder aliyunPlayAuthBuilder = new AliyunPlayAuth.AliyunPlayAuthBuilder();
        aliyunPlayAuthBuilder.setVid(videoId);
        aliyunPlayAuthBuilder.setPlayAuth(authInfo);
        aliyunPlayAuthBuilder.setQuality(IAliyunVodPlayer.QualityValue.QUALITY_ORIGINAL);
        mAliyunVodPlayerView.setTitleBarCanShow(false);
        mAliyunVodPlayerView.setAuthInfo(aliyunPlayAuthBuilder.build());

        mAliyunVodPlayerView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                mAliyunVodPlayerView.start();
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {                //转为竖屏了。
                //显示状态栏
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                ViewGroup.LayoutParams aliVcVideoViewLayoutParams = mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWight(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                //设置为小屏状态
                mAliyunVodPlayerView.changeScreenMode(AliyunScreenMode.Small);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {                //转到横屏了。
                //隐藏状态栏
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
                //设置view的布局，宽高
                ViewGroup.LayoutParams aliVcVideoViewLayoutParams = mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                //设置为全屏状态
                mAliyunVodPlayerView.changeScreenMode(AliyunScreenMode.Full);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (player != null) {
//            player.destroyVideo();
//            player = null;
//        }

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClicked();
        }
        return false;
    }

    /**
     * 点击返回按钮
     */
    public void onBackClicked() {

        finish();
//        if (player != null) {
//            //获得vodPlayer横屏还是竖屏
//            int mScreenState = player.getScreenState();
//            if (mScreenState == VODPlayer.Orientation_portrait) {
//                //如果是竖屏 则退出播放器
//                finish();
//            } else {
//                //如果是横屏 则变为横屏
//                player.changeOrientation(VODPlayer.Orientation_portrait);
//            }
//        }
    }
}
