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

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * 我的下载适配器
 */
public final class MyDownloadListAdapter extends BaseAdapter {

	private Context context;
	private List<DownloadInfo> downloadInfoList;
	private LayoutInflater layoutInflater;

	private FinalBitmap finalBitmap;
	private BitmapDrawable defDrawable;

	/**
	 * @param context
	 */
	public MyDownloadListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.downloadInfoList = new ArrayList<>();
		finalBitmap = FinalBitmap.create(context);
		//设置缓存路径和缓存大小
		finalBitmap.configDiskCachePath(context.getFilesDir().toString());
		finalBitmap.configDiskCacheSize(1024 * 1024 * 10);
		defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}
	
	public void setData(List<DownloadInfo> videoDatas) {
	    this.downloadInfoList = videoDatas;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return downloadInfoList.size();
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
		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.my_download_list_item, null);//
			holder = new ViewHolder();
			holder.tv_title = convertView.findViewById(R.id.tv_title);
			holder.tv_summary = convertView.findViewById(R.id.tv_summary);
			holder.iv_icon = convertView.findViewById(R.id.iv_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DownloadInfo videoData = downloadInfoList.get(position);
		holder.tv_title.setText(videoData.getVideoTitle());
		finalBitmap.display(holder.iv_icon, videoData.getVideoImageUrl(), defDrawable.getBitmap(), defDrawable.getBitmap());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_summary;//xx人已看过
		ImageView iv_icon;
	}



}