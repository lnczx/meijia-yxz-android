package com.meijialife.simi.utils;

import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.activity.LoginActivity;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;

/**
 * 自定义实现登录系统,继承自AbsLoginImpl
 */
public class SimpleLoginImpl extends AbsLoginImpl {
    @Override
    protected void onLogin(Context context, final LoginListener listener) {
        LoginActivity.sLoginListener = listener;
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}
