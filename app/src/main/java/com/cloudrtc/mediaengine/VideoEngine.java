package com.cloudrtc.mediaengine;

import android.view.SurfaceView;

public class VideoEngine {

  public VideoEngine() {
  }

  public native int StartSending(int camera_index, int rotation, SurfaceView local_renderer);
  public native int StopSending();
  public native int StartReceiving(SurfaceView remote_renderer);
  public native int StopReceiving();
  public native int getVideoChannel();
  public native int connectAudioChannel(int videoChannel, int voiceChannel);
  public native int numberOfCodecs();
  
  public native int RegisterVideoStreamObserver(VideoStreamObserver cb);
  public native int DeRegisterVideoStreamObserver();
  
  public native int setReceiveCodec();
  public native int setSendCodec(int StartBitRate, int Width, int Height, int MaxFrameRate);
  
  //public native int getOrientation(CameraDesc camera);
  public native int setVideoRotations(int cameraId, int degrees);
  public native int setNackStatus(int channel, boolean enable);

  public native int SetupVideoChannel(int width, int height, int fps, int bitrate);

  public native void ChangeCamera(int camera_index, int rotation_new, SurfaceView local_renderer);
  public native int GetCameraOrientation(int cam_index);
  public native void ChangeCaptureRotation(int rotation);
  
  public native int RegisterVideoCaptureObserver(VideoCaptureObserver cb);
  public native int DeRegisterVideoCaptureObserver();
 // public native void SetVideoCodec(String video_codecs);
}
