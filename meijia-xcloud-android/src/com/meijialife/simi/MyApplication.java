package com.meijialife.simi;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.igexin.sdk.PushManager;
import com.simi.easemob.EMDemoHelper;

public class MyApplication extends MultiDexApplication {

    private static final String TAG = "MyApplication";

    public static Context applicationContext;
    private static MyApplication instance;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;

        // init demo helper
        EMDemoHelper.getInstance().init(applicationContext);

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        PushManager.getInstance().initialize(this.getApplicationContext());
    
    
        ///初始化异常报告
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());


        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);

    }

    public static MyApplication getInstance() {
        return instance;
    }

}
