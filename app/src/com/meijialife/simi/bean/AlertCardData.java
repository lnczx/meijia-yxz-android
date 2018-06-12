package com.meijialife.simi.bean;



public class AlertCardData {
    
    
    private String alert_id;//卡片Id
    
    private String service_time;//提醒时间
    
    private String cycle_type_id;//周期类型
    
    private String inteval_time;//时间间隔


   
    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public String getCycle_type_id() {
        return cycle_type_id;
    }

    public void setCycle_type_id(String cycle_type_id) {
        this.cycle_type_id = cycle_type_id;
    }

    public String getInteval_time() {
        return inteval_time;
    }

    public void setInteval_time(String inteval_time) {
        this.inteval_time = inteval_time;
    }
    
    

}
