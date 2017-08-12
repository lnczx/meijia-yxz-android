package com.meijialife.simi.fra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.VideoListAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的收藏-->视频页面
 */
public class MyCollectionVideoFra extends BaseFragment {

    private List<VideoList> videoDatas;
    private List<VideoList> allVideoDatas;
    private VideoListAdapter adapter;
    private PullToRefreshListView mListView;

    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_collection_video, null);

        initView(v);

        getVideoList(page);

        return v;
    }

    private void initView(View v) {
        videoDatas = new ArrayList<VideoList>();
        allVideoDatas = new ArrayList<VideoList>();
        adapter = new VideoListAdapter(getActivity());
        mListView = (PullToRefreshListView) v.findViewById(R.id.m_trial_listview);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        initIndicator();

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getVideoList(page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (videoDatas != null && videoDatas.size() >= 10) {
                    page = page + 1;
                    getVideoList(page);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("videoListData", allVideoDatas.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 获得视频文章列表
     */
    public void getVideoList(int page) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = DBHelper.getUser(getActivity());

        Map<String, String> map = new HashMap<String, String>();

        if(user != null){
            map.put("user_id", user.getId());
        }
        map.put("keyword", "");
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_FAVORITES_VIDEO_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        LogOut.i("onSuccess", t.toString());
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (StringUtils.isEquals(status, "0")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                videoDatas = gson.fromJson(data, new TypeToken<ArrayList<VideoList>>() {
                                }.getType());
                                showData(videoDatas);
                            } else {

                                mListView.onRefreshComplete();
                            }
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mListView.onRefreshComplete();
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    private void showData(List<VideoList> videoDatas) {
        if (videoDatas != null && videoDatas.size() > 0) {
            if (page == 1) {
                allVideoDatas.clear();
            }
            for (VideoList videoData : videoDatas) {
                allVideoDatas.add(videoData);
            }
            adapter.setData(allVideoDatas);
        }else{
            if(page == 1){
                allVideoDatas.clear();
                adapter.setData(allVideoDatas);
            }
        }
        mListView.onRefreshComplete();
    }

}
