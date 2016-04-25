package com.easemob.easeui.utils;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.Constant;
import com.easemob.easeui.R;
import com.easemob.easeui.bean.SimiUser;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.database.DBHelper;
import com.easemob.easeui.domain.EaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && !TextUtils.isEmpty(user.getAvatar())){//modify by garry
            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
                Picasso.with(context).load(user.getAvatar()).into(imageView);
            } catch (Exception e) {
                //正常的string路径
//                Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.ease_default_avatar).into(imageView);
            	Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
    /**
     * 设置用户头像
     * 
     * by garry
     */
    public static void setUserAvatar(Context context, EaseUser user, ImageView imageView){
        if(user != null && !TextUtils.isEmpty(user.getAvatar())){//modify by garry
            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
            	Picasso.with(context).load(user.getAvatar()).into(imageView);
            } catch (Exception e) {
                //正常的string路径
//                Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.ease_default_avatar).into(imageView);
            	Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);//modify by garry
            }
        }else{
            Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置用户头像 和 昵称
     * 
     * by garry
     */
    public static void setUserAvatarAndNick(Context context, String username, ImageView imageView, TextView textView){
    	SimiUser simiUser = DBHelper.getInstance(context).getSimiUserInfo(context, username);
    	
		if (simiUser != null) {
			// //如果有缓存，直接显示
			EaseUser user = new EaseUser(username);
			user.setNick(simiUser.getName());
			user.setAvatar(simiUser.getHead_img());
			if (imageView != null) {
				setUserAvatar(context, user, imageView);
			}
			if (textView != null) {
				setUserNick(user.getNick(), textView);
			}
		}else{
    		//没有缓存，去后台获取
    		getSimiUserInfo(context, username, imageView, textView);
    	}
    }
    
    /**
     * 根据环信id，在后台库中获取用户头像和昵称
     * 
     * by garry
     * 
     * @param username 环信id
     */
    private static void getSimiUserInfo(final Context context, final String username, final ImageView imageView, final TextView textView) {
//        if (!NetworkUtils.isNetworkConnected(appContext)) {
//            Toast.makeText(appContext, appContext.getString(R.string.net_not_open), 0).show();
//            return;
//        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("im_username", username);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constant.URL_GET_IM_PROFILE, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                Log.i("========", "onSuccess：" + t);
                try {
                    if (!TextUtils.isEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == 0) { // 正确
                            if(!TextUtils.isEmpty(data)){
                                Gson gson = new Gson();
                                SimiUser simiUser = gson.fromJson(data, SimiUser.class);
                                simiUser.setId(simiUser.getUser_id());
                                DBHelper.getInstance(context).add(simiUser, simiUser.getId());
                            	
                              //设置显示环信头像和昵称
                                EaseUser user = new EaseUser(username);
//                                user.setNick("Garry");
//                                user.setAvatar("http://img4.imgtn.bdimg.com/it/u=1519979105,1747027397&fm=21&gp=0.jpg");
                                user.setNick(simiUser.getName());
                                user.setAvatar(simiUser.getHead_img());
                                if(imageView != null){
                                	setUserAvatar(context, user, imageView);
                                }
                                if(textView != null){
                                	setUserNick(user.getNick(), textView);
                                }
                            }else{
                            	//默认头像和昵称
//                            	if(imageView != null){
//                            		Picasso.with(context).load(R.drawable.ease_default_avatar).into(imageView);
//                            	}
//                            	if(textView != null){
//                            		textView.setText(user.getNick());
//                            	}
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    
}
