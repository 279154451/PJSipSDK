package com.cloudrtc.mediaengine;

public interface VideoStreamObserver {
	
	public void OnIncomingRate(int video_channel, int framerate, int bitrate);

	public void OnOutgoingRate(int video_channel, int framerate, int bitrate);
	
	public void OnVideoFrameSizeChanged(int video_channel, int width, int height);

}
