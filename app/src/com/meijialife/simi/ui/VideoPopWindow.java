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
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 视频播放页弹窗
 */
public class VideoPopWindow extends PopupWindow {
    private View conentView;
    private Context contexts;

    private TextView tv_title, tv_content;
    private TextView btn_more, btn_done;

    public VideoPopWindow(final Activity context, String title, String content, final String url, final String article_id) {
        
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
                postHelp(article_id);
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

    /**
     * 帮助-帮助点击记录接口
     */
    private void postHelp(String article_id) {
        User user = DBHelper.getUser(contexts);
        if (!NetworkUtils.isNetworkConnected(contexts) || contexts == null || user == null) {
//            Toast.makeText(contexts, getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "video-help");
        map.put("user_id", "" + user.getId());
        map.put("link_id", "" + article_id);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_HELP, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(contexts, contexts.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            errorMsg = contexts.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = contexts.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = contexts.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = contexts.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = contexts.getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
//                    UIUtils.showToast(contexts, errorMsg);
                }
            }
        });
    }
  
}
