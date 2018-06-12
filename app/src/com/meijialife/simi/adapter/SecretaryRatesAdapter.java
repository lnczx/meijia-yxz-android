package com.meijialife.simi.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.WebViewPartnerActivity;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.SecretaryRatesData;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.StringUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 秘书服务评价适配器
 * 
 */
public class SecretaryRatesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<SecretaryRatesData> mList;
    private Context context;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    public SecretaryRatesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mList = new ArrayList<SecretaryRatesData>();
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    /**
     * 
     */
    public void setData(List<SecretaryRatesData> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void addData(List<SecretaryRatesData> mList){
        this.mList.addAll(mList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
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
            convertView = inflater.inflate(R.layout.secraty_rates_item, null);
            holder.item_iv_icon = (RoundImageView) convertView.findViewById(R.id.item_iv_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        SecretaryRatesData data = mList.get(position);
        holder.tv_name.setText(data.getName());
        holder.ratingbar.setRating((float) data.getRate());
        holder.tv_content.setText(data.getRate_content());
        finalBitmap.display(holder.item_iv_icon,data.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());

        return convertView;
    }

    class Holder {
        RoundImageView item_iv_icon;
        TextView tv_name;
        RatingBar ratingbar;
        TextView tv_content;
    }

    private ProgressDialog m_pDialog;

    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

}
