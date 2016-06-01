package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：订单日志实体
 * @author： kerryg
 * @date:2016年5月31日 
 */
public class OrderLog implements Serializable {

    private Integer id;
    
    private Integer user_id;
    
    private String order_id;
    
    private String order_no;
    
    private short order_status;
    
    private String remarks;//
    
    private String add_time_str;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public short getOrder_status() {
        return order_status;
    }

    public void setOrder_status(short order_status) {
        this.order_status = order_status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    
    
    
}
