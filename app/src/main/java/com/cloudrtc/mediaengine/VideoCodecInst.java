package com.cloudrtc.mediaengine;

public class VideoCodecInst {
  private final long nativeCodecInst;

  // VideoCodecInst can only be created from the native layer.
  private VideoCodecInst(long nativeCodecInst) {
    this.nativeCodecInst = nativeCodecInst;
  }

  public String toString() {
    return name() + " " +
        "PlType: " + plType() + " " +
        "Width: " + width() + " " +
        "Height: " + height() + " " +
        "StartBitRate: " + startBitRate() + " " +
        "MaxFrameRate: " + maxFrameRate();
  }

  // Dispose must be called before all references to VideoCodecInst are lost as
  // it will free memory allocated in the native layer.
  public native void dispose();
  public native int plType();
  public native String name();
  public native int width();
  public native void setWidth(int width);
  public native int height();
  public native void setHeight(int height);
  public native int startBitRate();
  public native void setStartBitRate(int bitrate);
  public native int maxBitRate();
  public native void setMaxBitRate(int bitrate);
  public native int maxFrameRate();
  public native void setMaxFrameRate(int framerate);
}