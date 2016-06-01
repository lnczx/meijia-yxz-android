package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ChannelAdapter;
import com.meijialife.simi.bean.ChannelData;
import com.meijialife.simi.ui.ChannelGridView;

/**
 *更多频道Activity
 * 
 */
public class ChannelListActivity extends BaseActivity {

 
    private ChannelGridView m_channel_list;
    private List<ChannelData> mChannelData;
    private ChannelAdapter mChannelAdapter;
    private ImageView m_iv_cancle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_channel_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        
        m_channel_list = (ChannelGridView)findViewById(R.id.m_channel_list);
        mChannelData = new ArrayList<ChannelData>(); 
        mChannelAdapter = new ChannelAdapter(ChannelListActivity.this);
        m_iv_cancle = (ImageView)findViewById(R.id.m_iv_cancle);
        
        ChannelData cd1= new ChannelData(1,"招聘");
        ChannelData cd2 = new ChannelData(2,"培训");
        ChannelData cd3 = new ChannelData(3,"招聘");
        ChannelData cd4 = new ChannelData(4,"招聘");
        ChannelData cd5 = new ChannelData(5,"招聘");
         mChannelData.add(cd5);
         mChannelData.add(cd4);
         mChannelData.add(cd3);
         mChannelData.add(cd2);
         mChannelData.add(cd1);
        
        m_channel_list.setAdapter(mChannelAdapter);
        mChannelAdapter.setData(mChannelData);
        
        
        m_channel_list.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
        m_iv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}