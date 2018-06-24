package com.meijialife.simi.database.bean;

import com.google.gson.annotations.SerializedName;
import com.meijialife.simi.bean.ExpressTypeData;
import com.meijialife.simi.bean.XcompanySetting;

import java.io.Serializable;
import java.util.List;

/**
 * @description：基础数据实体
 * @author： kerryg
 * @date:2016年4月16日
 */
public class BaseData implements Serializable {

  private List<XcompanySetting> asset_types;

  private List<ExpressTypeData> express;

  private List<AppTools> apptools;

  private List<OpAd> opads;

  private List<City> city;
  /** youliao-redpoint : 1 opads : [] apptools : [] express : [] asset_types : [] city : [] */
  @SerializedName("youliao-redpoint")
  private int youliaoredpoint;

  public List<City> getCity() {
    return city;
  }

  public void setCity(List<City> city) {
    this.city = city;
  }

  public List<OpAd> getOpads() {
    return opads;
  }

  public void setOpads(List<OpAd> opads) {
    this.opads = opads;
  }

  public List<AppTools> getApptools() {
    return apptools;
  }

  public void setApptools(List<AppTools> apptools) {
    this.apptools = apptools;
  }

  public List<XcompanySetting> getAsset_types() {
    return asset_types;
  }

  public void setAsset_types(List<XcompanySetting> asset_types) {
    this.asset_types = asset_types;
  }

  public List<ExpressTypeData> getExpress() {
    return express;
  }

  public void setExpress(List<ExpressTypeData> express) {
    this.express = express;
  }

  public int getYouliaoredpoint() {
    return youliaoredpoint;
  }

  public void setYouliaoredpoint(int youliaoredpoint) {
    this.youliaoredpoint = youliaoredpoint;
  }
}
