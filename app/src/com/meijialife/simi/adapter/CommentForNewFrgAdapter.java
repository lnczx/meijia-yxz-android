package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.WebViewComment;
import com.meijialife.simi.ui.RoundImageView;
/**
 * @description：首页WebView评论列表适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class CommentForNewFrgAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<WebViewComment> list;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;

	public CommentForNewFrgAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<WebViewComment>();
		finalBitmap = FinalBitmap.create(context);
		this.context = context;
		//默认图标赋值
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.order_icon);
	}

	public void setData(ArrayList<WebViewComment> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
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
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.comment_list_item, null);
			holder.m_iv_icon = (RoundImageView)convertView.findViewById(R.id.m_iv_icon);
			holder.m_tv_name = (TextView) convertView.findViewById(R.id.m_tv_name);
			holder.m_tv_comment = (TextView)convertView.findViewById(R.id.m_comment_time);
			holder.m_tv_zan = (TextView)convertView.findViewById(R.id.m_tv_zan);
			holder.m_comment_time = (TextView)convertView.findViewById(R.id.m_comment_time);
			holder.m_comment_content = (TextView)convertView.findViewById(R.id.m_comment_content);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		
	    holder.m_tv_name.setText(list.get(position).getName());
	    holder.m_comment_time.setText(list.get(position).getAdd_time_str());
	    holder.m_comment_content.setText(list.get(position).getComment());
	    finalBitmap.display(holder.m_iv_icon,list.get(position).getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
		
        return convertView;
	}
	
	class Holder {
	    RoundImageView m_iv_icon;
		TextView m_tv_name;
		TextView m_tv_comment;//评论
		TextView m_tv_zan;//点赞
		TextView m_comment_time;
		TextView m_comment_content;
	}

}
