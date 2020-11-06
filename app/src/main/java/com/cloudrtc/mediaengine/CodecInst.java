package com.cloudrtc.mediaengine;

public class CodecInst {
  private final long nativeCodecInst;

  // CodecInst can only be created from the native layer.
  private CodecInst(long nativeCodecInst) {
    this.nativeCodecInst = nativeCodecInst;
  }

  public String toString() {
    return name() + " " +
        "PlType: " + plType() + " " +
        "PlFreq: " + plFrequency() + " " +
        "Size: " + pacSize() + " " +
        "Channels: " + channels() + " " +
        "Rate: " + rate();
  }

  // Dispose must be called before all references to CodecInst are lost as it
  // will free memory allocated in the native layer.
  public native void dispose();
  public native int plType();
  public native String name();
  public native int plFrequency();
  public native int pacSize();
  public native int channels();
  public native int rate();
}