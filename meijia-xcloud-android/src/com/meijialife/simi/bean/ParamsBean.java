package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页文章咨询参数实体
 * @author： kerryg
 * @date:2016年5月20日 
 */
public class ParamsBean implements Serializable {

    private String json;
    
    private String count;
    
    private String order;
    
    private String id;
    
    private String include;
    
    private String slug;
    
    

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }
    
   
    
    
    
}
