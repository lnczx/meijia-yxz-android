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
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

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

	/**是否参加该课程 1=已参加*/
	private int isJoin;
	/**当前播放的视频*/
	private String currentVideoUrl;

	private DownloadManager downloadManager;

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

	/**
	 *
	 * @param videoDatas
	 * @param isJoin 是否参加该课程 1=已参加
	 */
	public void setData(List<VideoCatalog> videoDatas, int isJoin) {
	    this.videoDatas = videoDatas;
	    this.isJoin = isJoin;
		notifyDataSetChanged();
	}

	public void setCurrentVideoUrl(String currentVideoUrl) {
		this.currentVideoUrl = currentVideoUrl;
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
		if(StringUtils.isEquals(currentVideoUrl, videoData.getVideo_url())){
			holder.tv_title.setTextColor(context.getResources().getColor(R.color.video_list_playing_text_color));
		}else{
			holder.tv_title.setTextColor(context.getResources().getColor(R.color.index_second_title_color));
		}

		if(isJoin == 1){
			DownloadInfo downloadInfo = downloadManager.getDownloadById(videoData.getVideo_url().hashCode());
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
				holder.tv_download.setTextColor(context.getResources().getColor(R.color.video_list_playing_text_color));
			}

			holder.tv_download.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = videoData.getVideo_url();
					if(StringUtils.isEmpty(url)){
						UIUtils.showToast(context,"视频下载链接为空");
						return;
					}
					String name = videoData.getTitle() + url.substring(url.lastIndexOf("."), url.length());
					String path = Constants.PATH_VIDEO_CACHE + File.separator + name;

					DownloadInfo downloadInfo = downloadManager.getDownloadById(url.hashCode());
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
		}else{
			holder.tv_download.setText("");
		}

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_download; //下载
	}



}