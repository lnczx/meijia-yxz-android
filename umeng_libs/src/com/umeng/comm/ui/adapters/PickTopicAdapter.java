package com.umeng.comm.ui.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.imagepicker.adapters.RecommendTopicAdapter;
import com.umeng.comm.ui.imagepicker.adapters.viewholders.RecommendTopicViewHolder;
import com.umeng.comm.ui.imagepicker.presenter.impl.RecommendTopicPresenter;

/**
 * Created by wangfei on 16/1/25.
 */
public class PickTopicAdapter extends RecommendTopicAdapter{
    /**
     * 推荐话题的显示样式跟推荐用户的样式相同
     *
     * @param context
     */
    public PickTopicAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setItemData(int position, final RecommendTopicViewHolder viewHolder, View rootView) {

        super.setItemData(position,viewHolder,rootView);
        viewHolder.mToggleButton.setVisibility(View.GONE);


    }
}
