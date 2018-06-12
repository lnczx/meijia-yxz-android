package com.meijialife.simi.bean;

import java.util.List;

/**
 * 相关文章和新闻bean
 * Created by yejiurui on 2017/5/22.
 */

public class HomeAboutNewsBean {


    /**
     * status : ok
     * count : 5
     * pages : 18
     * category : {"id":62,"slug":"zhaopin","title":"招聘","description":"","parent":0,"post_count":90}
     * posts : [{"id":12952,"url":"http://bolohr.com/zhaopin/hr12952.html","title":"离职面谈，有哪些可能问到的问题","modified":"2017-05-17 23:58:02","thumbnail":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png","custom_fields":{"fromname_value":["互联网"],"wpa_off":[""],"views":["5718","5718"]},"thumbnail_size":"thumbnail","thumbnail_images":{"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-300x155.png","width":300,"height":155},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330}}},{"id":13235,"url":"http://bolohr.com/zhaopin/hr13235.html","title":"HR日常面试规范与礼仪","modified":"2017-05-15 23:33:50","thumbnail":"http://bolohr.com/wp-content/uploads/2017/05/blob-29-150x150.png","custom_fields":{"fromname_value":["互联网"],"wpa_off":[""],"views":["4719"]},"thumbnail_size":"thumbnail","thumbnail_images":{"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-29.png","width":500,"height":333},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-29-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-29-300x200.png","width":300,"height":200},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-29.png","width":500,"height":333},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-29.png","width":500,"height":333}}},{"id":13289,"url":"http://bolohr.com/zhaopin/hr13289.html","title":"听听资深HR对于招聘面试的忠告","modified":"2017-05-14 22:28:39","thumbnail":"http://bolohr.com/wp-content/uploads/2017/05/blob-28-150x150.png","custom_fields":{"fromurl_value":["http://News.hrsalon.org/36571.html"],"fromname_value":["互联网"],"wpa_off":[""],"views":["4686"]},"thumbnail_size":"thumbnail","thumbnail_images":{"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-28.png","width":535,"height":369},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-28-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-28-300x207.png","width":300,"height":207},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-28.png","width":535,"height":369},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-28.png","width":535,"height":369}}},{"id":3964,"url":"http://bolohr.com/zhichang/hr3964.html","title":"面试者与求职者都应该注意自己的网上声誉啦","modified":"2017-05-12 09:23:03","thumbnail":"http://bolohr.com/wp-content/uploads/2017/05/blob-23-150x150.png","custom_fields":{"fromname_value":["互联网"],"wpa_off":[""],"views":["4002"]},"thumbnail_size":"thumbnail","thumbnail_images":{"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-23.png","width":558,"height":381},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-23-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-23-300x205.png","width":300,"height":205},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-23.png","width":558,"height":381},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-23.png","width":558,"height":381}}},{"id":13339,"url":"http://bolohr.com/zhaopin/hr13339.html","title":"饭局面试是什么一种现象","modified":"2017-05-22 12:15:10","thumbnail":"http://bolohr.com/wp-content/uploads/2017/05/blob-18-150x150.png","custom_fields":{"fromname_value":["互联网"],"wpa_off":[""],"views":["2112"]},"thumbnail_size":"thumbnail","thumbnail_images":{"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-18.png","width":550,"height":367},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-18-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-18-300x200.png","width":300,"height":200},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-18.png","width":550,"height":367},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-18.png","width":550,"height":367}}}]
     */

