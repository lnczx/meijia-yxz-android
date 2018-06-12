package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.QuestionTagAdapter;
import com.meijialife.simi.bean.TagData;
import com.meijialife.simi.ui.ChannelGridView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

public class QuestionTagListActivity extends BaseActivity {

    private ChannelGridView m_channel_list;
    private List<TagData> mDefaultTags;
    private QuestionTagAdapter mQuestionTagAdapter;

    private boolean[] tagStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_question_tags_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {

        requestBackBtn();
        setTitleName("问题标签");
        Constants.tagList.clear();
        m_channel_list = (ChannelGridView) findViewById(R.id.m_tag_list);
        mDefaultTags = new ArrayList<TagData>();
        mQuestionTagAdapter = new QuestionTagAdapter(QuestionTagListActivity.this);
        m_channel_list.setAdapter(mQuestionTagAdapter);

        m_channel_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean flag = tagStatus[position];
                if (flag) {
                    tagStatus[position] = false;
                } else {
                    tagStatus[position] = true;
                }
                mQuestionTagAdapter.setTagStatus(tagStatus);
                mQuestionTagAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        if (mDefaultTags != null && mDefaultTags.size() > 0) {
            for (int i = 0; i < mDefaultTags.size(); i++) {
                TagData tagData = mDefaultTags.get(i);
                boolean flag = tagStatus[i];
                if (flag) {
                    Constants.tagList.add(tagData);
                }
            }
           
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        getDefaultTagsList();
        super.onStart();
    }

    /**
     * 获得问题互助标签
     */
    public void getDefaultTagsList() {
        if (!NetworkUtils.isNetworkConnected(QuestionTagListActivity.this)) {
            Toast.makeText(QuestionTagListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        param.put("tag_type", "3");// 标签类型 0 = 用户 1 = 秘书 2 = 服务商 3= 问答
        new FinalHttp().get(Constants.GET_TAG_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(QuestionTagListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                Gson gson = new Gson();
                                mDefaultTags = gson.fromJson(data, new TypeToken<ArrayList<TagData>>() {
                                }.getType());
                                tagStatus = new boolean[mDefaultTags.size()];
                                mQuestionTagAdapter.setData(mDefaultTags, tagStatus);
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
                    UIUtils.showToast(QuestionTagListActivity.this, errorMsg);
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

}