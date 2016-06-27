package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页posts实体
 * @author： kerryg
 * @date:2016年4月6日 
 */
public class HomePost extends ArticleResultData implements Serializable {

    
    private String modified;
    
    
    private String content;//文章详情


    public String getModified() {
        return modified;
    }


    public void setModified(String modified) {
        this.modified = modified;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }
    

    
    
}
