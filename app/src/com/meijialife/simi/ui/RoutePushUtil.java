package com.meijialife.simi.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.CardListActivity;
import com.meijialife.simi.activity.CompanyListActivity;
import com.meijialife.simi.activity.CompanyListsActivity;
import com.meijialife.simi.activity.CompanyRegisterActivity;
import com.meijialife.simi.activity.DiscountCardActivity;
import com.meijialife.simi.activity.FindServerDetailActivity;
import com.meijialife.simi.activity.FriendApplyActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.MainPlusApplicationActivity;
import com.meijialife.simi.activity.MainPlusAssetListActivity;
import com.meijialife.simi.activity.MainPlusCarOrderActivity;
import com.meijialife.simi.activity.MainPlusCleanActivity;
import com.meijialife.simi.activity.MainPlusExpressActivity;
import com.meijialife.simi.activity.MainPlusLeaveDetailActivity;
import com.meijialife.simi.activity.MainPlusLeaveListActivity;
import com.meijialife.simi.activity.MainPlusSignInActivity;
import com.meijialife.simi.activity.MainPlusTeamActivity;
import com.meijialife.simi.activity.MainPlusWasterActivity;
import com.meijialife.simi.activity.MainPlusWaterActivity;
import com.meijialife.simi.activity.MyOrderActivity;
import com.meijialife.simi.activity.MyWalletActivity;
import com.meijialife.simi.activity.OrderDetailsActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.activity.WebViewDetailsActivity;
import com.meijialife.simi.bean.ReceiverBean;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;

import static android.R.attr.id;

/**
 * 推送路由跳转
 *
 * @author： yejiurui
 * @date:2016年12月06日
 */
public class RoutePushUtil {

    private static Context context;
    private static ReceiverBean mReceiverBean;

    public RoutePushUtil(Context context, ReceiverBean receiverBean) {
        super();
        this.context = context;
        this.mReceiverBean = receiverBean;
    }

    /**
     * 创建通知消息
     *
     * @param intent
     */
    public static void notify(PendingIntent intent) {
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(mReceiverBean.getRc()).setContentText(mReceiverBean.getRc())
                .setSmallIcon(R.drawable.icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mReceiverBean.getRc()));
        mNotifyBuilder.setContentIntent(intent);

