package com.meijialife.simi.bean;


public class CardZanViewVo extends CardZan {

	private String name;
	
	private String headImg;

	@Override
    public String getName() {
		return name;
	}

	@Override
    public void setName(String name) {
		this.name = name;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
}
