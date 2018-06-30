package com.meijialife.simi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.VideoCatalog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 视频目录列表适配器
 */
public final class VideoCatalogListAdapter extends BaseAdapter {

	private Context context;
	private List<VideoCatalog> videoDatas;
	private LayoutInflater layoutInflater;

	/**
	 * @param context
	 */
	public VideoCatalogListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.videoDatas = new ArrayList<>();
	}
	
	public void setData(List<VideoCatalog> videoDatas) {
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
			convertView = layoutInflater.inflate(R.layout.video_catalog_list_item, null);//
			holder = new ViewHolder();
			holder.tv_title = convertView.findViewById(R.id.tv_name);
			holder.tv_download = convertView.findViewById(R.id.tv_download_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final VideoCatalog videoData = videoDatas.get(position);
		holder.tv_title.setText(videoData.getTitle());
		holder.tv_download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = videoData.getVideo_url();
				String path = Constants.PATH_VIDEO_CACHE + File.separator + videoData.getTitle();

				Toast.makeText(context, "download", Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_download; //下载
	}



}