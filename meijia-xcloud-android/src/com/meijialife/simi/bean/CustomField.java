package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kerryg on 2016/7/11.
 */
public class CustomField implements Serializable {

    private List<String> fromname_value;//文章来源


    public List<String> getFromname_value() {
        return fromname_value;
    }

    public void setFromname_value(List<String> fromname_value) {
        this.fromname_value = fromname_value;
    }
}
