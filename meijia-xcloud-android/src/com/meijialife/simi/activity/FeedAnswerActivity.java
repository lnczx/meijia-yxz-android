package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：回答页面
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class FeedAnswerActivity extends BaseActivity implements OnClickListener{
    
    
    private TextView m_tv_question_title;//问题标题

    private EditText m_et_question;//回答问题
    
    private TextView m_tv_submit;//问题提交
    private TextView title_tv_left;//提交取消
    private RelativeLayout m__rl_question;
    private TextView m_tv_tip;//输入字数提示
    
    private FeedData feedData;
    private int maxNum = 1000;//最多输入512个字
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_feed_answer);
        super.onCreate(savedInstanceState);
        isLogin();
        initView();
    }
    /**
     * 是否登录
     */
    private void isLogin(){
        Boolean login = SpFileUtil.getBoolean(getApplication(),SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if(!login){
           startActivity(new Intent(FeedAnswerActivity.this,LoginActivity.class));
           finish();
           return;
       } 
    }
    
  
    private void initView(){
        
        feedData = (FeedData)getIntent().getSerializableExtra("feedData");
        setTitleName("回答");
        initFeedAnswerView();
    }
    
    private void initFeedAnswerView(){
       
        m_tv_question_title = (TextView)findViewById(R.id.m_tv_question_title);
        m_tv_question_title.setText(feedData.getTitle());
        m_et_question = (EditText)findViewById(R.id.m_et_question);
        m_tv_tip =(TextView)findViewById(R.id.m_tv_tip);
        
        m__rl_question = (RelativeLayout)findViewById(R.id.m__rl_question);
        m__rl_question.setVisibility(View.VISIBLE);
        m__rl_question.setOnClickListener(this);
        m_tv_submit = (TextView)findViewById(R.id.m_tv_submit);
        m_tv_submit.setText("提交");
        title_tv_left = (TextView)findViewById(R.id.title_tv_left);
        title_tv_left.setVisibility(View.VISIBLE);
        title_tv_left.setOnClickListener(this);
        
        
        
          m_et_question.addTextChangedListener(new TextWatcher() {
            
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                int number = maxNum-s.length();
                m_tv_tip.setText("可输入"+number+"个字");
                selectionStart = m_et_question.getSelectionStart();
                selectionEnd = m_et_question.getSelectionEnd();
                if (temp.length() > maxNum) {

                        s.delete(selectionStart - 1, selectionEnd);

                        int tempSelection = selectionEnd;

                        m_et_question.setText(s);
                        m_et_question.setSelection(tempSelection);// 设置光标在最后

                }
            }
        });
        

    }
    /**
     * 获取动态列表接口
     */
    private void postFeedAnswer(String fid,String comment) {
        User user = DBHelper.getUser(this);
        if(user!=null){
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("fid", fid);
        map.put("comment",comment);
        map.put("feed_type", "2");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_DYNAMIC_COMMENT, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FeedAnswerActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            UIUtils.showToast(FeedAnswerActivity.this, "答案提交成功！");
                            finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(FeedAnswerActivity.this, errorMsg);
                }
            }
        });}else {
            startActivity(new Intent(FeedAnswerActivity.this,LoginActivity.class));
            finish();
        }
    }
  
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m__rl_question:
            String answer = m_et_question.getText().toString();
            if(StringUtils.isEmpty(answer)){
                UIUtils.showToast(FeedAnswerActivity.this, "回答不能为空！");
                return;
            }
            postFeedAnswer(feedData.getFid(),answer);
            break;
        case R.id.title_tv_left:
                finish();
            break;
        default:
            break;
        }
        
    }
    
   
}
