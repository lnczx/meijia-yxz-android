package com.meijialife.simi.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.R;

/**
 * 搜索-->问答页面
 */
public class SearchFeedFra extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_search_feed, null);

        return v;
    }

}
