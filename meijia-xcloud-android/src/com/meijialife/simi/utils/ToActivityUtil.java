package com.meijialife.simi.utils;

import android.content.Context;
import android.content.Intent;

import com.easemob.easeui.EaseConstant;
import com.meijialife.simi.activity.MainFriendsActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.activity.WebViewPartnerActivity;
import com.simi.easemob.ui.ChatActivity;

import org.bitlet.weupnp.Main;

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
     * 跟客服聊天
     *
     * @param mContext
     */

    public static void gotoSimiChat(Context mContext) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, "simi-user-366");
        intent.putExtra(EaseConstant.EXTRA_USER_NAME, "云小秘");
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


}
