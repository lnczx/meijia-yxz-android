package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.FeedDetailActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.MainPlusSignActivity;
import com.meijialife.simi.activity.MainPlusSignInActivity;
import com.meijialife.simi.bean.FeedCommentData;
import com.meijialife.simi.bean.FeedData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelper;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.AlertWindow;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;

/**
 * 文章、动态、问答评论列表适配器
 *
 */
public final class FeedCommentAdapter extends BaseAdapter {

    private Context context;
    private List<FeedCommentData> datas;

    private LayoutInflater layoutInflater;
    private FinalBitmap finalBitmap;

    private ListItemClickHelper helper;
    private FeedData feedData;

    /**
     * @param context上下文
     * @param 数据列表
     * @param showDel
     *            是否显示删除按钮
     */
    public FeedCommentAdapter(Context context, FeedDetailActivity activity) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.datas = new ArrayList<FeedCommentData>();
        finalBitmap = FinalBitmap.create(context);
        helper = activity;
    }

    public void setData(List<FeedCommentData> feedDatas, FeedData feedData) {
        this.datas = feedDatas;
        this.feedData = feedData;
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
            convertView = layoutInflater.inflate(R.layout.feed_comment_list_item, null);//

            holder = new ViewHolder();

            holder.m_tv_name = (TextView) convertView.findViewById(R.id.m_tv_name);
            holder.m_tv_time = (TextView) convertView.findViewById(R.id.m_tv_time);
            holder.m_tv_content = (TextView) convertView.findViewById(R.id.m_tv_content);
            holder.m_tv_zan = (TextView) convertView.findViewById(R.id.m_tv_zan);
            holder.m_tv_accepte = (TextView) convertView.findViewById(R.id.m_tv_accepte);
            holder.m_rv_header = (RoundImageView) convertView.findViewById(R.id.m_rv_header);
            holder.m_iv_zan = (ImageView) convertView.findViewById(R.id.m_iv_zan);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FeedCommentData feedCommentData = datas.get(position);
        holder.m_tv_name.setText(feedCommentData.getName());
        holder.m_tv_time.setText(feedCommentData.getAdd_time_str());
        holder.m_tv_content.setText(feedCommentData.getComment());
        holder.m_tv_zan.setText(feedCommentData.getTotal_zan() + "");
        finalBitmap.display(holder.m_rv_header, feedCommentData.getHead_img());

        User user = DBHelper.getUser(context);
        if (user == null) {//用户未登录
            if(feedCommentData.getStatus()==1){//此评论已被采纳--显示
                holder.m_tv_accepte.setText("已采纳");
                holder.m_tv_accepte.setSelected(true);
                holder.m_tv_accepte.setEnabled(false);
                holder.m_tv_accepte.setVisibility(View.VISIBLE);
            }else {//此评论未被采纳---不显示
                holder.m_tv_accepte.setVisibility(View.INVISIBLE);
            }
        } else {
            if(feedData.getStatus() == 0){//进行中
                //登录者和回答问题者是同一个人，
                if(StringUtils.isEquals(user.getId(), feedCommentData.getUser_id())){
                    holder.m_tv_accepte.setVisibility(View.INVISIBLE);
                }else{//显示采纳按钮
                    //提问是当前用户则显示采纳按钮
                    if(StringUtils.isEquals(feedData.getUser_id(),user.getId())){
                        holder.m_tv_accepte.setText("采纳");
                        holder.m_tv_accepte.setSelected(false);
                        holder.m_tv_accepte.setEnabled(true);
                        holder.m_tv_accepte.setVisibility(View.VISIBLE);
                    }else {
                        holder.m_tv_accepte.setVisibility(View.INVISIBLE);
                    }
                }
            }else if(feedData.getStatus() == 1){//已采纳
                //登录者和回答问题者是同一个人，
                if(StringUtils.isEquals(user.getId(), feedCommentData.getUser_id())){
                   if(feedCommentData.getStatus()==0){
                       holder.m_tv_accepte.setVisibility(View.INVISIBLE);

                   }else  {
                       holder.m_tv_accepte.setText("已采纳");
                       holder.m_tv_accepte.setSelected(true);
                       holder.m_tv_accepte.setEnabled(false);
                       holder.m_tv_accepte.setVisibility(View.VISIBLE);
                   }
                }else{
                    
                    if(feedCommentData.getStatus()==0){
                        holder.m_tv_accepte.setVisibility(View.INVISIBLE);

                    }else  {
                        holder.m_tv_accepte.setText("已采纳");
                        holder.m_tv_accepte.setSelected(true);
                        holder.m_tv_accepte.setEnabled(false);
                        holder.m_tv_accepte.setVisibility(View.VISIBLE);
                    }
                   
                }
                
            }else if(feedData.getStatus() == 2){//已关闭
                holder.m_tv_accepte.setVisibility(View.INVISIBLE);
            }
        }
        if (feedCommentData.getIs_zan() > 0) {
            holder.m_iv_zan.setSelected(true);
        } else if (feedCommentData.getIs_zan() == 0) {
            holder.m_iv_zan.setSelected(false);
        }
        final boolean flag = holder.m_iv_zan.isSelected();
        holder.m_iv_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = "add";
                if (flag) {
                    action = "del";
                } else {
                    action = "add";
                }
                helper.onClick(feedCommentData.getId(), action, true);
            }
        });
        holder.m_tv_accepte.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                AlertWindow.dialog(context, "确认采纳","确认采纳此答案吗？", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        helper.onClick(feedCommentData.getId(), feedCommentData.getFid(), false);
                    }
                }, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView m_tv_name; // 用户名字
        TextView m_tv_time; // 问答时间
        TextView m_tv_content; // 问答内容
        TextView m_tv_zan; // 赞个数
        TextView m_tv_accepte; // 采纳按钮
        RoundImageView m_rv_header;// 用户头像
        ImageView m_iv_zan;
    }

}