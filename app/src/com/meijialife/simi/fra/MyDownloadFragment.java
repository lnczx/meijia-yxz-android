package com.meijialife.simi.fra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.MyApplication;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyDownloadListAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 *  我的下载
 */
public class MyDownloadFragment extends BaseFragment {

    public static final String INTENT_KEY_LABEL = "keyLabel";
    private String label;
    private User user;

    private List<DownloadInfo> downloadInfoList;
    private MyDownloadListAdapter mAdapter;
    private ListView listView;

    private DownloadManager downloadManager;

    public static MyDownloadFragment getInstace(String label) {
        MyDownloadFragment fragment = new MyDownloadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_LABEL, label);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = View.inflate(getActivity(),
                R.layout.fragment_my_download_list, null);
        if (getArguments() != null) {
            label = getArguments().getString(INTENT_KEY_LABEL);
        }
        user = DBHelper.getUser(getActivity());
        downloadManager = DownloadService.getDownloadManager(MyApplication.applicationContext);

        initView(mView);
        initListView(mView);
        setVideoDatas();
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化界面
     *
     * @param rootView
     */
    private void initView(View rootView) {

    }

    private void initListView(View rootView) {
        downloadInfoList = new ArrayList<>();
        mAdapter = new MyDownloadListAdapter(getActivity());
        listView = rootView.findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
        UIUtils.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DownloadInfo downloadInfo = downloadInfoList.get(position);
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("videoId", downloadInfo.getVideoId());
                startActivity(intent);
            }
        });
    }

    public void setVideoDatas() {
        this.downloadInfoList = downloadManager.findAllDownloaded();
        mAdapter.setData(downloadInfoList);
        UIUtils.setListViewHeightBasedOnChildren(listView);
    }

}