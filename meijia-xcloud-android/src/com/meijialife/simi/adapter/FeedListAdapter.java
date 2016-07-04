package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.FeedAnswerActivity;
import com.meijialife.simi.activity.FeedDetailActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;

/**
 * 文章、动态、问答列表适配器
 *
 */
public final class FeedListAdapter extends BaseAdapter {

    private Context context;
    private List<FeedData> datas;

    private LayoutInflater layoutInflater;
    private FinalBitmap finalBitmap;


    public FeedListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.datas = new ArrayList<FeedData>();
        finalBitmap = FinalBitmap.create(context);
    }

    public void setData(List<FeedData> feedDatas) {
        this.datas = feedDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.question_list_item, null);//

            holder = new ViewHolder();

            holder.m_tv_question_gold = (TextView) convertView.findViewById(R.id.m_tv_question_gold);
            holder.m_tv_question_content = (TextView) convertView.findViewById(R.id.m_tv_question_content);
            holder.m_tv_question_time = (TextView) convertView.findViewById(R.id.m_tv_question_time);
            holder.m_tv_user_name = (TextView) convertView.findViewById(R.id.m_tv_user_name);
            holder.m_tv_question_status = (TextView) convertView.findViewById(R.id.m_tv_question_status);
            holder.m_tv_question_count = (TextView) convertView.findViewById(R.id.m_tv_question_count);
            holder.m_iv_question_gold = (RoundImageView) convertView.findViewById(R.id.m_iv_question_gold);
            holder.m_iv_user_icon = (RoundImageView) convertView.findViewById(R.id.m_iv_user_icon);
            holder.m_ll_gold = (LinearLayout) convertView.findViewById(R.id.m_ll_gold);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FeedData feedData = datas.get(position);
        //金币==0不显示
        if(StringUtils.isEquals(feedData.getFeed_extra(),"0")){
            holder.m_ll_gold.setVisibility(View.GONE);
        }else{
            holder.m_ll_gold.setVisibility(View.VISIBLE);
            holder.m_tv_question_gold.setText(feedData.getFeed_extra());
        }
        holder.m_tv_question_content.setText(feedData.getTitle());
        holder.m_tv_question_time.setText(feedData.getAdd_time_str());

        holder.m_tv_user_name.setText(feedData.getName());
        holder.m_tv_question_count.setText(feedData.getTotal_comment() + "个答案");

        finalBitmap.display(holder.m_iv_user_icon, feedData.getHead_img());

        if (feedData.getStatus() == 0) {
            holder.m_tv_question_status.setText("我来答");
            holder.m_tv_question_status.setSelected(true);
            holder.m_tv_question_status.setEnabled(true);
           
        } else if(feedData.getStatus() == 1){
            holder.m_tv_question_status.setText("已采纳");
            holder.m_tv_question_status.setSelected(false);
            holder.m_tv_question_status.setEnabled(false);

        }else {
            holder.m_tv_question_status.setText("已关闭");
            holder.m_tv_question_status.setSelected(false);
            holder.m_tv_question_status.setEnabled(false);
        }
        final Boolean login = SpFileUtil.getBoolean(context,SpFileUtil.LOGIN_STATUS, Constants.LOGIN_STATUS, false);
        holder.m_tv_question_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!login){
                    context.startActivity(new Intent(context,LoginActivity.class));
                    return;
                }else {
                    Intent intent = new Intent(context, FeedAnswerActivity.class);
                    intent.putExtra("feedData", feedData);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView m_tv_question_gold; // 赏金个数
        TextView m_tv_question_content; // 问答内容
        TextView m_tv_question_time; // 问答时间
        TextView m_tv_user_name; // 用户名称
        TextView m_tv_question_status; // 问答状态
        TextView m_tv_question_count; // 问答个数
        RoundImageView m_iv_question_gold;// 赏金图标
        RoundImageView m_iv_user_icon;// 用户头像
        LinearLayout m_ll_gold;//金币布局
    }

}