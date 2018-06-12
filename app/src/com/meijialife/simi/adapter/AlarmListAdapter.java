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
import com.meijialife.simi.bean.AlarmData;
import com.meijialife.simi.utils.StringUtils;

/**
 * 常用提醒列表适配器
 *
 */
public final class AlarmListAdapter extends BaseAdapter {

	private Context context;
	private List<AlarmData> datas;

	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public AlarmListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<AlarmData>();
	}
	
	public void setData(List<AlarmData> alarmDatas) {
		this.datas = alarmDatas;
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
			convertView = layoutInflater.inflate(R.layout.alarm_list_item, null);//

			holder = new ViewHolder();

			holder.m_alarm_name = (TextView) convertView.findViewById(R.id.m_alarm_name);
			holder.m_alarm_day = (TextView) convertView.findViewById(R.id.m_alarm_day);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AlarmData alarmData = datas.get(position);
		holder.m_alarm_name.setText(alarmData.getName());
		if(StringUtils.isEquals(alarmData.getAlarm_day(),"0")){
		    holder.m_alarm_day.setText("不提醒");
		}else {
		    holder.m_alarm_day.setText(alarmData.getAlarm_day()+"天前");
        }
		return convertView;
	}

	private static class ViewHolder {
		TextView m_alarm_name; 
		TextView m_alarm_day; 
	}

	 

}