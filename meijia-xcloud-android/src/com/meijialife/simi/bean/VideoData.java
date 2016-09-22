package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * Created by Lenovo on 2016/9/22.
 */
public class VideoData implements Serializable {

    /**
     * 频道ID
     */
    private String channel_id;
    /**
     * 文章id
     */
    private String article_id;
    /**
     *
     */
    private String title;
    /**
     *
     */
    private String img_url;
    /**
     * 阅读数
     */
    private String total_view;
    /**
     * 时间，格式为MM-dd HH:mm
     */
    private String add_time;
    /**
     * 原价
     */
    private String price;
    /**
     * 现价
     */
    private String dis_price;
    /**
     * 文章详情
     */
    private String content;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 视频连接
     */
    private String video_url;
    /**
     * 优酷云播放视频参数
     */
    private String vid;
    /**
     * 优酷云播放视频参数
     */
    private String cid;
    /**
     * 是否需要弹窗， 如果 = h5 则会弹窗，弹窗内容来自content_desc
     */
    private String category;
    /**
     * 如果需要有弹窗，则此字段为弹窗内容
     */
    private String content_desc;
    /**
     * 如果需要有弹窗，则弹窗后跳转的html页面
     */
    private String goto_url;
    /**
     * 两种情况：
     1. 免费课程       0 = 未参加  1 = 已参加
     2. 收费课程       0 = 未购买  1 = 已购买
     */
    private String is_join;
    /**
     * 购买类参数  服务人员ID
     */
    private String partner_user_id;
    /**
     * 购买类参数  服务大类ID
     */
    private String service_type_id;
    /**
     * 购买类参数  服务课程ID
     */
    private String service_price_id;

    public VideoData(String channel_id, String article_id, String title, String img_url, String total_view, String add_time, String price, String dis_price, String content, String keywords, String video_url, String vid, String cid, String category, String content_desc, String goto_url, String is_join, String partner_user_id, String service_type_id, String service_price_id) {
        this.channel_id = channel_id;
        this.article_id = article_id;
        this.title = title;
        this.img_url = img_url;
        this.total_view = total_view;
        this.add_time = add_time;
        this.price = price;
        this.dis_price = dis_price;
        this.content = content;
        this.keywords = keywords;
        this.video_url = video_url;
        this.vid = vid;
        this.cid = cid;
        this.category = category;
        this.content_desc = content_desc;
        this.goto_url = goto_url;
        this.is_join = is_join;
        this.partner_user_id = partner_user_id;
        this.service_type_id = service_type_id;
        this.service_price_id = service_price_id;
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

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDis_price() {
        return dis_price;
    }

    public void setDis_price(String dis_price) {
        this.dis_price = dis_price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent_desc() {
        return content_desc;
    }

    public void setContent_desc(String content_desc) {
        this.content_desc = content_desc;
    }

    public String getGoto_url() {
        return goto_url;
    }

    public void setGoto_url(String goto_url) {
        this.goto_url = goto_url;
    }

    public String getIs_join() {
        return is_join;
    }

    public void setIs_join(String is_join) {
        this.is_join = is_join;
    }

    public String getPartner_user_id() {
        return partner_user_id;
    }

    public void setPartner_user_id(String partner_user_id) {
        this.partner_user_id = partner_user_id;
    }

    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }

    public String getService_price_id() {
        return service_price_id;
    }

    public void setService_price_id(String service_price_id) {
        this.service_price_id = service_price_id;
    }
}
