package com.meijialife.simi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CompanyApply;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.inter.ListItemClickHelpers;
import com.meijialife.simi.inter.ListItemClickHelpes;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

/**
 * 团队申请适配器
 */
public class CompanyApplyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<CompanyApply> CompanyApplys;

	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelpers callback;//回调方法


	public CompanyApplyAdapter(Context context, ListItemClickHelpers callback) {
		inflater = LayoutInflater.from(context);
		CompanyApplys = new ArrayList<CompanyApply>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        this.callback = callback;
	}

	public void setData(ArrayList<CompanyApply> CompanyApplys) {
		this.CompanyApplys = CompanyApplys;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return CompanyApplys.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.company_apply_item, null);
			holder.m_tv_company_name = (TextView) convertView.findViewById(R.id.m_tv_company_name);
			holder.m_tv_apply_name = (TextView) convertView.findViewById(R.id.m_tv_apply_name);
			holder.m_center_add = (TextView)convertView.findViewById(R.id.m_center_add);
			holder.m_center_add_name = (TextView)convertView.findViewById(R.id.m_center_add_name);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final CompanyApply companyApply = CompanyApplys.get(position);

		
        holder.m_tv_company_name.setText(companyApply.getCompany_name());
        holder.m_tv_apply_name.setText("提供者:"+companyApply.getName());
        final short status = companyApply.getStatus();

        if(status==0){//0==申请，1==同意，2==拒绝
            holder.m_center_add.setVisibility(View.VISIBLE);
			holder.m_center_add_name.setVisibility(View.GONE);
            holder.m_center_add.setSelected(true);
        }else if (status==1){
            holder.m_center_add.setVisibility(View.GONE);
			holder.m_center_add_name.setVisibility(View.VISIBLE);
			holder.m_center_add.setSelected(false);
        }
        holder.m_center_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyApply CompanyApply = CompanyApplys.get(position);
                callback.onClick(companyApply);
            }
        });
        return convertView;
	}
	

	
	class Holder {
		TextView  m_tv_company_name;
		TextView  m_tv_apply_name;
		TextView  m_center_add;
		TextView m_center_add_name;
	}
}
