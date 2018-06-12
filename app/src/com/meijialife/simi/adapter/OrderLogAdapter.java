package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.OrderLog;

/**
 * @description：订单进度适配器
 * @author： kerryg
 * @date:2015年11月14日
 */
public class OrderLogAdapter extends BaseAdapter {

    //定义全局变量
    private LayoutInflater inflater;
    private List<OrderLog> datas;
    private FinalBitmap finalBitmap;


    public OrderLogAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        datas = new ArrayList<OrderLog>();
        finalBitmap = FinalBitmap.create(context);

    }

    public void setData(List<OrderLog> passUsersList) {
        this.datas = passUsersList;
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
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.order_log_item, null);
            holder.m_order_log_status = (TextView) convertView.findViewById(R.id.m_order_log_status);
            holder.m_order_log_time = (TextView) convertView.findViewById(R.id.m_order_log_time);
            holder.m_iv_point = (ImageView) convertView.findViewById(R.id.m_iv_point);
            holder.m_iv_black_point = (ImageView) convertView.findViewById(R.id.m_iv_black_point);
            holder.m_v_line = convertView.findViewById(R.id.m_v_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final OrderLog passUsersData = datas.get(position);
        holder.m_order_log_status.setText(passUsersData.getRemarks());
        holder.m_order_log_time.setText(passUsersData.getAdd_time_str());

        if (position == (datas.size() - 1)) {
            holder.m_v_line.setVisibility(View.GONE);
        }
        if (position == 0) {
            holder.m_v_line.setBackgroundResource(R.color.simi_color_green);
            holder.m_iv_black_point.setVisibility(View.GONE);
            holder.m_iv_point.setVisibility(View.VISIBLE);
        } else {
            holder.m_v_line.setBackgroundResource(R.color.simi_color_order_line_black);
            holder.m_iv_black_point.setVisibility(View.VISIBLE);
            holder.m_iv_point.setVisibility(View.GONE);
        }
        return convertView;
    }

    class Holder {
        ImageView m_iv_point;
        ImageView m_iv_black_point;
        TextView m_order_log_status;
        TextView m_order_log_time;
        View m_v_line;
    }
}
