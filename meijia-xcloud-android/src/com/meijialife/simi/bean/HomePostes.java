package com.meijialife.simi.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章详情的javabean
 * add by andye 2016.07.25
 */
public class HomePostes   {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("custom_fields")
    @Expose
    private CustomFields customFields;
    @SerializedName("thumbnail_size")
    @Expose
    private String thumbnailSize;
    @SerializedName("thumbnail_images")
    @Expose
    private ThumbnailImages thumbnailImages;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The modified
     */
    public String getModified() {
        return modified;
    }

    /**
     * @param modified The modified
     */
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     * @return The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return The customFields
     */
    public CustomFields getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields The custom_fields
     */
    public void setCustomFields(CustomFields customFields) {
        this.customFields = customFields;
    }

    /**
     * @return The thumbnailSize
     */
    public String getThumbnailSize() {
        return thumbnailSize;
    }

    /**
     * @param thumbnailSize The thumbnail_size
     */
    public void setThumbnailSize(String thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    /**
     * @return The thumbnailImages
     */
    public ThumbnailImages getThumbnailImages() {
        return thumbnailImages;
    }

    /**
     * @param thumbnailImages The thumbnail_images
     */
    public void setThumbnailImages(ThumbnailImages thumbnailImages) {
        this.thumbnailImages = thumbnailImages;
    }


    public class CustomFields {

        @SerializedName("fromname_value")
        @Expose
        private List<String> fromnameValue = new ArrayList<String>();
        @SerializedName("fromurl_value")
        @Expose
        private List<String> fromurlValue = new ArrayList<String>();
        @SerializedName("keywords")
        @Expose
        private List<String> keywords = new ArrayList<String>();
        @SerializedName("wpa_off")
        @Expose
        private List<Object> wpaOff = new ArrayList<Object>();
        @SerializedName("views")
        @Expose
        private List<String> views = new ArrayList<String>();

        /**
         * @return The fromnameValue
         */
        public List<String> getFromnameValue() {
            return fromnameValue;
        }

        /**
         * @param fromnameValue The fromname_value
         */
        public void setFromnameValue(List<String> fromnameValue) {
            this.fromnameValue = fromnameValue;
        }

        /**
         * @return The fromurlValue
         */
        public List<String> getFromurlValue() {
            return fromurlValue;
        }

        /**
         * @param fromurlValue The fromurl_value
         */
        public void setFromurlValue(List<String> fromurlValue) {
            this.fromurlValue = fromurlValue;
        }

        /**
         * @return The keywords
         */
        public List<String> getKeywords() {
            return keywords;
        }

        /**
         * @param keywords The keywords
         */
        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        /**
         * @return The wpaOff
         */
        public List<Object> getWpaOff() {
            return wpaOff;
        }

        /**
         * @param wpaOff The wpa_off
         */
        public void setWpaOff(List<Object> wpaOff) {
            this.wpaOff = wpaOff;
        }

        /**
         * @return The views
         */
        public List<String> getViews() {
            return views;
        }

        /**
         * @param views The views
         */
        public void setViews(List<String> views) {
            this.views = views;
        }
    }

    public class Full {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

    }

    public class Large {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }
    }

    public class Medium {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

    }

    public class MediumLarge {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

    }


    public class Thumbnail {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

    }

    public class ThumbnailImages {

        @SerializedName("full")
        @Expose
        private Full full;
        @SerializedName("thumbnail")
        @Expose
        private Thumbnail thumbnail;
        @SerializedName("medium")
        @Expose
        private Medium medium;
        @SerializedName("medium_large")
        @Expose
        private MediumLarge mediumLarge;
        @SerializedName("large")
        @Expose
        private Large large;

        /**
         * @return The full
         */
        public Full getFull() {
            return full;
        }

        /**
         * @param full The full
         */
        public void setFull(Full full) {
            this.full = full;
        }

        /**
         * @return The thumbnail
         */
        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        /**
         * @param thumbnail The thumbnail
         */
        public void setThumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
        }

        /**
         * @return The medium
         */
        public Medium getMedium() {
            return medium;
        }

        /**
         * @param medium The medium
         */
        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        /**
         * @return The mediumLarge
         */
        public MediumLarge getMediumLarge() {
            return mediumLarge;
        }

        /**
         * @param mediumLarge The medium_large
         */
        public void setMediumLarge(MediumLarge mediumLarge) {
            this.mediumLarge = mediumLarge;
        }

        /**
         * @return The large
         */
        public Large getLarge() {
            return large;
        }

        /**
         * @param large The large
         */
        public void setLarge(Large large) {
            this.large = large;
        }

    }
}