package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：首页posts实体
 * @author： kerryg
 * @date:2016年4月6日 
 */
public class HomePosts extends HomePost implements Serializable {

    
    private CustomFields custom_fields;

    public CustomFields getCustom_fields() {
        return custom_fields;
    }

    public void setCustom_fields(CustomFields custom_fields) {
        this.custom_fields = custom_fields;
    }
}
