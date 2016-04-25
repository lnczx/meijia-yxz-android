package com.easemob.easeui.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 环信聊天记录里的用户信息缓存数据，
 * 用户信息是从后台接口获取的
 * 
 * @author garry
 *
 */
@Table(name="SimiUser")
public class SimiUser implements Serializable {

//	@Id(column="user_id")
	
	public String id;
	
	/** 用户ID **/
	public String user_id;
	
	/** 手机号 **/
	public String mobile;
	
	/** 称呼/昵称 **/
	public String name;
	
	/** 性别: 先生/女士 **/
	public String sex;
	
	/** 头像url **/
	public String head_img;
	
	/** 环信账号名 **/
	public String im_username;
	
	/** 用户类型 0 = 普通用户 1= 秘书 2 = 服务商 **/
	public String user_type;
	
	public SimiUser(){}

	public SimiUser(String user_id, String mobile, String name, String sex,
			String head_img, String im_username, String user_type) {
		super();
		this.user_id = user_id;
		this.mobile = mobile;
		this.name = name;
		this.sex = sex;
		this.head_img = head_img;
		this.im_username = im_username;
		this.user_type = user_type;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getIm_username() {
		return im_username;
	}

	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	
	
}