        Notification notification = mNotifyBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, notification);
    }


    public void Routings(String category, String action, String goto_url, String params) {
        Intent intent;
        if (!StringUtils.isEmpty(category)) {
            if (category.equals("h5")) {
                intent = new Intent(context, WebViewsActivity.class);
                intent.putExtra("url", goto_url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent intent1 = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notify(intent1);
            } else if (category.equals("app") && !StringUtils.isEmpty(action)) {
                if (action.equals("index")) {// 首页
                    intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    if(StringUtils.isNotEmpty(params) && "youliao-redpoint".equalsIgnoreCase(params)){
                        SpFileUtil.saveInt(context, SpFileUtil.FILE_UI_PARAMETER,
                                SpFileUtil.KEY_YOULIAO_RED, 1);
                    }
                } else if (action.equals("discover")) {// 发现
                   /* MainActivity mainActivity =MyApplication.getInstance().getMainActivity();
                    mainActivity.changeFind();*/
                } else if (action.equals("sns")) {// 圈子
                  /*  MainActivity mainActivity =MyApplication.getInstance().getMainActivity();
                    mainActivity.change2Contacts();
                    Constants.checkedIndex = 0;*/
                } else if (action.equals("mine")) {// 我的
                  /*  MainActivity mainActivity =MyApplication.getInstance().getMainActivity();
                    mainActivity.changePersonal();*/
                } else if (action.equals("work")) {// 加号
                   /* Intent intent2 = new Intent(context, MainPlusActivity.class);
                    MainActivity mainActivity =MyApplication.getInstance().getMainActivity();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainActivity.startActivity(intent2);
                    ((MainActivity) mainActivity).overridePendingTransition(R.anim.activity_open, 0);*/
                } else if (action.equals("alarm")) {// 事务提醒
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "3");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("meeting")) {// 会议安排
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("company")) {// 企业通讯录
                    UserInfo userInfo = DBHelper.getUserInfo(context);
                    if (userInfo.getHas_company() == 0) {
                        intent = new Intent(context, CompanyRegisterActivity.class);
                        notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    } else {
                        intent = new Intent(context, CompanyListActivity.class);
                        intent.putExtra("flag", 2);
                        notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    }
                } else if (action.equals("company")) {// 跳转到公司列表
                    intent = new Intent(context, CompanyListActivity.class);
                    intent.putExtra("flag", 2);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("notice")) {// 通知公告
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "2");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("interview")) {// 面试邀约
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "4");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("trip")) {// 差旅规划
                    intent = new Intent(context, CardListActivity.class);
                    intent.putExtra("cardType", "5");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("card_detail")) {// 跳转到卡片详情
                    intent = new Intent(context, CardDetailsActivity.class);
                    intent.putExtra("card_id", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("feed")) {// 跳转到动态列表
                    /*
                     * intent = new Intent(context,FriendApplyActivity.class); context.startActivity(intent);
                     */
                } else if (action.equals("checkin")) {// 跳转到考勤
                    intent = new Intent(context, MainPlusSignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("friends")) {// 跳转到好友审批页面
                    intent = new Intent(context, FriendApplyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("im")) {// 跳转到IM消息页面
                   /* MainActivity mainActivity = (MainActivity) context;
                    mainActivity.change2IM();*/
                } else if (action.equals("leave")) {// 跳转到请假列表页面
                    intent = new Intent(context, MainPlusLeaveListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("leave_pass")) {// 跳转到请假详情页面
                    intent = new Intent(context, MainPlusLeaveDetailActivity.class);
                    intent.putExtra("leave_id", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("express")) {// 跳转到快递列表页面
                    intent = new Intent(context, MainPlusExpressActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("water")) {// 跳转到送水订单列表
                    intent = new Intent(context, MainPlusWaterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("teamwork")) {// 跳转到团队建设详情
                    intent = new Intent(context, MainPlusTeamActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("expy")) {// 速通宝
                    intent = new Intent(context, MainPlusCarOrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("company_pass")) {// 跳转到公司申请列表
                    intent = new Intent(context, CompanyListsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("app_tools")) {// 跳转到应用中心
                    intent = new Intent(context, MainPlusApplicationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("wallet")) {// 跳转到钱包
                    intent = new Intent(context, MyWalletActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("coupons")) {// 跳转到我的优惠券
                    intent = new Intent(context, DiscountCardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("order")) {// 跳转到订单列表
                    intent = new Intent(context, MyOrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("order_detail")) {// 跳转到订详情
                    intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderId", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("friend_req")) {// 跳转到好友申请
                    intent = new Intent(context, FriendApplyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("flag", true);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("company_pass")) {// 跳转到团队申请
                    intent = new Intent(context, FriendApplyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("flag", false);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("p_user_list")) {// 跳转到服务人员列表
                    intent = new Intent(context, FindServerDetailActivity.class);
                    intent.putExtra("service_type_ids", params);
                    intent.putExtra("title_name", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("score")) {// 跳转到积分商城
                    Intent intent6 = new Intent();
                    intent6.setClass(context, PointsShopActivity.class);
                    intent6.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
                    intent6.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
                    intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(context).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
                    intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent6);
                } else if (action.equals("boon")) {// 跳转到福利商城

                } else if (action.equals("add_friend")) {
                    intent = new Intent(context, FriendPageActivity.class);
                    intent.putExtra("friend_id", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("clean")) {// 保洁列表页面
                    intent = new Intent(context, MainPlusCleanActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("recycle")) {// 企业内训列表页面
                    intent = new Intent(context, MainPlusWasterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("card")) {
                    intent = new Intent(context, CardDetailsActivity.class);

                    intent.putExtra("card_id", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (action.equals("asset")) {
                    intent = new Intent(context, MainPlusAssetListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                }else if (action.equals("video_detail")) {//视频详情
                    intent = new Intent(context, CourseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("videoId", params);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                }
            } else if (category.equals("h5+list")) {
                if (action.equals("p_user_list")) {// 先跳转到h5再跳到服务人员列表
                    intent = new Intent(context, WebViewDetailsActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", "");
                    intent.putExtra("service_type_ids", params);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notify(PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                }
            }
        }
    }

}
