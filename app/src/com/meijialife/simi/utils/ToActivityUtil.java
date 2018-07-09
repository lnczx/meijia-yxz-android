package com.meijialife.simi.utils;

import android.content.Context;
import android.content.Intent;

import com.easemob.easeui.EaseConstant;
import com.meijialife.simi.activity.ArticleDetailActivity;
import com.meijialife.simi.activity.MainFriendsActivity;
import com.meijialife.simi.activity.PartnerActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.activity.WebViewDetailsActivity;
import com.simi.easemob.ui.ChatActivity;

/**
 * activity跳转类
 *
 * @author ye
 */
public class ToActivityUtil {
    /**
     * 打开网页
     *
     * @param mContext
     * @param title
     * @param url
     */
    public static void gotoWebPage(Context mContext, String title, String url) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }

    /**
     * 打开vip咨询的网页
     *
     * @param mContext
     * @param title
     * @param url
     */
    public static void gotoImWebPage(Context mContext, String title, String url, boolean isVip) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, WebViewDetailsActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("isVip", isVip);
        mContext.startActivity(intent);
    }

    /**
     * 会员购买页面
     *
     * @param mContext
     */
    public static void gotoVipWebPage(Context mContext) {
        if (null == mContext) return;
        Intent intent2 = new Intent(mContext, PartnerActivity.class);
        intent2.putExtra("partner_user_id", "987");
        intent2.putExtra("service_type_id", "306");
        mContext.startActivity(intent2);
    }

    /**
     * 跟客服聊天
     *
     * @param mContext
     */

    public static void gotoSimiChat(Context mContext) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, "simi-user-366");
        intent.putExtra(EaseConstant.EXTRA_USER_NAME, "菠萝小秘");
        mContext.startActivity(intent);
    }

    /**
     * 去好友页面
     *
     * @param mContext
     */

    public static void gotoFriendsActivity(Context mContext, String type) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, MainFriendsActivity.class);
        intent.putExtra(MainFriendsActivity.TYPE, type);
        mContext.startActivity(intent);
    }

    /**
     * 去文章详情页面
     *
     * @param mContext
     */

    public static void gotoArticleDetailActivity(Context mContext, String url, String id, String Contents) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("p_id", id);// 文章Id
        intent.putExtra("is_show", true);
//        intent.putExtra("home_post", homePost);
        intent.putExtra("article_content", Contents);
        mContext.startActivity(intent);
    }


}
