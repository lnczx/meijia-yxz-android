package com.meijialife.simi.utils;

import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.Constants;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.database.DBHelper;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;

/**
 * 自定义实现登录系统,继承自AbsLoginImpl
 * add by ye 2016/08/31
 */
public class SimpleLoginImpl extends AbsLoginImpl implements Loginable {

    @Override
    protected void onLogin(Context context, final LoginListener listener) {
        LoginActivity.sLoginListener = listener;

        boolean is_login = SpFileUtil.getBoolean(context, SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (!is_login) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } else {
            String userId = DBHelper.getUser(context).getId();
            String nickName = DBHelper.getUser(context).getName();

            CommUser loginedUser = new CommUser(userId);
            loginedUser.name = nickName;
            loginedUser.source = Source.SELF_ACCOUNT;

            //写入缓存保存登录信息
//             AppContext.getInstance().setProperty("user_isLogin", "true");
            // 登录完成回调给社区SDK
            listener.onComplete(200, loginedUser);
        }
    }

    @Override
    protected void onLogout(Context context, LoginListener loginListener) {
        super.onLogout(context, loginListener);
        loginListener.onComplete(200, null);

    }

//    @Override
//    protected boolean isLogined(Context context) {
//        //读取缓存判断是否登录
//        String user_isLogin = AppContext.getInstance().getProperty("user_isLogin");
//        if (StringUtils.isEmpty(user_isLogin)) {
//            user_isLogin = "false";
//        }
//        isLogin = Boolean.parseBoolean(user_isLogin);
//        return isLogin;
//    }

}
