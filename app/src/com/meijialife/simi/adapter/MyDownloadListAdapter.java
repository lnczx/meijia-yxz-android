package com.meijialife.simi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
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

	/**
	 * @param context
	 */
	public MyDownloadListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.downloadInfoList = new ArrayList<>();
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
			holder.tv_title = convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DownloadInfo videoData = downloadInfoList.get(position);
		holder.tv_title.setText(videoData.getPath());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
	}



}