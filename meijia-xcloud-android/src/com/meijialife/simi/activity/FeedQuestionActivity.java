package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AlarmListAdapter;
import com.meijialife.simi.bean.AlarmData;
import com.meijialife.simi.bean.TagData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.wheelview.ArrayWheelAdapter;
import com.meijialife.simi.ui.wheelview.WheelView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：问答互助---我要提问页面
 * @author： kerryg
 * @date:2016年3月10日
 */
public class FeedQuestionActivity extends BaseActivity implements OnClickListener {


    private EditText m_et_question;//问题描述
    private TextView m_tv_tip;//输入文字个数提示
    private TextView m_tv_gold;//赏金个数
    private TextView m_question_tags;//问题标签

    private RelativeLayout m_rl_question;//右上角提交
    private TextView m_tv_submit;


    private AlarmListAdapter adapter;
    private List<AlarmData> alarmDatas;
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    private int remindAlerm = 0;// 提醒设置 0 = 不提醒 1 = 一天前 3 = 3天前 7=7天前
    private PopupWindow mTimePopup;
    private View view_mask;

    private int maxNum = 512;
    private String tag_ids = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.feed_question_activity);
        super.onCreate(savedInstanceState);

        isLogin();
        initView();
    }

    private void isLogin() {
        Boolean login = SpFileUtil.getBoolean(getApplication(), SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        if (!login) {
            startActivity(new Intent(FeedQuestionActivity.this, LoginActivity.class));
            finish();
            return;
        }
    }

    private void initView() {

        requestBackBtn();
        setTitleName("描述问题");

        m_et_question = (EditText) findViewById(R.id.m_et_question);
        m_tv_tip = (TextView) findViewById(R.id.m_tv_tip);
        m_tv_tip.setText("可输入" + maxNum + "个字");
        m_tv_gold = (TextView) findViewById(R.id.m_tv_gold);
        m_question_tags = (TextView) findViewById(R.id.m_question_tags);
        m_tv_submit = (TextView) findViewById(R.id.m_tv_submit);

        view_mask = findViewById(R.id.view_mask);
        m_rl_question = (RelativeLayout) findViewById(R.id.m__rl_question);
        m_rl_question.setVisibility(View.VISIBLE);
        m_tv_submit.setText("提交");

        UserInfo userInfo = DBHelper.getUserInfo(this);
        if (null != userInfo) {
            m_tv_gold.setText("您有" + userInfo.getRest_money() + "金币，有赏有效果哦");
        }

        setOnClick();


    }


    private void setOnClick() {
        findViewById(R.id.m_questoin_item1).setOnClickListener(this);
        findViewById(R.id.m_questoin_item2).setOnClickListener(this);
        m_rl_question.setOnClickListener(this);

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
                int number = maxNum - s.length();
                m_tv_tip.setText("可输入" + number + "个字");
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


    public void showRemindWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_remind, null, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("答题悬赏");
        InitTimeRemind(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_mask, Gravity.BOTTOM, 0, 0);
        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });


    }

    private View InitTimeRemind(View view) {

        remind = (WheelView) view.findViewById(R.id.remind);
        String[] items = new String[5];
        items[0] = "0";
        items[1] = "10";
        items[2] = "20";
        items[3] = "50";
        items[4] = "100";


        arryadapter = new ArrayWheelAdapter<>(this, items);
        remind.setViewAdapter(arryadapter);
        remind.setCyclic(false);// 是否可循环滑动
        remind.setVisibleItems(items.length);// 设置显示行数
        remind.setCurrentItem(0);
        arryadapter.setTextColor(getResources().getColor(R.color.simi_color_black));
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = remind.getCurrentItem();
                remindAlerm = currentItem;
                String itemText = (String) arryadapter.getItemText(currentItem);
                m_tv_gold.setText(itemText);
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
                // Toast.makeText(MainPlusTrevelActivity.this, time, 1).show();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_questoin_item1:
                //关闭键盘
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                showRemindWindow();
                break;
            case R.id.m_questoin_item2:
                startActivity(new Intent(FeedQuestionActivity.this, QuestionTagListActivity.class));
                break;
            case R.id.m__rl_question:
                String title = m_et_question.getText().toString();
                String feed_extra = m_tv_gold.getText().toString();
                if (StringUtils.isEmpty(title)) {
                    Toast.makeText(this, "问题描述不可以为空，请编辑问题描述", Toast.LENGTH_SHORT).show();
                    return;
                }
                findViewById(R.id.m__rl_question).setClickable(false);
                postFeed(title, feed_extra);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onRestart() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbs = new StringBuilder();
        ArrayList<TagData> list = Constants.tagList;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TagData tagData = list.get(i);
                if (i < list.size() - 1) {
                    sb.append(tagData.getTag_name() + ",");
                    sbs.append(tagData.getTag_id() + ",");
                } else {
                    sb.append(tagData.getTag_name());
                    sbs.append(tagData.getTag_id());
                }
            }
        }
        tag_ids = sbs.toString();
        m_question_tags.setText(sb.toString());
        super.onRestart();
    }

    /**
     * 我要提问问题接口
     *
     * @param title
     * @param feed_extra
     */
    private void postFeed(String title, String feed_extra) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(FeedQuestionActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        if (user != null) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", title);
            map.put("feed_type", "2");//类型 0 = 动态 1 = 文章 2 = 问答
            map.put("feed_extra", feed_extra);
            map.put("tag_ids", tag_ids);
            map.put("user_id", user.getId());
            AjaxParams param = new AjaxParams(map);

            new FinalHttp().post(Constants.URL_POST_FRIEND_DYNAMIC, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Toast.makeText(FeedQuestionActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                findViewById(R.id.m__rl_question).setClickable(true);
                                Toast.makeText(FeedQuestionActivity.this, "问题提交成功！", Toast.LENGTH_SHORT).show();
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
                    if (!StringUtils.isEmpty(errorMsg.trim())) {
                        UIUtils.showToast(FeedQuestionActivity.this, errorMsg);
                    }
                }
            });
        } else {
            startActivity(new Intent(FeedQuestionActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Constants.tagList.clear();
        super.onDestroy();
    }
}
