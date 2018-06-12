package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：标签实体
 * @author： kerryg
 * @date:2016年6月6日 
 */
public class TagData implements Serializable {

    private String tag_id;
    
    private String tag_name;
    
    private String tag_type;//标签类型 0 = 用户 1 = 秘书 2 = 服务商 3= 问答

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_type() {
        return tag_type;
    }

    public void setTag_type(String tag_type) {
        this.tag_type = tag_type;
    }

    
}
