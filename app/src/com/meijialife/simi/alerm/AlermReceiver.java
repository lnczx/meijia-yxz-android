package com.meijialife.simi.alerm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.activity.CardAlertActivity;

/**
 * 提醒接收者
 */
@SuppressLint({"NewApi", "UseValueOf"})
public class AlermReceiver extends BroadcastReceiver {

    public static final String TAG = "Alerm";

    Context context;

    @SuppressLint("UseValueOf")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


        String card_id = intent.getStringExtra("card_id");
        //其他周期执行的弹屏操作
        Intent intent2 = new Intent(context, CardAlertActivity.class);
        intent2.putExtra("card_id", card_id);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        AlermUtils.playAudio(context);

    }

    /*private void showDlg(Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher_logo_alerm));
        builder.setTitle(title);
        builder.setMessage(text).setCancelable(false).setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
//                if(null!=mWakelock){
//                    mWakelock.release();
//                }
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if(null!=mWakelock){
//                    mWakelock.release();
//                }
                
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
        LogOut.i(TAG, "成功弹出提示框！");
    }*/

}