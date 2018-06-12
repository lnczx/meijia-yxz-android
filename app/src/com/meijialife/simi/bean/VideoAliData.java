package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * Created by Lenovo on 2016/9/22.
 */
public class VideoAliData implements Serializable {


    /**
     * need_playauth : 1
     * video_url : fd7358acac724a3aa6bac0e6cf86135e
     * vid : fd7358acac724a3aa6bac0e6cf86135e
     */

    private int need_playauth;
    private String video_url;
    private String vid;

    public int getNeed_playauth() {
        return need_playauth;
    }

    public void setNeed_playauth(int need_playauth) {
        this.need_playauth = need_playauth;
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
}
