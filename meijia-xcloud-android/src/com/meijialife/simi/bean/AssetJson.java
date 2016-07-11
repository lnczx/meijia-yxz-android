package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：领用列表中资产类型实体(大类)
 * @author： kerryg
 * @date:2016年4月1日
 */
public class AssetJson implements Serializable {


    private String name;//资产名称

    private String price;// 单价

    private int total;// 数量

    private String asset_id;// 资产Id

    private String asset_name;

    private String total_price;// 总价

    private String asset_type_id;// 资产类别Id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getAsset_name() {
        return asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getAsset_type_id() {
        return asset_type_id;
    }

    public void setAsset_type_id(String asset_type_id) {
        this.asset_type_id = asset_type_id;
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

}
