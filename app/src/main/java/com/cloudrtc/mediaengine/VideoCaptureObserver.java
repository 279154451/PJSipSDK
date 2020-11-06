package com.cloudrtc.mediaengine;

public interface VideoCaptureObserver{
	public void OnCaptureFrame(byte[] data, int rotation, long captureMs);
}