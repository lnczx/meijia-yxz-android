package com.meijialife.simi.player.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * fragment viewpage Adapter
 * Created by yejiurui on 2017/9/21.
 * Mail:yejiurui@gmail.com
 */

public class FragmentAdapterUtils extends FragmentPagerAdapter {

    private List<String> titleList;
    private List<Fragment> fragmentList;

    public FragmentAdapterUtils(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}