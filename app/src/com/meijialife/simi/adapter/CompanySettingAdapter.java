package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CompanySetting;

/**
 * 
 *  常用工具列表适配器
 */
public final class CompanySettingAdapter extends BaseAdapter {

	private Context context;
	private List<CompanySetting> datas;

	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public CompanySettingAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<CompanySetting>();
	}
	
	public void setData(List<CompanySetting> citys) {
		this.datas = citys;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return datas.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.setting_list_item, null);//

			holder = new ViewHolder();

			holder.m_tv_tool_name = (TextView) convertView.findViewById(R.id.m_tv_tool_name);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.m_tv_tool_name.setText(datas.get(position).getName());
		return convertView;
	}

	private static class ViewHolder {
		TextView m_tv_tool_name; // 地址
	}

	 

}