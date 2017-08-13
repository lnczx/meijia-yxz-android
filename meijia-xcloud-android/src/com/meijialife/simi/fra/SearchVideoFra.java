package com.meijialife.simi.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.R;

/**
 * 搜索-->视频页面 （同首页试听课程一样）
 */
public class SearchVideoFra extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_search_video, null);

        return v;
    }

}
