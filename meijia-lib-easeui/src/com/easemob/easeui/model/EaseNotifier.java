/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.easemob.easeui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.NotificationCompat;
import com.easemob.easeui.Constant;
import com.easemob.easeui.bean.SimiUser;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseSettingsProvider;
import com.easemob.easeui.database.DBHelper;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.google.gson.Gson;

/**
 * 新消息提醒class
 * 2.1.8把新消息提示相关的api移除出sdk，方便开发者自由修改
 * 开发者也可以继承此类实现相关的接口
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class EaseNotifier {
    private final static String TAG = "notify";
    Ringtone ringtone = null;

    protected final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
                                                "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
                                              };
    protected final static String[] msg_ch = { "发来一条消息", "发来一张图片", "发来一段语音", "发来位置信息", "发来一个视频", "发来一个文件",
                                               "%1个联系人发来%2条消息"
                                             };

    protected static int notifyID = 0525; // start notification id
    protected static int foregroundNotifyID = 0555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;

    protected Context appContext;
    protected String packageName;
    protected String[] msgs;
    protected long lastNotifiyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    protected EaseNotificationInfoProvider notificationInfoProvider;

    public EaseNotifier() {
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     * @param context
     * @return
     */
    public EaseNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            msgs = msg_ch;
        } else {
            msgs = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        
        return this;
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }
    
    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    /**
     * 处理新收到的消息，然后发送通知
     * 
     * 开发者可以重载此函数
     * this function can be override
     * 
     * @param message
     */
    public synchronized void onNewMsg(EMMessage message) {
        if(EMChatManager.getInstance().isSlientMessage(message)){
            return;
        }
        EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
        if(!settingsProvider.isMsgNotifyAllowed(message)){
            return;
        }
        
        // 判断app是否在后台
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            sendNotification(message, true);

        }
        
        viberateAndPlayTone(message);
    }
    
    public synchronized void onNewMesg(List<EMMessage> messages) {
        if(EMChatManager.getInstance().isSlientMessage(messages.get(messages.size()-1))){
            return;
        }
        EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
        if(!settingsProvider.isMsgNotifyAllowed(null)){
            return;
        }
        // 判断app是否在后台
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(messages, false);
        } else {
            sendNotification(messages, true);
        }
        viberateAndPlayTone(messages.get(messages.size()-1));
    }

    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     * @param messages
     * @param isForeground
     */
    protected void sendNotification (List<EMMessage> messages, boolean isForeground){
        for(EMMessage message : messages){
            if(!isForeground){
                notificationNum++;
                fromUsers.add(message.getFrom());
            }
        }
        sendNotification(messages.get(messages.size()-1), isForeground, false, null);
    }
    
    protected void sendNotification (EMMessage message, boolean isForeground){
    	String username = message.getFrom();
    	//add by garry 用来显示新消息的发送人昵称
    	SimiUser simiUser = DBHelper.getInstance(appContext).getSimiUserInfo(appContext, username);
    	if(simiUser != null){
    		//如果本地有该用户缓存数据，直接推送
    		sendNotification(message, isForeground, true, simiUser);
    	}else{
    		//如果没有数据，则在后台获取昵称后再推送
    		getSimiUserInfo(appContext, message, isForeground, true);
    	}
    }
    
    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     * @param message
     * 
     * @param simiUser 用户信息字段，add by garry
     */
    protected void sendNotification(EMMessage message, boolean isForeground, boolean numIncrease, SimiUser simiUser) {
        String username = message.getFrom();
        try {
            String notifyText = username + " ";
            switch (message.getType()) {
            case TXT:
                notifyText += msgs[0];
                break;
            case IMAGE:
                notifyText += msgs[1];
                break;
            case VOICE:

                notifyText += msgs[2];
                break;
            case LOCATION:
                notifyText += msgs[3];
                break;
            case VIDEO:
                notifyText += msgs[4];
                break;
            case FILE:
                notifyText += msgs[5];
                break;
            }
            
            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
            
            // notification titile
            String contentTitle = appname;
            if (notificationInfoProvider != null) {
                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customCotentTitle = notificationInfoProvider.getTitle(message);
                if (customNotifyText != null){
                    // 设置自定义的状态栏提示内容
                    notifyText = customNotifyText;
                }
                    
                if (customCotentTitle != null){
                    // 设置自定义的通知栏标题
                    contentTitle = customCotentTitle;
                }   
            }
            
            // ================add by garry 设置状态栏的消息提示============
            String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
            if(message.getType() == Type.TXT){
                ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
            }
            if(simiUser != null){
            	notifyText = simiUser.getName() + ": " + ticker;
            }
            // ================add by garry end============================
                

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (notificationInfoProvider != null) {
                // 设置自定义的notification点击跳转intent
                msgIntent = notificationInfoProvider.getLaunchIntent(message);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(numIncrease){
                // prepare latest event info section
                if(!isForeground){
                    notificationNum++;
                    fromUsers.add(message.getFrom());
                }
            }

            int fromUsersNum = fromUsers.size();
            String summaryBody = msgs[6].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));
            
            if (notificationInfoProvider != null) {
                // lastest text
                String customSummaryBody = notificationInfoProvider.getLatestText(message, fromUsersNum,notificationNum);
                if (customSummaryBody != null){
                    summaryBody = customSummaryBody;
                }
                
                // small icon
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0){
                    mBuilder.setSmallIcon(smallIcon);
                }
            }

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(summaryBody);
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone(EMMessage message) {
        if(message != null){
            if(EMChatManager.getInstance().isSlientMessage(message)){
                return;
            } 
        }
        
        
        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        
        try {
            lastNotifiyTime = System.currentTimeMillis();
            
            // 判断是否处于静音模式
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                EMLog.e(TAG, "in slient mode now");
                return;
            }
            EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
            if(settingsProvider.isMsgVibrateAllowed(message)){
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            if(settingsProvider.isMsgSoundAllowed(message)){
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        EMLog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }
                
                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;
                    
                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置通知栏消息Provider
     * 
     * @param provider
     */
    public void setNotificationInfoProvider(EaseNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface EaseNotificationInfoProvider {
        /**
         * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
         * 
         * @param message
         *            接收到的消息
         * @return null为使用默认
         */
        String getDisplayedText(EMMessage message);

        /**
         * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
         * 
         * @param message
         *            接收到的消息
         * @param fromUsersNum
         *            发送人的数量
         * @param messageNum
         *            消息数量
         * @return null为使用默认
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         * 
         * @param message
         * @return null为使用默认
         */
        String getTitle(EMMessage message);

        /**
         * 设置小图标
         * 
         * @param message
         * @return 0使用默认图标
         */
        int getSmallIcon(EMMessage message);

        /**
         * 设置notification点击时的跳转intent
         * 
         * @param message
         *            显示在notification上最近的一条消息
         * @return null为使用默认
         */
        Intent getLaunchIntent(EMMessage message);
    }
    
    /**
     * 根据环信id，在后台库中获取用户头像和昵称
     * 
     * by garry
     * 
     * @param username 环信id
     */
    private void getSimiUserInfo(final Context context, final EMMessage message, final boolean isForeground, final boolean numIncrease) {
//        if (!NetworkUtils.isNetworkConnected(appContext)) {
//            Toast.makeText(appContext, appContext.getString(R.string.net_not_open), 0).show();
//            return;
//        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("im_username", message.getFrom());
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
                            	
                              //得到对方用户信息后，推送新消息通知
                                sendNotification(message, isForeground, true, simiUser);
                            }else{
                            	
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
