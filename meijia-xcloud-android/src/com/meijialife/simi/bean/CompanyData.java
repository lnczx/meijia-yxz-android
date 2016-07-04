package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：公司Bean
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class CompanyData implements Serializable {
    
    private String company_id;//公司Id
    private String company_name;//公司名称
    private int is_default;//默认企业1==默认，0==不默认


    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getCompany_id() {
        return company_id;
    }
    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }
    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
    public CompanyData(String company_id, String company_name) {
        super();
        this.company_id = company_id;
        this.company_name = company_name;
    }
    public CompanyData() {
        super();
    }
    
    
    

}
