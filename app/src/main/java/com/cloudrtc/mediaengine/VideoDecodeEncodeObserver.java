package com.cloudrtc.mediaengine;

public interface VideoDecodeEncodeObserver {
  void incomingRate(int videoChannel, int framerate, int bitrate);

  // VideoCodecInst.dispose must be called for |videoCodec| before all
  // references to it are lost as it will free memory allocated in the native
  // layer.
  void incomingCodecChanged(int videoChannel, VideoCodecInst videoCodec);

  void requestNewKeyFrame(int videoChannel);

  void outgoingRate(int videoChannel, int framerate, int bitrate);
}
