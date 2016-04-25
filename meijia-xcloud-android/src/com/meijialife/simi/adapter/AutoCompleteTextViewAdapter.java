package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.meijialife.simi.R;

/**
 * 添加地址时，地图页AutoCompleteTextView联想词的适配器
 * 
 */
public class AutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {
	private ArrayFilter mFilter;
	private ArrayList<PoiInfo> mList;
	private Context context;
	private ArrayList<PoiInfo> mUnfilteredData;
	
	public AutoCompleteTextViewAdapter(Context context) {
		mList = new ArrayList<>();
		this.context = context;
	}
	
	public void setData(ArrayList<PoiInfo> mList){
		this.mList = mList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		
		return mList==null ? 0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(convertView==null){
			view = View.inflate(context, R.layout.auto_complete_textview_item, null);
			
			holder = new ViewHolder();
			holder.tv_name = (TextView) view.findViewById(R.id.item_name);
			holder.tv_address = (TextView) view.findViewById(R.id.item_address);
			holder.m_ll_checked = (LinearLayout) view.findViewById(R.id.m_ll_checked);
			
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		PoiInfo pi = mList.get(position);
		if(position == selectedItem){
		    holder.m_ll_checked.setVisibility(View.VISIBLE);
		}else {
            holder.m_ll_checked.setVisibility(View.GONE);
        }
		
		holder.tv_name.setText(pi.name);
		holder.tv_address.setText(pi.address);
		
		return view;
	}
	
	static class ViewHolder{
		public TextView tv_name;
		public TextView tv_address;
		public LinearLayout m_ll_checked;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<PoiInfo>(mList);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<PoiInfo> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<PoiInfo> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<PoiInfo> newValues = new ArrayList<PoiInfo>(count);

                for (int i = 0; i < count; i++) {
                	PoiInfo pi = unfilteredValues.get(i);
                    if (pi != null) {
                        
                    	if(pi.name!=null && pi.name.startsWith(prefixString)){
                    		
                    		newValues.add(pi);
                    	}else if(pi.address!=null && pi.address.startsWith(prefixString)){
                    		
                    		newValues.add(pi);
                    	}
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			 //noinspection unchecked
            mList = (ArrayList<PoiInfo>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
	}
	
	private int selectedItem;

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
	
}
