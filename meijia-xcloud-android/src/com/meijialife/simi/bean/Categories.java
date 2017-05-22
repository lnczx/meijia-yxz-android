package com.meijialife.simi.bean;

/**
 * 文章猜你喜欢详情的javabean
 */
public class Categories {

    /**
     * id : 67
     * slug : renziguihua
     * title : 人资规划
     * description :
     * parent : 0
     * post_count : 46
     */

    private String id;
    private String slug;
    private String title;
    private String description;
    private int parent;
    private int post_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }
}
