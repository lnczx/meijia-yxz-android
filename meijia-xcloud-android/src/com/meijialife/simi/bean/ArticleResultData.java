package com.meijialife.simi.bean;

import java.io.Serializable;

public class ArticleResultData implements Serializable {
    
    private String id;
    
    private String url;//html链接
    
    private String title;
    
    private String thumbnail;//图片链接

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    

}
