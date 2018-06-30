package com.meijialife.simi.player.download;

import android.widget.TextView;

import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.exception.DownloadException;

/**
 * 视频下载监听
 */
public class MDownloadListener implements DownloadListener {

    private TextView mTextView;

    public MDownloadListener(TextView textView){
        mTextView = textView;
    }

    @Override
    public void onStart() {
        mTextView.setText("start");
    }

    @Override
    public void onWaited() {
        mTextView.setText("waited");
    }

    @Override
    public void onPaused() {
        mTextView.setText("paused");
    }

    @Override
    public void onDownloading(long progress, long size) {
        mTextView.setText("" + progress + "/" + size);
    }

    @Override
    public void onRemoved() {
        mTextView.setText("removed");
    }

    @Override
    public void onDownloadSuccess() {
        mTextView.setText("已下载");
    }

    @Override
    public void onDownloadFailed(DownloadException e) {
        mTextView.setText("下载失败");
    }
}
