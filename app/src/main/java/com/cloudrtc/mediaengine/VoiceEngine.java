package com.cloudrtc.mediaengine;

public class VoiceEngine {
  //private final long nativeVoiceEngine;

  // Keep in sync (including this comment) with
  // webrtc/common_types.h:NsModes
  public enum NsModes {
    UNCHANGED, DEFAULT, CONFERENCE, LOW_SUPPRESSION,
    MODERATE_SUPPRESSION, HIGH_SUPPRESSION, VERY_HIGH_SUPPRESSION
  }

  // Keep in sync (including this comment) with
  // webrtc/common_types.h:AgcModes
  public enum AgcModes {
    UNCHANGED, DEFAULT, ADAPTIVE_ANALOG, ADAPTIVE_DIGITAL,
    FIXED_DIGITAL
  }

  // Keep in sync (including this comment) with
  // webrtc/common_types.h:AecmModes
  public enum AecmModes {
    QUIET_EARPIECE_OR_HEADSET, EARPIECE, LOUD_EARPIECE,
    SPEAKERPHONE, LOUD_SPEAKERPHONE
  }

  // Keep in sync (including this comment) with
  // webrtc/common_types.h:EcModes
  public enum EcModes { UNCHANGED, DEFAULT, CONFERENCE, AEC, AECM }

  public static class AgcConfig {
    AgcConfig(int targetLevelDbOv, int digitalCompressionGaindB,
        boolean limiterEnable) {
      this.targetLevelDbOv = targetLevelDbOv;
      this.digitalCompressionGaindB = digitalCompressionGaindB;
      this.limiterEnable = limiterEnable;
    }
    private final int targetLevelDbOv;
    private final int digitalCompressionGaindB;
    private final boolean limiterEnable;
  }

  public VoiceEngine() {
  }
  
  public native int getAudioChannel();
  public native int setLoudspeakerStatus(boolean enable);
  public native int numOfCodecs();
  
  public native int StartSending();
  public native int StopSending();
  public native int StartReceiving();
  public native int StopReceiving();
  
  public native CodecInst getCodec(int index);
  public native int setSendCodec(int channel, CodecInst codec);
  public int setEcStatus(boolean enable, EcModes mode) {
    return setEcStatus(enable, mode.ordinal());
  }
  private native int setEcStatus(boolean enable, int ec_mode);
  public int setAecmMode(AecmModes aecm_mode, boolean cng) {
    return setAecmMode(aecm_mode.ordinal(), cng);
  }
  private native int setAecmMode(int aecm_mode, boolean cng);
  public int setAgcStatus(boolean enable, AgcModes agc_mode) {
    return setAgcStatus(enable, agc_mode.ordinal());
  }
  private native int setAgcStatus(boolean enable, int agc_mode);
  public native int setAgcConfig(AgcConfig agc_config);
  public int setNsStatus(boolean enable, NsModes ns_mode) {
    return setNsStatus(enable, ns_mode.ordinal());
  }
  private native int setNsStatus(boolean enable, int ns_mode);
  //public native void SetAudioCodec(String audio_codecs);
  public  native int RegisterVoiceStreamObserver(VoiceStreamObserver cb);

}