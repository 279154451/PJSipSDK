package com.cloudrtc.mediaengine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera;
import android.media.AudioManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class MediaEngine {
  // TODO(henrike): Most of these should be moved to xml (since static).
  private static final int VCM_VP8_PAYLOAD_TYPE = 100;
  private static final int SEND_CODEC_FPS = 30;
  // TODO(henrike): increase INIT_BITRATE_KBPS to 2000 and ensure that
  // 720p30fps can be acheived (on hardware that can handle it). Note that
  // setting 2000 currently leads to failure, so that has to be resolved first.
  private static final int INIT_BITRATE_KBPS = 500;
  private static final int MAX_BITRATE_KBPS = 3000;
  private static final int WIDTH_IDX = 0;
  private static final int HEIGHT_IDX = 1;
  private static final int[][] RESOLUTIONS = {
    {176,144}, {320,240}, {352,288}, {640,480}, {1280,720}
  };
  public static int numberOfResolutions() { return RESOLUTIONS.length; }

  public static String[] resolutionsAsString() {
    String[] retVal = new String[numberOfResolutions()];
    for (int i = 0; i < numberOfResolutions(); ++i) {
      retVal[i] = RESOLUTIONS[i][0] + "x" + RESOLUTIONS[i][1];
    }
    return retVal;
  }

  // Checks for and communicate failures to user (logcat and popup).
  private void check(boolean value, String message) {
    if (value) {
      return;
    }
    Log.e("WEBRTC-CHECK", message);
    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle("WebRTC Error");
    alertDialog.setMessage(message);
    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
        "OK",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            return;
          }
        }
                          );
    alertDialog.show();
  }

  // Converts device rotation to camera rotation. Rotation depends on if the
  // camera is back facing and rotate with the device or front facing and
  // rotating in the opposite direction of the device.
  private static int rotationFromRealWorldUp(CameraInfo info,
                                             int deviceRotation) {
    int coarseDeviceOrientation =
        (int)(Math.round((double)deviceRotation / 90) * 90) % 360;
    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
      // The front camera rotates in the opposite direction of the
      // device.
      int inverseDeviceOrientation = 360 - coarseDeviceOrientation;
      return (inverseDeviceOrientation + info.orientation) % 360;
    }
    return (coarseDeviceOrientation + info.orientation) % 360;
  }

  // Shared Audio/Video members.
  private final Context context;
  // Audio
  private VoiceEngine voe;
  private int audioChannel;
  private boolean audioEnabled;
  private boolean voeRunning;
  private int audioCodecIndex;
  private boolean speakerEnabled;
  private boolean enableAgc;
  private boolean enableNs;
  private boolean enableAecm;

  // Video
  private VideoEngine vie;
  private int videoChannel;
  private boolean receiveVideo;
  private boolean sendVideo;
  private boolean vieRunning;
  private int videoCodecIndex;
  private int resolutionIndex;
  // Indexed by CameraInfo.CAMERA_FACING_{BACK,FRONT}.
  private CameraInfo cameras[];
  private boolean useFrontCamera;
  private int currentCameraHandle;
  private boolean enableNack;
  // openGl, surfaceView or mediaCodec (integers.xml)
  private int viewSelection;
  private SurfaceView svLocal;
  private SurfaceView svRemote;
  MediaCodecVideoDecoder externalCodec;

  private int inWidth;
  private int inHeight;

  private OrientationEventListener orientationListener;
  private int deviceOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

  public MediaEngine(Context context) {
    this.context = context;
    voe = new VoiceEngine();  
    vie = new VideoEngine();
  /*  cameras = new CameraInfo[2];
    CameraInfo info = new CameraInfo();
    for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
      Camera.getCameraInfo(i, info);
      cameras[info.facing] = info;
    }
    setDefaultCamera();
    check(voe.setAecmMode(VoiceEngine.AecmModes.SPEAKERPHONE, false) == 0,
        "VoE set Aecm speakerphone mode failed");
    
    orientationListener =
        new OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {
          public void onOrientationChanged (int orientation) {
            deviceOrientation = orientation;
            compensateRotation();
          }
        };
    orientationListener.enable();
    
    // Set audio mode to communication
   // AudioManager audioManager =
     //   ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
    //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);*/
  }

  public void dispose() {
    check(!voeRunning && !voeRunning, "Engines must be stopped before dispose");
    orientationListener.disable();
  }

  public void start() {
    if (audioEnabled) {
      startVoE();
    }
    if (receiveVideo || sendVideo) {
      startViE();
    }
  }

  public void stop() {
    stopVoe();
    stopVie();
  }

  public boolean isRunning() {
    return voeRunning || vieRunning;
  }

  public void startVoE() {
    check(!voeRunning, "VoE already started");
    voeRunning = true;
  }

  private void stopVoe() {
    check(voeRunning, "VoE not started");
    voeRunning = false;
  }

  public void setAudio(boolean audioEnabled) {
    this.audioEnabled = audioEnabled;
  }

  public boolean audioEnabled() { return audioEnabled; }

  public int audioCodecIndex() { return audioCodecIndex; }

  public void setAudioCodec(int codecNumber) {
    audioCodecIndex = codecNumber;
    CodecInst codec = voe.getCodec(codecNumber);
    check(voe.setSendCodec(audioChannel, codec) == 0, "Failed setSendCodec");
    codec.dispose();
  }

  public String[] audioCodecsAsString() {
    String[] retVal = new String[voe.numOfCodecs()];
    for (int i = 0; i < voe.numOfCodecs(); ++i) {
      CodecInst codec = voe.getCodec(i);
      retVal[i] = codec.toString();
      codec.dispose();
    }
    return retVal;
  }

  private CodecInst[] defaultAudioCodecs() {
    CodecInst[] retVal = new CodecInst[voe.numOfCodecs()];
     for (int i = 0; i < voe.numOfCodecs(); ++i) {
      retVal[i] = voe.getCodec(i);
    }
    return retVal;
  }
  public int getIsacIndex() {
    CodecInst[] codecs = defaultAudioCodecs();
    for (int i = 0; i < codecs.length; ++i) {
      if (codecs[i].name().contains("ISAC")) {
        return i;
      }
    }
    return 0;
  }

  public boolean agcEnabled() { return enableAgc; }

  public void setAgc(boolean enable) {
    enableAgc = enable;
    VoiceEngine.AgcConfig agc_config =
        new VoiceEngine.AgcConfig(3, 9, true);
    check(voe.setAgcConfig(agc_config) == 0, "VoE set AGC Config failed");
    check(voe.setAgcStatus(enableAgc, VoiceEngine.AgcModes.FIXED_DIGITAL) == 0,
        "VoE set AGC Status failed");
  }

  public boolean nsEnabled() { return enableNs; }

  public void setNs(boolean enable) {
    enableNs = enable;
    check(voe.setNsStatus(enableNs,
            VoiceEngine.NsModes.MODERATE_SUPPRESSION) == 0,
        "VoE set NS Status failed");
  }

  public boolean aecmEnabled() { return enableAecm; }

  public void setEc(boolean enable) {
    enableAecm = enable;
    check(voe.setEcStatus(enable, VoiceEngine.EcModes.AECM) == 0,
        "voe setEcStatus");
  }

  public boolean speakerEnabled() {
    return speakerEnabled;
  }

  public void setSpeaker(boolean enable) {
    speakerEnabled = enable;
    updateAudioOutput();
  }

  private void updateAudioOutput() {
    boolean useSpeaker = speakerEnabled;
    AudioManager audioManager =
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
    audioManager.setSpeakerphoneOn(useSpeaker);
  }
  public void startViE() {
    check(!vieRunning, "ViE already started");
    /*if (receiveVideo) {
      if (viewSelection ==
          context.getResources().getInteger(R.integer.openGl)) {
        svRemote = ViERenderer.CreateRenderer(context, true);
      } else if (viewSelection ==
          context.getResources().getInteger(R.integer.surfaceView)) {
        svRemote = ViERenderer.CreateRenderer(context, false);
      } else {
        externalCodec = new MediaCodecVideoDecoder(context);
        svRemote = externalCodec.getView();
      }
      if (externalCodec != null) {
        check(vie.registerExternalReceiveCodec(videoChannel,
                VCM_VP8_PAYLOAD_TYPE, externalCodec, true) == 0,
            "Failed to register external decoder");
      } else {
        check(vie.addRenderer(videoChannel, svRemote,
                0, 0, 0, 1, 1) == 0, "Failed AddRenderer");
        check(vie.startRender(videoChannel) == 0, "Failed StartRender");
      }
      check(vie.startReceive(videoChannel) == 0, "Failed StartReceive");
    }
    if (sendVideo) {
      startCamera();
      check(vie.startSend(videoChannel) == 0, "Failed StartSend");
    }*/
    vieRunning = true;
  }

  private void stopVie() {
    if (!vieRunning) {
      return;
    }
    
    stopCamera(); // Stop capturer after remote renderer.
    svRemote = null;
    vieRunning = false;
  }

  public void setReceiveVideo(boolean receiveVideo) {
    this.receiveVideo = receiveVideo;
  }

  public boolean receiveVideo() { return receiveVideo; }

  public void setSendVideo(boolean sendVideo) { this.sendVideo = sendVideo; }

  public boolean sendVideo() { return sendVideo; }

  public int videoCodecIndex() { return videoCodecIndex; }

  public void setVideoCodec(int codecNumber) {
    videoCodecIndex = codecNumber;
    updateVideoCodec();
  }

  public String[] videoCodecsAsString() {
    String[] retVal = new String[vie.numberOfCodecs()];
    //for (int i = 0; i < vie.numberOfCodecs(); ++i) {
      //VideoCodecInst codec = vie.getCodec(i);
      //retVal[i] = codec.toString();
      //codec.dispose();
    //}
    return retVal;
  }

  public int resolutionIndex() { return resolutionIndex; }

  public void setResolutionIndex(int resolution) {
    resolutionIndex = resolution;
    updateVideoCodec();
  }
  private void updateVideoCodec() {
    VideoCodecInst codec = getVideoCodec(videoCodecIndex, resolutionIndex);
    //check(vie.setSendCodec(videoChannel, codec) == 0, "Failed setReceiveCodec");
    codec.dispose();
  }  
  private VideoCodecInst getVideoCodec(int codecNumber, int resolution) {
    /*VideoCodecInst retVal = vie.getCodec(codecNumber);
    retVal.setStartBitRate(INIT_BITRATE_KBPS);
    retVal.setMaxBitRate(MAX_BITRATE_KBPS);
    retVal.setWidth(RESOLUTIONS[resolution][WIDTH_IDX]);
    retVal.setHeight(RESOLUTIONS[resolution][HEIGHT_IDX]);
    retVal.setMaxFrameRate(SEND_CODEC_FPS);*/
    return null;
  }

  public boolean hasMultipleCameras() {
    return Camera.getNumberOfCameras() > 1;
  }

  public boolean frontCameraIsSet() {
    return useFrontCamera;
  }

  // Set default camera to front if there is a front camera.
  private void setDefaultCamera() {
    useFrontCamera = hasFrontCamera();
  }

  public void toggleCamera() {
    if (vieRunning) {
      stopCamera();
    }
    useFrontCamera = !useFrontCamera;
    if (vieRunning) {
      startCamera();
    }
  }

  private void startCamera() {
    /*CameraDesc cameraInfo = vie.getCaptureDevice(getCameraId(getCameraIndex()));
    currentCameraHandle = vie.allocateCaptureDevice(cameraInfo);
    cameraInfo.dispose();
    check(vie.connectCaptureDevice(currentCameraHandle, videoChannel) == 0,
        "Failed to connect capture device");
    // Camera and preview surface.
    svLocal = new SurfaceView(context);
    VideoCaptureAndroid.setLocalPreview(svLocal.getHolder());
    check(vie.startCapture(currentCameraHandle) == 0, "Failed StartCapture");*/
    compensateRotation();
  }

  private void stopCamera() {
	  
  }

  private boolean hasFrontCamera() {
    return cameras[CameraInfo.CAMERA_FACING_FRONT] != null;
  }

  public SurfaceView getRemoteSurfaceView() {
    return svRemote;
  }

  public SurfaceView getLocalSurfaceView() {
    return svLocal;
  }

  public void setViewSelection(int viewSelection) {
    this.viewSelection = viewSelection;
  }

  public int viewSelection() { return viewSelection; }

  public boolean nackEnabled() { return enableNack; }

  public void setNack(boolean enable) {
    enableNack = enable;
    check(vie.setNackStatus(videoChannel, enableNack) == 0,
        "Failed setNackStatus");
  }

  // Collates current state into a multiline string.
  public String sendReceiveState() {
    /* if (vieRunning) {
      RtcpStatistics stats = vie.getReceivedRtcpStatistics(videoChannel);
      if (stats != null) {
        // Calculate % lost from fraction lost.
        // Definition of fraction lost can be found in RFC3550.
        packetLoss = (stats.fractionLost * 100) >> 8;
      }
    }
    String retVal =
        "fps in/out: " + inFps + "/" + outFps + "\n" +
        "kBps in/out: " + inKbps / 1024 + "/ " + outKbps / 1024 + "\n" +
        "resolution: " + inWidth + "x" + inHeight + "\n" +
        "loss: " + packetLoss + "%";*/
    return null;
  }

  // Callbacks from the VideoDecodeEncodeObserver interface.
  public void incomingRate(int videoChannel, int framerate, int bitrate) {
  }

  public void incomingCodecChanged(int videoChannel,
      VideoCodecInst videoCodec) {
    inWidth = videoCodec.width();
    inHeight = videoCodec.height();
    videoCodec.dispose();
  }

  public void outgoingRate(int videoChannel, int framerate, int bitrate) {
  }

  private int getCameraIndex() {
    return useFrontCamera ? CameraInfo.CAMERA_FACING_FRONT :
        CameraInfo.CAMERA_FACING_BACK;
  }

  private int getCameraId(int index) {
    for (int i = Camera.getNumberOfCameras() - 1; i >= 0; --i) {
      CameraInfo info = new CameraInfo();
      Camera.getCameraInfo(i, info);
      if (index == info.facing) {
        return i;
      }
    }
    throw new RuntimeException("Index does not match a camera");
  }

  private void compensateRotation() {
    if (svLocal == null) {
      // Not rendering (or sending).
      return;
    }
    if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
      return;
    }
    int cameraRotation = rotationFromRealWorldUp(
        cameras[getCameraIndex()], deviceOrientation);
    // Egress streams should have real world up as up.
    check(vie.setVideoRotations(currentCameraHandle, cameraRotation) == 0,
            "Failed setVideoRotations: camera " + currentCameraHandle +
            "rotation " + cameraRotation);
  }
  
  public VoiceEngine GetVoiceEngine()
  {
	  return this.voe;
  }
  
  public VideoEngine GetVideoEngine()
  {
	  return this.vie;
  }
}
