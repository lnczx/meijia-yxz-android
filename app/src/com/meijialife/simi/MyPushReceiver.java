package com.meijialife.simi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.meijialife.simi.activity.CarAlertActivity;
import com.meijialife.simi.activity.CardAlertActivity;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.NoticeActivity;
import com.meijialife.simi.activity.SplashActivity;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.ReceiverBean;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.RoutePushUtil;
import com.meijialife.simi.utils.AndroidUtil;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyPushReceiver extends BroadcastReceiver {

  /** 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null) */
  public static StringBuilder payloadData = new StringBuilder();

  private Context mContext;
  private ReceiverBean receiverBean;
  private Date fdate;
  private final String ACTION_SETCLOCK = "s";
  private final String ACTION_ALARM = "a";
  private final String ACTION_MSG = "m";
  private final String ACTION_DEL_COLOCK = "d"; // 收到推送删除本地闹钟
  private final String ACTION_CAR_MSG = "car-msg";

  private SQLiteDatabase db;

  @SuppressWarnings("deprecation")
  @Override
  public void onReceive(Context context, Intent intent) {
    this.mContext = context;

    Bundle bundle = intent.getExtras();
    Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
    switch (bundle.getInt(PushConsts.CMD_ACTION)) {
      case PushConsts.GET_MSG_DATA:
        // 获取透传数据
        // String appid = bundle.getString("appid");
        byte[] payload = bundle.getByteArray("payload");

        String taskid = bundle.getString("taskid");
        String messageid = bundle.getString("messageid");

        // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result =
            PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

        if (payload != null) {
          String data = new String(payload);
          payloadData.append(data);
          payloadData.append("\n");
          try {
            receiverBean = new Gson().fromJson(data, new TypeToken<ReceiverBean>() {}.getType());
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (null != receiverBean) { // 为了测试接收推送，不做操作

            if (!AndroidUtil.isRunningForeground(context)) {
              /** app处于后台 勾选理解提醒 声音+通知 准点弹出大屏+声音 */
              if (StringUtils.isEquals(receiverBean.getIs(), "true")
                  && StringUtils.isEquals(receiverBean.getAc(), ACTION_MSG)) {
                // 推送通知
                //                            setNotification(receiverBean);
                if (StringUtils.isNotEmpty(receiverBean.getGo())
                    || StringUtils.isNotEmpty(receiverBean.getAj())) {
                  RoutePushUtil ru = new RoutePushUtil(context, receiverBean);
                  //                                RouteUtil ru = new RouteUtil(context);
                  ru.Routings(
                      receiverBean.getCa(),
                      receiverBean.getAj(),
                      receiverBean.getGo(),
                      receiverBean.getPa());
                }
              } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_SETCLOCK)) {
                // is_show=true表示显示通知栏，=false不显示通知栏
                if (StringUtils.isEquals(receiverBean.getIs(), "true")) {
                  setNotification3(receiverBean);
                  AlermUtils.playAudio(context);
                }
                Date date = new Date(receiverBean.getRe() * 1000);
                // 接收推送给其他人设置本地闹钟
                AlermUtils.initAlerm(
                    mContext,
                    1,
                    date,
                    receiverBean.getRt(),
                    receiverBean.getRc(),
                    receiverBean.getCi());
                setLocalAlarm(receiverBean.getCi());
              } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_ALARM)) {
                // push-alarm准点推送弹出大屏
                // is_show=true表示显示通知栏，=false不显示通知栏
                AlermUtils.playAudio(context);
                intent = new Intent(mContext, CardAlertActivity.class);
                long remindTime = Long.parseLong((receiverBean.getRe() * 1000) + "");
                fdate = new Date(remindTime);
                intent.putExtra("title", receiverBean.getRt());
                intent.putExtra("text", receiverBean.getRc());
                intent.putExtra("date", fdate);
                intent.putExtra("card_id", receiverBean.getCi());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
              } else if (StringUtils.isEquals(receiverBean.getIs(), "false")
                  && StringUtils.isEquals(receiverBean.getAc(), ACTION_CAR_MSG)) {
                Intent intent1 = new Intent(mContext, CarAlertActivity.class);
                intent1.putExtra("bean", receiverBean);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);

              } else if (StringUtils.isEquals(
                  receiverBean.getAc(), ACTION_DEL_COLOCK)) { // 删除其他用户本地闹钟
                AssetsDatabaseManager.initManager(context); // 初始化，只需要调用一次
                AssetsDatabaseManager mg =
                    AssetsDatabaseManager.getManager(); // 获取管理对象，因为数据库需要通过管理对象才能够获取
                db = mg.getDatabase("simi01.db"); // 通过管理对象获取数据库
                AlermUtils.cancelSigninAlerm(context, Integer.valueOf(receiverBean.getCi()));
                AssetsDatabaseManager.deleteAlertCardByCardId(db, receiverBean.getCi());
              }
            } else {
              /** app处于前台，创建勾选立即提醒，弹出小窗口+声音+通知 到点弹出大屏+声音 */
              if (StringUtils.isEquals(receiverBean.getIs(), "true")
                  && StringUtils.isEquals(receiverBean.getAc(), ACTION_MSG)) {
                // 推送通知
                //                            setNotification(receiverBean);
                if (StringUtils.isNotEmpty(receiverBean.getAj())) {
                  RoutePushUtil ru = new RoutePushUtil(context, receiverBean);
                  ru.Routings(
                      receiverBean.getCa(),
                      receiverBean.getAj(),
                      receiverBean.getGo(),
                      receiverBean.getPa());
                }
              } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_SETCLOCK)) {
                // is_show=true表示显示通知栏，=false不显示通知栏
                if (StringUtils.isEquals(receiverBean.getIs(), "true")) {
                  setNotification3(receiverBean);
                  AlermUtils.playAudio(context);

                  intent = new Intent(mContext, NoticeActivity.class);
                  intent.putExtra("receiverBean", receiverBean);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  context.startActivity(intent);
                }
                Date date = new Date(receiverBean.getRe() * 1000);
                // 接收推送给其他人设置本地闹钟
                AlermUtils.initAlerm(
                    mContext,
                    1,
                    date,
                    receiverBean.getRt(),
                    receiverBean.getRc(),
                    receiverBean.getCi());
                setLocalAlarm(receiverBean.getCi());
              } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_ALARM)) {
                // push-alarm准点推送弹出大屏
                // is_show=true表示显示通知栏，=false不显示通知栏
                //                            if(StringUtils.isEquals(receiverBean.getIs_show(),
                // "true")){
                //                                setNotification3(receiverBean);
                //                                AlermUtils.playAudio(context);
                //                            }
                AlermUtils.playAudio(context);
                intent = new Intent(mContext, CardAlertActivity.class);
                long remindTime = Long.parseLong((receiverBean.getRe() * 1000) + "");
                fdate = new Date(remindTime);
                intent.putExtra("title", receiverBean.getRt());
                intent.putExtra("text", receiverBean.getRc());
                intent.putExtra("date", fdate);
                intent.putExtra("card_id", receiverBean.getCi());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
              } else if (StringUtils.isEquals(receiverBean.getIs(), "false")
                  && StringUtils.isEquals(receiverBean.getAc(), ACTION_CAR_MSG)) {
                Intent intent1 = new Intent(mContext, CarAlertActivity.class);
                intent1.putExtra("bean", receiverBean);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);

              } else if (StringUtils.isEquals(
                  receiverBean.getAc(), ACTION_DEL_COLOCK)) { // 删除其他用户本地闹钟
                AlermUtils.cancelSigninAlerm(context, Integer.valueOf(receiverBean.getCi()));
              }
            }
          }
        }
        break;
      case PushConsts.GET_CLIENTID:
        String cid = bundle.getString("clientid");
        if (StringUtils.isNotEmpty(cid)) {
          SpFileUtil.saveString(
              mContext, SpFileUtil.FILE_UI_PARAMETER, SpFileUtil.KEY_CLIENT_ID, cid);
        }
        LogOut.i("推送cid:" + cid);
        break;
      default:
        break;
    }
  }
  /**
   * 点击弹框进入app
   *
   * @param receiverBean
   */
  private void setNotification(ReceiverBean receiverBean) {
    // NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
    NotificationManager manager =
        (NotificationManager)
            mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

    // 点击通知负责页面跳转
    //        Intent intent = new Intent(mContext, SplashActivity.class);
    Intent intent = new Intent(mContext, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(mContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    // android3.0以后采用NotificationCompat构建
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setContentTitle(receiverBean.getRt());
    builder.setContentText(receiverBean.getRc());
    builder.setSmallIcon(R.drawable.icon);
    builder.setLargeIcon(
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon));
    builder.setDefaults(Notification.DEFAULT_ALL);
    builder.setContentIntent(pendingIntent);
    builder.setAutoCancel(true);
    // builder.setSubText("点击进入播放...");
    // builder.setTicker(channelNanme + "的" + programName + "还有五分钟就要开播了！" + "点击进入播放...");
    manager.notify(1, builder.build());
  }
  /**
   * 点击弹框进入大屏
   *
   * @param receiverBean
   */
  private void setNotification2(ReceiverBean receiverBean) {
    // NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
    NotificationManager manager =
        (NotificationManager)
            mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    long remindTime = Long.parseLong((receiverBean.getRe() * 1000) + "");
    fdate = new Date(remindTime);
    // 点击通知负责页面跳转
    Intent intent = new Intent(mContext, CardAlertActivity.class);
    //        Intent intent = new Intent(mContext, MainActivity.class);
    intent.putExtra("title", receiverBean.getRt());
    intent.putExtra("text", receiverBean.getRc());
    intent.putExtra("date", fdate);
    intent.putExtra("card_id", receiverBean.getCi());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(mContext, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    // android3.0以后采用NotificationCompat构建
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setContentTitle(receiverBean.getRt());
    builder.setContentText(receiverBean.getRc());
    builder.setSmallIcon(R.drawable.icon);
    builder.setLargeIcon(
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon));
    builder.setDefaults(Notification.DEFAULT_ALL);
    builder.setContentIntent(pendingIntent);
    builder.setAutoCancel(true);
    manager.notify(1, builder.build());
  }
  /**
   * 点击通知进入卡片详情
   *
   * @param receiverBean
   */
  private void setNotification3(ReceiverBean receiverBean) {
    // NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
    NotificationManager manager =
        (NotificationManager)
            mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    // 点击通知负责页面跳转
    Intent intent = new Intent(mContext, CardDetailsActivity.class);
    //        Intent intent = new Intent(mContext, MoreActivity.class);
    intent.putExtra("card_id", receiverBean.getCi());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    // android3.0以后采用NotificationCompat构建
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setContentTitle(receiverBean.getRt());
    builder.setContentText(receiverBean.getRc());
    builder.setSmallIcon(R.drawable.icon);
    builder.setLargeIcon(
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon));
    builder.setDefaults(Notification.DEFAULT_ALL);
    builder.setContentIntent(pendingIntent);
    builder.setAutoCancel(true);
    manager.notify(1, builder.build());
  }

  private void setNotification4(ReceiverBean receiverBean) {
    // NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
    NotificationManager manager =
        (NotificationManager)
            mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

    // 点击通知负责页面跳转
    //        Intent intent = new Intent(mContext, SplashActivity.class);
    Intent intent = new Intent(mContext, CarAlertActivity.class);
    intent.putExtra("bean", receiverBean);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(mContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    // android3.0以后采用NotificationCompat构建
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
    builder.setContentTitle(receiverBean.getRt());
    builder.setContentText(receiverBean.getRc());
    builder.setSmallIcon(R.drawable.icon);
    builder.setLargeIcon(
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon));
    builder.setDefaults(Notification.DEFAULT_ALL);
    builder.setContentIntent(pendingIntent);
    builder.setAutoCancel(true);
    manager.notify(1, builder.build());
  }

  private void setLocalAlarm(String cardId) {
    if (!NetworkUtils.isNetworkConnected(mContext)) {
      Toast.makeText(mContext, mContext.getString(R.string.net_not_open), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    User user = DBHelper.getUser(mContext);
    Map<String, String> map = new HashMap<String, String>();
    map.put("card_id", cardId);
    map.put("user_id", "" + user.getId());
    AjaxParams param = new AjaxParams(map);
    new FinalHttp()
        .post(
            Constants.URL_POST_SET_LOCAL_ALARM,
            param,
            new AjaxCallBack<Object>() {
              @Override
              public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(
                        mContext, mContext.getString(R.string.network_failure), Toast.LENGTH_SHORT)
                    .show();
              }

              @Override
              public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                  if (StringUtils.isNotEmpty(t.toString())) {
                    JSONObject obj = new JSONObject(t.toString());
                    int status = obj.getInt("status");
                    String msg = obj.getString("msg");
                    String data = obj.getString("data");
                    if (status == Constants.STATUS_SUCCESS) { // 正确
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                      errorMsg = mContext.getString(R.string.servers_error);
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                      errorMsg = mContext.getString(R.string.param_missing);
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                      errorMsg = mContext.getString(R.string.param_illegal);
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                      errorMsg = msg;
                    } else {
                      errorMsg = mContext.getString(R.string.servers_error);
                    }
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                  errorMsg = mContext.getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                  UIUtils.showToast(mContext, errorMsg);
                }
              }
            });
  }
}
