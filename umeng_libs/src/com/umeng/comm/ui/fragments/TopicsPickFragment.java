package com.umeng.comm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.TopicDetailActivity;
import com.umeng.comm.ui.adapters.PickTopicAdapter;
import com.umeng.comm.ui.imagepicker.adapters.RecommendTopicAdapter;
import com.umeng.comm.ui.imagepicker.listener.TopicToTopicDetail;
import com.umeng.comm.ui.imagepicker.presenter.impl.RecommendTopicPresenter;
import com.umeng.comm.ui.imagepicker.presenter.impl.TopicPickPresenter;

/**
 * Created by wangfei on 16/1/25.
 */
public class TopicsPickFragment extends RecommendTopicFragment{
    public static TopicsPickFragment newRecommendTopicFragment() {
        return new TopicsPickFragment();
    }
    protected void initTitleView(View rootView) {
        findViewById(ResFinder.getId("umeng_comm_divider")).setVisibility(View.GONE);

    }
    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_recommend");
    }
    @Override
    protected TopicPickPresenter createPresenters() {
        return new TopicPickPresenter(this);
    }
    @Override
    protected void initWidgets() {
        super.initWidgets();
    }

    @Override
    protected void initAdapter() {
        PickTopicAdapter adapter = new PickTopicAdapter(getActivity());
        adapter.setFromFindPage(!mSaveButtonVisiable);
        mAdapter = adapter;
        mTopicListView.setAdapter(mAdapter);
        setAdapterGotoDetail();
    }

    @Override
    protected void setAdapterGotoDetail() {
        ((RecommendTopicAdapter)mAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                Intent intent = new Intent();
                intent.putExtra(Constants.TAG_TOPIC, topic);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }
}
