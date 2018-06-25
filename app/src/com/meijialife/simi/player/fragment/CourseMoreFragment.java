package com.meijialife.simi.player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.R;

public class CourseMoreFragment extends BaseFragment {

    public static final String INTENT_KEY_LABEL = "keyLabel";

    private String label;

    public static CourseMoreFragment getInstace(String label) {
        CourseMoreFragment fragment = new CourseMoreFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_LABEL, label);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = View.inflate(getActivity(),
                R.layout.fragment_course_more, null);
        if (getArguments() != null) {
            label = getArguments().getString(INTENT_KEY_LABEL);
        }

        initView(mView);
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
}