package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * Created by RUI on 2017/8/13 0013.
 */

public class SecretaryRatesData implements Serializable {

    private int id;

    private String head_img;
    private String name;

    private double rate;

    private String rate_content;

    private String add_time_str;

    public SecretaryRatesData(int id, String head_img, String name, double rate, String rate_content, String add_time_str) {
        this.id = id;
        this.head_img = head_img;
        this.name = name;
        this.rate = rate;
        this.rate_content = rate_content;
        this.add_time_str = add_time_str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getRate_content() {
        return rate_content;
    }

    public void setRate_content(String rate_content) {
        this.rate_content = rate_content;
    }

    public String getAdd_time_str() {
        return add_time_str;
    }

    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
}
