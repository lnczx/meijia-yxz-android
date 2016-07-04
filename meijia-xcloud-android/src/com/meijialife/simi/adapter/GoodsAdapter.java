package com.meijialife.simi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.Constant;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AssetConsumeActivity;
import com.meijialife.simi.bean.AssetData;
import com.meijialife.simi.bean.AssetJson;
import com.meijialife.simi.bean.AssetJsons;

import java.util.List;

/**
 * Created by bobge on 15/7/31.
 */
public class GoodsAdapter extends BaseAdapter {

    private List<AssetData> list;
    private Context context;
    private CatograyAdapter catograyAdapter;
    private int assetTypeId;

    public GoodsAdapter(Context context, List<AssetData> list1) {
        this.context = context;
        this.list = list1;
    }

    public GoodsAdapter(Context context, List<AssetData> list2, CatograyAdapter catograyAdapter) {
        this.context = context;
        this.list = list2;
        this.catograyAdapter = catograyAdapter;
    }

    public void setData(List<AssetData> list, int assetTypeId) {
        this.list = list;
        this.assetTypeId = assetTypeId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Viewholder viewholder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.right_listview, null);
            viewholder = new Viewholder();
            viewholder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewholder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            viewholder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            viewholder.iv_add = (ImageView) view.findViewById(R.id.iv_add);
            viewholder.iv_remove = (ImageView) view.findViewById(R.id.iv_remove);
            viewholder.et_acount = (EditText) view.findViewById(R.id.et_count);
            view.setTag(viewholder);
        } else if (list != null && list.size() > 0) {

            final AssetData assetData = list.get(position);
            int total = 0;
            AssetJsons jsons = Constants.ASSET_MAP_JSON.get(assetData.getAsset_id());
            if (jsons !=null) {
                total = jsons.getTotal();
            }
            viewholder = (Viewholder) view.getTag();
            viewholder.tv_name.setText(list.get(position).getName());
            viewholder.tv_desc.setText(list.get(position).getSeq());
            viewholder.tv_price.setText(list.get(position).getPrice());
            viewholder.iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int total = 0;
                    Constants.ASSET_COUNT++;//每次点击总数+1
                    AssetJsons jsons = Constants.ASSET_MAP_JSON.get(assetData.getAsset_id());
                    if (jsons != null) {
                        total = jsons.getTotal() + 1;
                        jsons.setTotal(total);
                        Constants.ASSET_MAP_JSON.put(assetData.getAsset_id(), jsons);
                    } else {
                        //添加资产类型
                        total = total + 1;
                        AssetJsons json = new AssetJsons(assetData.getAsset_id(), total, assetData.getName());
                        Constants.ASSET_MAP_JSON.put(assetData.getAsset_id(), json);
                    }

                    viewholder.et_acount.setVisibility(View.VISIBLE);
                    viewholder.iv_remove.setVisibility(View.VISIBLE);
                    viewholder.et_acount.setText(total + "");
                    catograyAdapter.notifyDataSetChanged();

                    int[] startLocation = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                    v.getLocationInWindow(startLocation);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
                    ImageView ball = new ImageView(context);// buyImg是动画的图片，我的是一个小球（R.drawable.sign）
                    ball.setImageResource(R.drawable.ic_launcher);// 设置buyImg的图片
                    ((AssetConsumeActivity) context).setAnim(ball, startLocation);// 开始执行动画

                }
            });
            viewholder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Constants.ASSET_COUNT <= 0) {
                        Constants.ASSET_COUNT = 0;
                    } else {
                        Constants.ASSET_COUNT--;
                    }
                    int total = 0;
                    AssetJsons jsons = Constants.ASSET_MAP_JSON.get(assetData.getAsset_id());
                    if (jsons != null) {
                        total = jsons.getTotal();

                        if (total > 0) {
                            total = total - 1;
                            jsons.setTotal(total);
                            Constants.ASSET_MAP_JSON.put(assetData.getAsset_id(), jsons);
                        } else {
                            Constants.ASSET_MAP_JSON.remove(assetData.getAsset_id() + "");
                        }
                    }


                    if (total == 0) {
                        viewholder.et_acount.setVisibility(View.GONE);
                        viewholder.iv_remove.setVisibility(View.GONE);
                    }
                    //删除资产类型
                    Constants.ASSET_MAP_JSON.remove(assetData.getAsset_id() + "");
                    viewholder.et_acount.setText(total + "");
                    catograyAdapter.notifyDataSetChanged();
                    ((AssetConsumeActivity) context).setAnim();// 更新购物车数量
                }
            });
            //显示选择数量
            if (total <= 0) {
                viewholder.et_acount.setVisibility(View.INVISIBLE);
                viewholder.iv_remove.setVisibility(View.INVISIBLE);
            } else {
                viewholder.et_acount.setVisibility(View.VISIBLE);
                viewholder.iv_remove.setVisibility(View.VISIBLE);
                viewholder.et_acount.setText(total + "");
            }
        }
        return view;
    }

    class Viewholder {
        TextView tv_name;
        TextView tv_desc;
        TextView tv_price;
        ImageView iv_add, iv_remove;
        EditText et_acount;
    }

}
