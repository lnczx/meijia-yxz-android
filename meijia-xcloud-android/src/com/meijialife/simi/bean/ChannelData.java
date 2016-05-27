package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 
 * @description：首页频道实体
 * @author： kerryg
 * @date:2016年5月27日
 */
public class ChannelData implements Serializable {
    
    
    private Integer id;
    
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelData(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public ChannelData() {
        super();
    }
    
    
    
    
}
