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

/**
 * 频道列表适配器
 *
 */
public final class ChannelAdapter extends BaseAdapter {

	private Context context;
	private List<String> datas;
	private List<String> userTags;

	private LayoutInflater layoutInflater;
	
	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public ChannelAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<String>();
	}
	
	public void setData(List<String> defaultTags,List<String> userTags) {
		this.datas = defaultTags;
		this.userTags = userTags;
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
			convertView = layoutInflater.inflate(R.layout.channel_item, null);//

			holder = new ViewHolder();

			holder.text_item = (TextView) convertView.findViewById(R.id.text_item);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String tag = datas.get(position);
		if(userTags!=null && userTags.size()>0){
		    if(userTags.contains(tag)){
		        holder.text_item.setSelected(true);
		    }else {
                holder.text_item.setSelected(false);
            }
		}else {
		    holder.text_item.setSelected(false);
        }
		holder.text_item.setText(datas.get(position));
		return convertView;
	}

	private static class ViewHolder {
		TextView text_item; // 名称
	}

}