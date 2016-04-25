package com.umeng.comm.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.fragments.FollowedTopicFragment;
import com.umeng.comm.ui.fragments.TopicsPickFragment;

/**
 * Created by wangfei on 16/1/25.
 */
public class TopicPickActivity extends BaseTitleActivity{
    @Override
    protected int getContentView() {
        return ResFinder.getLayout("umeng_comm_followed_topic_layout");
    }
    @Override
    protected void initTitleLayout() {
        super.initTitleLayout();
        mTitleTextView.setText(ResFinder.getString("umeng_comm_topic"));
    }
    @Override
    protected void initFragment() {
        addFragment(ResFinder.getId("umeng_comm_user_followed_container"),
                createFragment());
    }

    private Fragment createFragment() {

        return TopicsPickFragment.newRecommendTopicFragment();
    }
}
