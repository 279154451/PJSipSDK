package com.cloudrtc.sipsdk.api.manager.media;

import android.content.Context;
import android.util.Log;

import com.cloudrtc.mediaengine.CloudRtcAudioManager;

/**
 * 创建时间：2020/5/8
 * 创建人：singleCode
 * 功能描述：audio 管理类
 **/
public class AudioMediaManager {
    private CloudRtcAudioManager rtcAudioManager;

    private static AudioMediaManager manager;
    public AudioMediaManager(){

    }
//    public static AudioMediaManager getManager(){
//        if(manager == null){
//            synchronized (AudioMediaManager.class){
//                if(manager == null){
//                    manager = new AudioMediaManager();
//                }
//            }
//        }
//        return manager;
//    }

    public CloudRtcAudioManager getRtcAudio(Context context){
        if(rtcAudioManager == null){
            rtcAudioManager =  CloudRtcAudioManager.create(context, new Runnable() {
                @Override
                public void run() {

                }
            });
            Log.d("AudioMediaManager", "Initializing the audio manager...");
            rtcAudioManager.init();
        }
        return rtcAudioManager;
    }


    public boolean setLoudspeakerStatus(boolean yesno) {
        boolean speaker_on = yesno;
        if(rtcAudioManager != null)
            rtcAudioManager.SetLoudspeakerStatus(speaker_on);
        return true;
    }
    public void muteMic(boolean yesno) {
        // TODO Auto-generated method stub
        boolean mute_on = yesno;
        if(rtcAudioManager != null)
            rtcAudioManager.setMicrophoneMute(mute_on);
    }
    public void closeRtcAudio() {
        if (rtcAudioManager != null) {
            rtcAudioManager.close();
            rtcAudioManager = null;
        }
    }

}
