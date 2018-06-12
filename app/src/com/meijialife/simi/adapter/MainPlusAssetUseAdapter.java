package com.meijialife.simi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.AssetJson;
import com.meijialife.simi.bean.AssetUseData;
import com.meijialife.simi.ui.RoundImageView;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加号--资产列表---适配器
 */
public class MainPlusAssetUseAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<AssetUseData> AssetUseDatas;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;


    public MainPlusAssetUseAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        AssetUseDatas = new ArrayList<AssetUseData>();
        contexts = context;
        finalBitmap = FinalBitmap.create(context);
        //获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    public void setData(ArrayList<AssetUseData> AssetUseDataList) {
        this.AssetUseDatas = AssetUseDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return AssetUseDatas.size();
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
            convertView = inflater.inflate(R.layout.main_plus_use_asset_item, null);
            holder.m_asset_title = (TextView) convertView.findViewById(R.id.m_asset_title);
            holder.m_add_time = (TextView) convertView.findViewById(R.id.m_add_time);
            holder.m_asset_count = (TextView) convertView.findViewById(R.id.m_asset_count);
            holder.m_ll_asset_json = (ListView) convertView.findViewById(R.id.m_ll_asset_json);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        AssetUseData assetUseData = AssetUseDatas.get(position);
        Gson gson = new Gson();
        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        int count = 0;
        List<AssetJson> list = gson.fromJson(assetUseData.getAsset_json(), new TypeToken<ArrayList<AssetJson>>() {
        }.getType());
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                AssetJson json = list.get(i);
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("name", json.getName());
                listem.put("total", json.getTotal());
                count+=json.getTotal();
                listems.add(listem);
            }

            SimpleAdapter simplead = new SimpleAdapter(contexts, listems,
                    R.layout.asset_json_item, new String[]{"name", "total"},
                    new int[]{ R.id.m_tv_asset_name, R.id.m_tv_asset_total});
            holder.m_ll_asset_json.setAdapter(simplead);
        }
        holder.m_asset_title.setText(assetUseData.getName() + "领用");
        holder.m_add_time.setText(assetUseData.getAdd_time() + "");
        if(count>0){
            holder.m_asset_count.setText("共:" +count+"件");
        }else{
            holder.m_asset_count.setVisibility(View.GONE);
        }
        return convertView;
    }

    class Holder {
        TextView m_asset_title;//资产名称
        TextView m_add_time;//添加时间
        TextView m_asset_count;//总数量
        ListView m_ll_asset_json;//所选的子类型
    }
}