    private String status;
    private int count;
    private int pages;
    private CategoryBean category;
    private List<PostsBean> posts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public List<PostsBean> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsBean> posts) {
        this.posts = posts;
    }

    public static class CategoryBean {
        /**
         * id : 62
         * slug : zhaopin
         * title : 招聘
         * description :
         * parent : 0
         * post_count : 90
         */

        private int id;
        private String slug;
        private String title;
        private String description;
        private int parent;
        private int post_count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

    public static class PostsBean {
        /**
         * id : 12952
         * url : http://bolohr.com/zhaopin/hr12952.html
         * title : 离职面谈，有哪些可能问到的问题
         * modified : 2017-05-17 23:58:02
         * thumbnail : http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png
         * custom_fields : {"fromname_value":["互联网"],"wpa_off":[""],"views":["5718","5718"]}
         * thumbnail_size : thumbnail
         * thumbnail_images : {"full":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330},"thumbnail":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png","width":150,"height":150},"medium":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-300x155.png","width":300,"height":155},"medium_large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330},"large":{"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330}}
         */

        private String id;
        private String url;
        private String title;
        private String modified;
        private String thumbnail;
        private CustomFieldsBean custom_fields;
        private String thumbnail_size;
        private ThumbnailImagesBean thumbnail_images;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public CustomFieldsBean getCustom_fields() {
            return custom_fields;
        }

        public void setCustom_fields(CustomFieldsBean custom_fields) {
            this.custom_fields = custom_fields;
        }

        public String getThumbnail_size() {
            return thumbnail_size;
        }

        public void setThumbnail_size(String thumbnail_size) {
            this.thumbnail_size = thumbnail_size;
        }

        public ThumbnailImagesBean getThumbnail_images() {
            return thumbnail_images;
        }

        public void setThumbnail_images(ThumbnailImagesBean thumbnail_images) {
            this.thumbnail_images = thumbnail_images;
        }

        public static class CustomFieldsBean {
            private List<String> fromname_value;
            private List<String> wpa_off;
            private List<String> views;

            public List<String> getFromname_value() {
                return fromname_value;
            }

            public void setFromname_value(List<String> fromname_value) {
                this.fromname_value = fromname_value;
            }

            public List<String> getWpa_off() {
                return wpa_off;
            }

            public void setWpa_off(List<String> wpa_off) {
                this.wpa_off = wpa_off;
            }

            public List<String> getViews() {
                return views;
            }

            public void setViews(List<String> views) {
                this.views = views;
            }
        }

        public static class ThumbnailImagesBean {
            /**
             * full : {"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330}
             * thumbnail : {"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png","width":150,"height":150}
             * medium : {"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37-300x155.png","width":300,"height":155}
             * medium_large : {"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330}
             * large : {"url":"http://bolohr.com/wp-content/uploads/2017/05/blob-37.png","width":640,"height":330}
             */

            private FullBean full;
            private ThumbnailBean thumbnail;
            private MediumBean medium;
            private MediumLargeBean medium_large;
            private LargeBean large;

            public FullBean getFull() {
                return full;
            }

            public void setFull(FullBean full) {
                this.full = full;
            }

            public ThumbnailBean getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(ThumbnailBean thumbnail) {
                this.thumbnail = thumbnail;
            }

            public MediumBean getMedium() {
                return medium;
            }

            public void setMedium(MediumBean medium) {
                this.medium = medium;
            }

            public MediumLargeBean getMedium_large() {
                return medium_large;
            }

            public void setMedium_large(MediumLargeBean medium_large) {
                this.medium_large = medium_large;
            }

            public LargeBean getLarge() {
                return large;
            }

            public void setLarge(LargeBean large) {
                this.large = large;
            }

            public static class FullBean {
                /**
                 * url : http://bolohr.com/wp-content/uploads/2017/05/blob-37.png
                 * width : 640
                 * height : 330
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }
            }

            public static class ThumbnailBean {
                /**
                 * url : http://bolohr.com/wp-content/uploads/2017/05/blob-37-150x150.png
                 * width : 150
                 * height : 150
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }
            }

            public static class MediumBean {
                /**
                 * url : http://bolohr.com/wp-content/uploads/2017/05/blob-37-300x155.png
                 * width : 300
                 * height : 155
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }
            }

            public static class MediumLargeBean {
                /**
                 * url : http://bolohr.com/wp-content/uploads/2017/05/blob-37.png
                 * width : 640
                 * height : 330
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }
            }

            public static class LargeBean {
                /**
                 * url : http://bolohr.com/wp-content/uploads/2017/05/blob-37.png
                 * width : 640
                 * height : 330
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }
            }
        }
    }
}
