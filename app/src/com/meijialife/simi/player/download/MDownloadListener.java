package com.meijialife.simi.player.download;

import android.widget.TextView;

import java.text.NumberFormat;

import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.exception.DownloadException;

/**
 * 视频下载监听
 */
public class MDownloadListener implements DownloadListener {

    private TextView mTextView;
    private NumberFormat numberFormat;

    public MDownloadListener(TextView textView){
        mTextView = textView;
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
    }

    @Override
    public void onStart() {
        mTextView.setText("");
    }

    @Override
    public void onWaited() {
        //mTextView.setText("");
    }

    @Override
    public void onPaused() {
        //mTextView.setText("");
    }

    @Override
    public void onDownloading(long progress, long size) {
        String progresStr = numberFormat.format(progress / size * 100);
        mTextView.setText("下载中(" + progresStr + "%)");
    }

    @Override
    public void onRemoved() {
        //mTextView.setText("");
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
