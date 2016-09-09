package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 视频列表实体
 */
public class VideoList implements Serializable {

    /**
     * 频道ID
     */
    private String channel_id;
    /**
     * 文章ID
     */
    private String article_id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章图片
     */
    private String img_url;
    /**
     * 阅读数
     */
    private String total_view;

    public VideoList(String channel_id, String article_id, String title, String img_url, String total_view) {
        this.channel_id = channel_id;
        this.article_id = article_id;
        this.title = title;
        this.img_url = img_url;
        this.total_view = total_view;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTotal_view() {
        return total_view;
    }

    public void setTotal_view(String total_view) {
        this.total_view = total_view;
    }
}
