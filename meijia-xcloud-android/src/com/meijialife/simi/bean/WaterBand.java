package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：送水服务商实体
 * @author： kerryg
 * @date:2016年3月4日 
 */
public class WaterBand implements Serializable {

    private String name;//商品名称

    private String service_price_id;//商品价格Id
    
    private String price;//原价
    
    private String dis_price;//折扣价
    
    private String img_url;//商品图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getService_price_id() {
        return service_price_id;
    }

    public void setService_price_id(String service_price_id) {
        this.service_price_id = service_price_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDis_price() {
        return dis_price;
    }

    public void setDis_price(String dis_price) {
        this.dis_price = dis_price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    
    
}
