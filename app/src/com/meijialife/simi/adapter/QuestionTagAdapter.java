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
import com.meijialife.simi.bean.TagData;
import com.meijialife.simi.utils.StringUtils;

/**
 * 问答互助标签列表适配器
 *
 */
public final class QuestionTagAdapter extends BaseAdapter {

	private Context context;
	private List<TagData> datas;
	private LayoutInflater layoutInflater;
    private boolean tagStatus[];
	
	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public QuestionTagAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<TagData>();
		
	}
	
	public void setData(List<TagData> defaultTags, boolean tagStatus[]) {
		this.datas = defaultTags;
		this.tagStatus = tagStatus;
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
		TagData tag = datas.get(position);
		if(tagStatus!=null){
		    boolean flag = tagStatus[position];
	        if(flag){
	            holder.text_item.setSelected(true);
	        }else {
	            holder.text_item.setSelected(false);
	        }  
		}else {
		    holder.text_item.setSelected(false);
        }
		
		holder.text_item.setText(tag.getTag_name());
		return convertView;
	}

	private static class ViewHolder {
		TextView text_item; // 名称
	}

    public boolean[] getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(boolean[] tagStatus) {
        this.tagStatus = tagStatus;
    }

	

	
}