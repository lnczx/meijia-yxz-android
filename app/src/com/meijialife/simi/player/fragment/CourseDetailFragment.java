package com.meijialife.simi.player.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.adapter.VideoRelateListAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.VideoData;
import com.meijialife.simi.bean.VideoList;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.player.CourseActivity;
import com.meijialife.simi.ui.VideoPopWindow;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.ToActivityUtil;
import com.meijialife.simi.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailFragment extends BaseFragment implements View.OnClickListener{

    public static final String INTENT_KEY_LABEL = "keyLabel";
    private String label;
    private User user;
    private VideoData video;//视频详细信息

    private LinearLayout ll_all;
    private TextView tv_vname;//课程名称
    private TextView tv_tname;//讲师
    private TextView tv_count;//阅读数量
    private TextView tv_price;//价格
    private TextView tv_orig_price;//原价
    private TextView tv_exchange;//金币兑换代金券
    private TextView tv_more;//了解更多
    private TextView tv_detail;//概述

    /**
     * 相关视频列表
     */
    private List<VideoList> videoDatas;
    private VideoRelateListAdapter videoAdapter;
    private ListView listView;

    public static CourseDetailFragment getInstace(String label) {
        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_LABEL, label);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = View.inflate(getActivity(),
                R.layout.fragment_course_detail, null);
        if (getArguments() != null) {
            label = getArguments().getString(INTENT_KEY_LABEL);
        }
        user = DBHelper.getUser(getActivity());

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
        ll_all = (LinearLayout) rootView.findViewById(R.id.ll_all);
        tv_vname = (TextView) rootView.findViewById(R.id.tv_vname);
        tv_tname = (TextView) rootView.findViewById(R.id.tv_tname);
        tv_count = (TextView) rootView.findViewById(R.id.tv_count);
        tv_price = (TextView) rootView.findViewById(R.id.tv_price);
        tv_orig_price = (TextView) rootView.findViewById(R.id.tv_orig_price);
        tv_orig_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_exchange = (TextView) rootView.findViewById(R.id.tv_exchange);
        tv_more = (TextView) rootView.findViewById(R.id.tv_more);
        tv_detail = (TextView) rootView.findViewById(R.id.tv_detail);

        tv_exchange.setOnClickListener(this);
        tv_more.setOnClickListener(this);
    }

    private void initListView(View rootView) {
        videoDatas = new ArrayList<VideoList>();
        videoAdapter = new VideoRelateListAdapter(getActivity());
        listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(videoAdapter);
        UIUtils.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*player.destroyVideo();
                getVideoDetail(videoDatas.get(position).getArticle_id());*/
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("videoListData", videoDatas.get(position));
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void setVideo(VideoData video) {
        this.video = video;
        showData();
    }

    public void setVideoDatas(List<VideoList> videoDatas) {
        this.videoDatas = videoDatas;
        videoAdapter.setData(videoDatas);
        UIUtils.setListViewHeightBasedOnChildren(listView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_exchange://金币兑换代金券
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intent6 = new Intent();
                intent6.setClass(getActivity(), PointsShopActivity.class);
                intent6.putExtra("navColor", "#E8374A");    //配置导航条的背景颜色，请用#ffffff长格式。
                intent6.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
                intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(getActivity()).getUser_id());    //配置自动登陆地址，每次需服务端动态生成。
                startActivity(intent6);
                break;
            case R.id.tv_more://了解更多
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                if (video != null) {
                    ToActivityUtil.gotoWebPage(getActivity(),"详情",video.getVideo_more_url());
                } else {
                    Toast.makeText(getActivity(), "数据错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showData() {
        if (video == null) {
            return;
        }
        tv_vname.setText(video.getTitle());
        tv_tname.setText("讲师:" + video.getTeacher());
        tv_count.setText(video.getTotal_view() + " 人学过");
        tv_price.setText("￥" + video.getDis_price());
        tv_orig_price.setText("￥" + video.getPrice());
        if (isHtml(video.getContent())) {
            tv_detail.setText(Html.fromHtml(video.getContent()));
        } else if (StringUtils.isNotEmpty(video.getContent())) {
            tv_detail.setText(video.getContent());
        }

        if (video.getCategory() != null && video.getCategory().trim().equals("h5")) {
            //弹窗
            VideoPopWindow popWindow = new VideoPopWindow(getActivity(), "提醒", video.getContent_desc(), video.getGoto_url(), video.getArticle_id());
            popWindow.showPopupWindow(ll_all);
        }
    }

    /**
     * 判断是否包含html代码
     *
     * @param text
     * @return
     */
    private boolean isHtml(String text) {
        if (text.contains("<div") || text.contains("<span") || text.contains("<h1")
                || text.contains("style=") || text.contains("font-size") || text.contains("color:")
                || text.contains("<p") || text.contains("<br") || text.contains("</") || text.contains("<pre")) {
            return true;
        } else {
            return false;
        }
    }
}