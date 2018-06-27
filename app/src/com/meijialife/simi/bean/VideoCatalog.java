package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 视频目录实体
 */
public class VideoCatalog implements Serializable {

    /***/
    private String video_id;
    /***/
    private int service_type_id;
    /***/
    private int service_price_id;
    /***/
    private int no;
    /***/
    private String title;
    /***/
    private String img_url;
    /***/
    private String video_url;
    /***/
    private int from_aliyun;
    /***/
    private String remark;
    /***/
    private String from_user_id;
    /***/
    private String from_user_name;
    /***/
    private String from_user_mobile;
    /***/
    private int status;
    /***/
    private String add_time;
    /***/
    private String update_time;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(int service_type_id) {
        this.service_type_id = service_type_id;
    }

    public int getService_price_id() {
        return service_price_id;
    }

    public void setService_price_id(int service_price_id) {
        this.service_price_id = service_price_id;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getFrom_aliyun() {
        return from_aliyun;
    }

    public void setFrom_aliyun(int from_aliyun) {
        this.from_aliyun = from_aliyun;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_user_name() {
        return from_user_name;
    }

    public void setFrom_user_name(String from_user_name) {
        this.from_user_name = from_user_name;
    }

    public String getFrom_user_mobile() {
        return from_user_mobile;
    }

    public void setFrom_user_mobile(String from_user_mobile) {
        this.from_user_mobile = from_user_mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
