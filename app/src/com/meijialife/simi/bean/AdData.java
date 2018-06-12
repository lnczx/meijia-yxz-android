package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页广告位实体
 * @author： kerryg
 * @date:2016年4月5日 
 */
public class AdData implements Serializable {


    /**
     * id : 2
     * title : 新手必读，立刻体验
     * app_type : xcloud
     * ad_type : 0,1,
     * service_type_ids : 306
     * img_url : http://img.bolohr.com/2fab695da5d89535f7ed74fa31fcfab7?p=0
     * goto_type : app
     * action : video_detail
     * params : 378
     * goto_url : xcloud://service_type_ids=306&goto_type=app
     * add_time : 1449655652
     * update_time : 1500459365
     * enable : 1
     * no : 9
     */

    private int id;
    private String title;
    private String app_type;
    private String ad_type;
    private String service_type_ids;
    private String img_url;
    private String goto_type;
    private String action;
    private String params;
    private String goto_url;
    private int add_time;
    private int update_time;
    private int enable;
    private int no;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public String getService_type_ids() {
        return service_type_ids;
    }

    public void setService_type_ids(String service_type_ids) {
        this.service_type_ids = service_type_ids;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getGoto_type() {
        return goto_type;
    }

    public void setGoto_type(String goto_type) {
        this.goto_type = goto_type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public int getAdd_time() {
        return add_time;
    }

    public void setAdd_time(int add_time) {
        this.add_time = add_time;
    }

    public int getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
