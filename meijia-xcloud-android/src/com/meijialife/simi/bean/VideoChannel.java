package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * 视频列表的频道实体
 */
public class VideoChannel implements Serializable {

    /**
     * 频道ID
     */
    private String channel_id;
    /**
     *
     */
    private String name;

    public VideoChannel(String channel_id, String name) {
        this.channel_id = channel_id;
        this.name = name;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
