package com.meijialife.simi.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.utils.StringUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;


/**
 * 相关视频列表适配器
 */
public final class VideoRelateListAdapter extends BaseAdapter {

	private Context context;
	private List<VideoList> videoDatas;
	private LayoutInflater layoutInflater;

	private FinalBitmap finalBitmap;
	private BitmapDrawable defDrawable;
	/**
	 * @param context
	 */
	public VideoRelateListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.videoDatas = new ArrayList<VideoList>();
		  finalBitmap = FinalBitmap.create(context);
		  //设置缓存路径和缓存大小
		  finalBitmap.configDiskCachePath(context.getFilesDir().toString());
		  finalBitmap.configDiskCacheSize(1024*1024*10);
		  defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	}
	
	public void setData(List<VideoList> videoDatas) {
	    this.videoDatas = videoDatas;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return videoDatas.size();
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
			convertView = layoutInflater.inflate(R.layout.video_relate_list_item, null);//
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.msg_tv_title);
			holder.tv_summary = (TextView) convertView.findViewById(R.id.msg_tv_summary);
			holder.iv_iconUrl = (ImageView) convertView.findViewById(R.id.msg_iv_icon_url);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	
		VideoList videoData = videoDatas.get(position);
		holder.tv_title.setText(videoData.getTitle());
		String total_view = videoData.getTotal_view();
		if(StringUtils.isNotEmpty(total_view)){
		    holder.tv_summary.setText(total_view + "人已看过");
		}else {
            holder.tv_summary.setText("0人已看过");
        }
        finalBitmap.display(holder.iv_iconUrl,videoData.getImg_url(),defDrawable.getBitmap(),defDrawable.getBitmap());
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_summary; //阅读数量
		ImageView iv_iconUrl; //图片

	}

	 

}