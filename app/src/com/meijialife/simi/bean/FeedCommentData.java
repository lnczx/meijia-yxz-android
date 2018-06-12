package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：动态、文章、问答实体
 * @author： kerryg
 * @date:2016年6月6日 
 */
public class FeedCommentData implements Serializable {

    private String id;
    
    private String fid;
    
    private String user_id;

    private String name;

    private String head_img;

    private String comment;
    
    private String total_zan;//点赞数量

    private int is_zan;//当前用户是否已经赞过，仅对已经登陆的用户有效
    
    private int status;//状态 0 = 不显示  1 = 已采纳
    
    private String add_time_str;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotal_zan() {
        return total_zan;
    }

    public void setTotal_zan(String total_zan) {
        this.total_zan = total_zan;
    }

    public int getIs_zan() {
        return is_zan;
    }

    public void setIs_zan(int is_zan) {
        this.is_zan = is_zan;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
}
