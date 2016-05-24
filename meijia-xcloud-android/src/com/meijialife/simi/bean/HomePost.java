package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页posts实体
 * @author： kerryg
 * @date:2016年4月6日 
 */
public class HomePost implements Serializable {

    private String id;
    
    private String url;//html链接
    
    private String title;
    
    private String modified;
    
    private String thumbnail;//图片链接
    
    private String content;//文章详情
    

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    
    
}
