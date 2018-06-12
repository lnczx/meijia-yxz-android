package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ChannelAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.ChannelGridView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 更多频道Activity
 * 
 */
public class ChannelListActivity extends BaseActivity {

    private ChannelGridView m_channel_list;
    private List<String> mDefaultTags;
    private List<String> mUserTags;
    private ChannelAdapter mChannelAdapter;
    private ImageView m_iv_cancle;
    HashMap<String,String> tagMap = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_channel_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {

        m_channel_list = (ChannelGridView) findViewById(R.id.m_channel_list);
        mDefaultTags = new ArrayList<String>();
        mUserTags = new ArrayList<String>();
        tagMap = new HashMap<String,String>();
        mChannelAdapter = new ChannelAdapter(ChannelListActivity.this);
        m_iv_cancle = (ImageView) findViewById(R.id.m_iv_cancle);
        m_channel_list.setAdapter(mChannelAdapter);
        getDefaultTagsList();
        


        m_channel_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = mDefaultTags.get(position);
                if(!tagMap.isEmpty()){
                    if(tagMap.containsKey(temp)){
                        tagMap.remove(temp);
                    }else {
                        tagMap.put(temp,temp);
                    }
                }else {
                    tagMap.put(temp,temp); 
                }
                List<String> list = new ArrayList<String>();
                for (Map.Entry<String, String> entry : tagMap.entrySet()) {  
                    list.add(entry.getValue());
                } 
                setUserTagsList(list);
            }
            
        });

        m_iv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获得默认订阅文章标签
     */
    public void getDefaultTagsList() {
        if (!NetworkUtils.isNetworkConnected(ChannelListActivity.this)) {
            Toast.makeText(ChannelListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_DEFAULT_SUBSCRIBE_TAGS_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ChannelListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isNotEmpty(data)) {
                                String[] temp = data.split(",");
                                mDefaultTags = Arrays.asList(temp);
                                getUserTagsList(mDefaultTags);
                            } else {
                                mDefaultTags = new ArrayList<String>();
                            }
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
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(ChannelListActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 获取用户订阅的文章标签接口
     */
    public void getUserTagsList(final List<String> defaultTags) {
        if (!NetworkUtils.isNetworkConnected(ChannelListActivity.this)) {
            Toast.makeText(ChannelListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        param.put("user_id",user.getId());
        new FinalHttp().get(Constants.GET_USER_SUBSCRIBE_TAGS_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ChannelListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
SpFileUtil.saveString(ChannelListActivity.this,SpFileUtil.KEY_USER_TAGS, Constants.USER_TAGS,data);;   
                            if (StringUtils.isNotEmpty(data)) {
                                String[] temp = data.split(",");
                                mUserTags = Arrays.asList(temp);
                                mChannelAdapter.setData(defaultTags,mUserTags);
                            } else {
                                mUserTags = new ArrayList<String>();
                                mChannelAdapter.setData(defaultTags,mUserTags);
                            }
                            for (Iterator iterator = mUserTags.iterator(); iterator.hasNext();) {
                                String string = (String) iterator.next();
                                tagMap.put(string, string);
                            }
                            
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
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(ChannelListActivity.this, errorMsg);
                }
            }
        });
    }
    
    
    public static String listToString(List list) {  
        
        StringBuilder sb = new StringBuilder();  
        if (list != null && list.size() > 0) {  
            for (int i = 0; i < list.size(); i++) {  
                if (i < list.size() - 1) {  
                    sb.append(list.get(i) + ",");  
                } else {  
                    sb.append(list.get(i));  
                }  
            }  
        }  
        return sb.toString();  
    } 
    
    /**
     * 设置用户订阅的文章标签接口
     */
    public void setUserTagsList(final List<String> setTags) {
        if (!NetworkUtils.isNetworkConnected(ChannelListActivity.this)) {
            Toast.makeText(ChannelListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        param.put("user_id",user.getId());
        param.put("subscribe_tags",listToString(setTags));
        new FinalHttp().post(Constants.SET_USER_SUBSCRIBE_TAGS_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ChannelListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                           getDefaultTagsList();
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
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(ChannelListActivity.this, errorMsg);
                }
            }
        });
    }

}