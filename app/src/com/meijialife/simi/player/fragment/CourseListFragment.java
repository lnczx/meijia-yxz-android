package com.meijialife.simi.player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.VideoCatalogListAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoCatalog;
import com.meijialife.simi.bean.VideoData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.player.CourseActivity;
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
 *  目录
 */
public class CourseListFragment extends BaseFragment {

    public static final String INTENT_KEY_LABEL = "keyLabel";
    private String label;
    private User user;
    private VideoData video;//视频详细信息

    private List<VideoCatalog> videoDatas;
    private VideoCatalogListAdapter videoAdapter;
    private ListView listView;

    public static CourseListFragment getInstace(String label) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_LABEL, label);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = View.inflate(getActivity(),
                R.layout.fragment_course_list, null);
        if (getArguments() != null) {
            label = getArguments().getString(INTENT_KEY_LABEL);
        }

        initView(mView);
        initListView(mView);
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
        videoDatas = new ArrayList<>();
        videoAdapter = new VideoCatalogListAdapter(getActivity());
        listView = rootView.findViewById(R.id.listview);
        listView.setAdapter(videoAdapter);
        UIUtils.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoCatalog catalog = videoDatas.get(position);
                ((CourseActivity)getActivity()).onCatalogClick(catalog);
            }
        });
    }

    public void setVideo(VideoData video) {
        this.video = video;
        getVideoList();
    }

    public void setVideoDatas(List<VideoCatalog> videoDatas) {
        this.videoDatas = videoDatas;
        if(videoAdapter != null) {
            videoAdapter.setData(videoDatas, video.getIs_join());
            UIUtils.setListViewHeightBasedOnChildren(listView);
        }
    }

    /**
     * 获得视频目录
     */
    public void getVideoList() {
        user = DBHelper.getUser(getActivity());
        if (video == null || user == null) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("article_id", video.getArticle_id());//文章id
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_VIDEO_SUB_LIST, param, new AjaxCallBack<Object>() {
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
                                List<VideoCatalog> videoDatas = gson.fromJson(data, new TypeToken<ArrayList<VideoCatalog>>() {
                                }.getType());

                                for (VideoCatalog catalog : videoDatas){
                                    catalog.setImg_url(video.getImg_url());
                                }
                                setVideoDatas(videoDatas);
                            } else {
                                //无相关课程
//                                errorMsg = getString(R.string.servers_error);
                            }
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

}