package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.UserInfo;

/**
 * 秘书适配器
 *
 */
public class ForWhoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<UserInfo> list;

	public ForWhoAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<UserInfo>();
	}

	public void setData(ArrayList<UserInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.secretary_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_text = (TextView) convertView.findViewById(R.id.item_tv_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getName());
		holder.tv_text.setText(list.get(position).getMobile());
		
		return convertView;
	}
	
	class Holder {
		TextView tv_name;
		TextView tv_text;
	}

}
