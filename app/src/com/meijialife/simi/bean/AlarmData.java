package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：常用提醒实体
 * @author： kerryg
 * @date:2016年6月4日 
 */
public class AlarmData implements Serializable {

    private String setting_id;//
    
    private String name;
    
    private String alarm_day;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetting_id() {
        return setting_id;
    }

    public void setSetting_id(String setting_id) {
        this.setting_id = setting_id;
    }

    public String getAlarm_day() {
        return alarm_day;
    }

    public void setAlarm_day(String alarm_day) {
        this.alarm_day = alarm_day;
    }

  
    
    
}
