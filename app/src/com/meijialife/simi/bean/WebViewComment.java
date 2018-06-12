package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：webView评论实体
 * @author： kerryg
 * @date:2016年5月19日 
 */
public class WebViewComment implements Serializable {
    
    private String id;
    
    private String fid;

    private String user_id;
    
    private String name;
    
    private String head_img;
    
    private String add_time_str;//
    
    private String comment;

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

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

  
    
    

}
