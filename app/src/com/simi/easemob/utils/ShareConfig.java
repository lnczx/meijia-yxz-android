package com.simi.easemob.utils;

import android.app.Activity;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareConfig {
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private Activity mContext;
    private static ShareConfig instance;
    private static  String card_id="";//卡片详情分享卡片Id
    private static String webUrl="";//webView分享的url

    public static ShareConfig getInstance() {
        if (instance == null) {
            instance = new ShareConfig();
        }
        return instance;
    }
    public static ShareConfig getInstance(String cardId) {
        if (instance == null) {
            instance = new ShareConfig();
        }
        card_id = cardId;
        return instance;
    }
 

    public void init(Activity context) {
        this.mContext = context;

        // 配置需要分享的相关平台
        configPlatforms();

        // 设置分享的内容
        setShareContent();
    }
    
    public void init(Activity context,String card_id) {
        this.mContext = context;

        // 配置需要分享的相关平台
        configPlatforms();
        // 设置分享的内容
        setShareContent(card_id);
    }
    public void inits(Activity context,String webUrl,String title, String imgUrl) {
        this.mContext = context;
        
        // 配置需要分享的相关平台
        configPlatforms();
        // 设置分享的内容
        setShareContents(webUrl,title, imgUrl);
    }
    public void init(Activity context,String uid,String invitation_code) {
        this.mContext = context;

        // 配置需要分享的相关平台
        configPlatforms();
        // 设置分享的内容
        setShareContent(uid,invitation_code);
    }
    public void setShareContents(String webUrl,String title, String imgUrl) {
        
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, "1104934408", "bRW2glhUCR6aJYIZ");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(Constants.SHARE_CONTENT);
        
        UMImage localImage = new UMImage(mContext, R.drawable.ic_launcher_logo);

        UMImage pImage = null;

        if (imgUrl != "") {
            pImage = new UMImage(mContext, imgUrl);
        }

        //微信好友
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(Constants.SHARE_CONTENT);
        weixinContent.setTitle(title);
        weixinContent.setTargetUrl(webUrl);
        if (pImage != null) {
            weixinContent.setShareMedia(pImage);
        } else {
            weixinContent.setShareMedia(localImage);
        }
        mController.setShareMedia(weixinContent);
        
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(Constants.SHARE_CONTENT);
        circleMedia.setTitle(title);
        if (pImage != null) {
            circleMedia.setShareMedia(pImage);
        } else {
            circleMedia.setShareMedia(localImage);
        }

        circleMedia.setTargetUrl(webUrl);
        mController.setShareMedia(circleMedia);
        
        
        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("【" + title + "】（分享自#菠萝HR#）");
        
        qzone.setTargetUrl(webUrl);
        qzone.setTitle("菠萝HR");
        qzone.setShareMedia(localImage);

        if (pImage != null) {
            qzone.setShareMedia(pImage);
        } else {
            qzone.setShareMedia(localImage);
        }

        mController.setShareMedia(qzone);



        //QQ好友
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(Constants.SHARE_CONTENT);
        qqShareContent.setTitle(title);
        if (pImage != null) {
            qqShareContent.setShareMedia(pImage);
        } else {
            qqShareContent.setShareMedia(localImage);
        }
        qqShareContent.setTargetUrl(webUrl);
        mController.setShareMedia(qqShareContent);



        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(title);
        sinaContent.setShareContent("【" + title + "】 " + webUrl + " (分享自@菠萝HR_人事行政神器)");
        sinaContent.setTargetUrl(webUrl);

        if (pImage != null) {
            sinaContent.setShareMedia(pImage);
        } else {
            sinaContent.setShareMedia(localImage);
        }
        mController.setShareMedia(sinaContent);
        
    }
    public void setShareContent(String uid,String invitation_code) {
        String targetUrl = "http://app.bolohr.com/simi-h5/show/company-join.html?uid="+uid+"&invitation_code="+invitation_code;
        String title = "好友邀请你加入团队";
        String content = "好友邀请你加入团队";
          // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, "1104934408", "bRW2glhUCR6aJYIZ");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(content);

        UMImage localImage = new UMImage(mContext, R.drawable.ic_launcher_logo);

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(content);
        weixinContent.setTitle(title);
        weixinContent.setTargetUrl(targetUrl);
        weixinContent.setShareMedia(localImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(content);
        circleMedia.setTitle(title);
        circleMedia.setShareMedia(localImage);
        circleMedia.setTargetUrl(targetUrl);
        mController.setShareMedia(circleMedia);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(content);

        qzone.setTargetUrl(targetUrl);
        qzone.setTitle(title);
        qzone.setShareMedia(localImage);
        mController.setShareMedia(qzone);

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(content);
        qqShareContent.setTitle(title);
        qqShareContent.setShareMedia(localImage);
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(title);
        sinaContent.setShareContent(content);
        sinaContent.setTargetUrl(targetUrl);
        sinaContent.setShareMedia(localImage);
        mController.setShareMedia(sinaContent);

    }
    public void setShareContent(String card_id) {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, "1104934408", "bRW2glhUCR6aJYIZ");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(Constants.SHARE_CONTENT);

        UMImage localImage = new UMImage(mContext, R.drawable.ic_launcher_logo);

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(Constants.SHARE_CONTENT);
        weixinContent.setTitle(Constants.SHARE_TITLE);
        weixinContent.setTargetUrl(Constants.SHARE_TARGET_URL+card_id);
//        weixinContent.setTargetUrl("http://bolohr.com/m/app");
        weixinContent.setShareMedia(localImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(Constants.SHARE_CONTENT);
        circleMedia.setTitle(Constants.SHARE_TITLE);
        circleMedia.setShareMedia(localImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
            circleMedia.setTargetUrl(Constants.SHARE_TARGET_URL+card_id);
//        circleMedia.setTargetUrl("http://bolohr.com/m/app");
        mController.setShareMedia(circleMedia);


        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(Constants.SHARE_CONTENT);
      
            qzone.setTargetUrl(Constants.SHARE_TARGET_URL+card_id);
//        qzone.setTargetUrl("http://bolohr.com/m/app");
        qzone.setTitle(Constants.SHARE_TITLE);
        qzone.setShareMedia(localImage);
        // qzone.setShareMedia(uMusic);
        mController.setShareMedia(qzone);

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(Constants.SHARE_CONTENT);
        qqShareContent.setTitle(Constants.SHARE_TITLE);
        qqShareContent.setShareMedia(localImage);
            qqShareContent.setTargetUrl(Constants.SHARE_TARGET_URL+card_id);
        mController.setShareMedia(qqShareContent);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(Constants.SHARE_TITLE);
        sinaContent.setShareContent(Constants.SHARE_CONTENT);
        sinaContent.setTargetUrl(Constants.SHARE_TARGET_URL+card_id);
        sinaContent.setShareMedia(localImage);
        mController.setShareMedia(sinaContent);

    }
    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    public void setShareContent() {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, "1104934408", "bRW2glhUCR6aJYIZ");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(Constants.SHARE_CONTENT);

        UMImage localImage = new UMImage(mContext, R.drawable.ic_launcher_logo);
//        UMImage urlImage = new UMImage(mContext, "http://www.umeng.com/images/pic/social/integrated_3.png");
        // UMImage resImage = new UMImage(mContext, R.drawable.icon);

        // UMEmoji emoji = new UMEmoji(mContext,
        // "http://www.pc6.com/uploadimages/2010214917283624.gif");
        // UMEmoji emoji = new UMEmoji(mContext,
        // "/storage/sdcard0/emoji.gif");

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(Constants.SHARE_CONTENT);
        weixinContent.setTitle(Constants.SHARE_TITLE);
        weixinContent.setTargetUrl(Constants.SHARE_CUSTOMER_TARGET_URL);
        
//        weixinContent.setTargetUrl("http://bolohr.com/m/app");
        weixinContent.setShareMedia(localImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(Constants.SHARE_CONTENT);
        circleMedia.setTitle(Constants.SHARE_TITLE);
        circleMedia.setShareMedia(localImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
            circleMedia.setTargetUrl(Constants.SHARE_CUSTOMER_TARGET_URL);
//        circleMedia.setTargetUrl("http://bolohr.com/m/app");
        mController.setShareMedia(circleMedia);


        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(Constants.SHARE_CONTENT);
            qzone.setTargetUrl(Constants.SHARE_CUSTOMER_TARGET_URL);
//        qzone.setTargetUrl("http://bolohr.com/m/app");
        qzone.setTitle(Constants.SHARE_TITLE);
        qzone.setShareMedia(localImage);
        // qzone.setShareMedia(uMusic);
        mController.setShareMedia(qzone);

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(Constants.SHARE_CONTENT);
        qqShareContent.setTitle(Constants.SHARE_TITLE);
        qqShareContent.setShareMedia(localImage);
            qqShareContent.setTargetUrl(Constants.SHARE_CUSTOMER_TARGET_URL);
        mController.setShareMedia(qqShareContent);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(Constants.SHARE_TITLE);
        sinaContent.setShareContent(Constants.SHARE_CONTENT);
        qqShareContent.setTargetUrl(Constants.SHARE_CUSTOMER_TARGET_URL);
        sinaContent.setShareMedia(localImage);
        mController.setShareMedia(sinaContent);

    }

    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }

    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx93aa45d30bf6cba3";
        String appSecret = "7a4ec42a0c548c6e39ce9ed25cbc6bd7";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary, image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title : 要分享标题
     *       summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
        String appId = "1104934408";
        String appKey = "bRW2glhUCR6aJYIZ";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, appId, appKey);
        qqSsoHandler.setTargetUrl("http://bolohr.com/m/app");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

}
