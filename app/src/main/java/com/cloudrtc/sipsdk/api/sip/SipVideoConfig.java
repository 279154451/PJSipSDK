package com.cloudrtc.sipsdk.api.sip;

/**
 * 创建时间：2020/11/4
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipVideoConfig {
    private int width;
    private int height;
    private int fps;
    private  int bitrate;

    public SipVideoConfig(int width, int height, int fps, int bitrate) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.bitrate = bitrate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFps() {
        return fps;
    }

    public int getBitrate() {
        return bitrate;
    }
}
