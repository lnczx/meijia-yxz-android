package com.meijialife.simi.utils;

import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.activity.WebViewActivity;

/**
 * activity跳转类
 *
 * @author ye
 */
public class ToActivityUtil {
    public static void gotoWebPage(Context mContext, String title, String url) {
        if (null == mContext) return;
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }


}
