package com.meijialife.simi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.MyApplication;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.VideoCatalog;
import com.meijialife.simi.photo.util.FileUtils;
import com.meijialife.simi.player.download.MDownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * 视频目录列表适配器
 */
public final class VideoCatalogListAdapter extends BaseAdapter {

	private Context context;
	private List<VideoCatalog> videoDatas;
	private LayoutInflater layoutInflater;

	private DownloadManager downloadManager;
	private DownloadInfo downloadInfo;

	/**
	 * @param context
	 */
	public VideoCatalogListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.videoDatas = new ArrayList<>();
		downloadManager = DownloadService.getDownloadManager(MyApplication.applicationContext);
		FileUtils.createFolder(Constants.PATH_VIDEO_CACHE);
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
		final ViewHolder holder;
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


		downloadInfo = downloadManager.getDownloadById(videoData.getVideo_url().hashCode());
		holder.tv_download.setText("");
		if(downloadInfo != null){
			//已在下载任务，重新绑定监听
			if(downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED){
				holder.tv_download.setText("已下载");
			}else if(downloadInfo.getStatus() == DownloadInfo.STATUS_DOWNLOADING){
				holder.tv_download.setText("下载中");
			}
			downloadInfo.setDownloadListener(new MDownloadListener(holder.tv_download));
			holder.tv_download.setTextColor(context.getResources().getColor(R.color.common_input_text_color));
		}else {
			holder.tv_download.setText("下载");
			holder.tv_download.setTextColor(context.getResources().getColor(R.color.simi_color_red));
		}

		holder.tv_download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = videoData.getVideo_url();
				String name = videoData.getTitle() + url.substring(url.lastIndexOf("."), url.length());
				String path = Constants.PATH_VIDEO_CACHE + File.separator + name;

				if (downloadInfo != null) {
					//已在下载队列中，根据状态处理
					switch (downloadInfo.getStatus()) {
						case DownloadInfo.STATUS_NONE:
						case DownloadInfo.STATUS_PAUSED:
						case DownloadInfo.STATUS_ERROR:
							downloadManager.resume(downloadInfo);
							break;
					}
				} else {
					//新建下载任务
					downloadInfo = new DownloadInfo.Builder()
							.setUrl(url)
							.setPath(path)
							.setVideoId(String.valueOf(videoData.getService_price_id()))
							.setVideoTitle(videoData.getTitle())
							.setVideoImageUrl(videoData.getImg_url())
							.build();
					downloadInfo.setDownloadListener(new MDownloadListener(holder.tv_download));
					downloadManager.download(downloadInfo);
					holder.tv_download.setTextColor(context.getResources().getColor(R.color.common_input_text_color));
				}
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_download; //下载
	}



}