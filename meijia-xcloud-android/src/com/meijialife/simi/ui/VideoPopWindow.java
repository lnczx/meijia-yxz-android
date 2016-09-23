package com.meijialife.simi.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;

/**
 * 视频播放页弹窗
 */
public class VideoPopWindow extends PopupWindow {
    private View conentView;
    private Context contexts;

    private TextView tv_title, tv_content;
    private TextView btn_more, btn_done;

    public VideoPopWindow(final Activity context, String title, String content, final String url) {
        
        this.contexts = context;
        //加载popWidow布局文件
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.video_pop_window, null);
        
        //获取屏幕尺寸，设置Popwidow尺寸大小
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w  = dm.widthPixels;
        int h = dm.heightPixels;
        this.setContentView(conentView);  
        this.setWidth(w*5/7);  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        
        
        //获得对应的控件
        tv_title = (TextView)conentView.findViewById(R.id.tv_title);
        tv_content = (TextView)conentView.findViewById(R.id.tv_content);
        btn_more = (TextView)conentView.findViewById(R.id.btn_more);
        btn_done = (TextView)conentView.findViewById(R.id.btn_done);

        final User user = DBHelper.getUser(contexts);

        //为控件赋值
        tv_title.setText(title);
        tv_content.setText(content);
      
        //设置动画和点击范围
        this.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        this.setFocusable(true);  
        this.setOutsideTouchable(true);
        btn_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != this && VideoPopWindow.this.isShowing()) {
                    VideoPopWindow.this.dismiss();
                    backgroundAlpha(1f);
                }
            }
        });

        btn_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != this && VideoPopWindow.this.isShowing()) {

                    Intent intent = new Intent(contexts, WebViewsActivity.class);
                    intent.putExtra("url", url);
                    contexts.startActivity(intent );
                    VideoPopWindow.this.dismiss();
                    backgroundAlpha(1f);
                }
            }
        });
    }
    /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    @SuppressLint("NewApi")
	public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            backgroundAlpha(0.4f);
            showAtLocation(parent,Gravity.CENTER,0,0);
        } else {  
            this.dismiss();  
        }  
    } 
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity) contexts).getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            ((Activity) contexts).getWindow().setAttributes(lp);
    }
  
}
