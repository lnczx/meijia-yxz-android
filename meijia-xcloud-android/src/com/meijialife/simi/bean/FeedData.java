package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：动态、文章、问答实体
 * @author： kerryg
 * @date:2016年6月6日 
 */
public class FeedData implements Serializable {

    private String fid;
    
    private String title;
    
    private String user_id;
    
    private String name;
    
    private String head_img;
    
    private String add_time_str;
    
    private String total_zan;//点赞数量
    
    private String total_comment;//评论数量
    
    private String total_view;//浏览次数
    
    private String feed_extra;
    
    private int status;//2=已关闭，0=我来答
    

    
    
    
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getFeed_extra() {
        return feed_extra;
    }

    public void setFeed_extra(String feed_extra) {
        this.feed_extra = feed_extra;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal_zan() {
        return total_zan;
    }

    public void setTotal_zan(String total_zan) {
        this.total_zan = total_zan;
    }

    public String getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(String total_comment) {
        this.total_comment = total_comment;
    }

    public String getTotal_view() {
        return total_view;
    }

    public void setTotal_view(String total_view) {
        this.total_view = total_view;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
    
    
    
}
