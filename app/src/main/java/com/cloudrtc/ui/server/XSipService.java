package com.cloudrtc.ui.server;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.cloudrtc.sipsdk.api.entity.SipCallRxStatus;
import com.cloudrtc.sipsdk.api.sip.SipAccount;
import com.cloudrtc.sipsdk.api.sip.SipCall;
import com.cloudrtc.sipsdk.service.SipBinder;
import com.cloudrtc.sipsdk.service.SipService;
import com.cloudrtc.ui.activity.AppSipCallActivity;

/**
 * 创建时间：2020/11/5
 * 创建人：singleCode
 * 功能描述：
 **/
public class XSipService extends SipService {
    private String TAG = XSipService.class.getSimpleName();
        @Override
    public void onCallRxStatus(SipAccount account, SipCall call, SipCallRxStatus status) {

    }

    @Override
    public void onVideoStatusChange(SipAccount account, SipCall call, boolean open) {
        //TODO 通话视频流状态变化
        Log.d(TAG, "onVideoStatusChange: "+open);
    }

    @Override
    public void OnVideoFrameSizeChanged(int video_channel, int width, int height) {
        //TODO 通话视频流帧变化
        Log.d(TAG, "OnVideoFrameSizeChanged: "+width+" "+height);
    }

    @Override
    public void OnAudioIncomingSize(int size) {
        //TODO 通话音频大小，以及dtmf
        Log.d(TAG, "OnAudioIncomingSize: "+size);
    }
    @Override
    public void onSipLog(String message) {

    }

    @Override
    public void onStartCallActivity() {
        KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁软盘
        kl.disableKeyguard();
        //获取电源管理器对象,解决灭屏下呼叫界面起不来问题
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        AppSipCallActivity.startActivity(getApplicationContext());
        //释放
        wl.release();
        kl.reenableKeyguard();
    }

    @Override
    public SipBinder getBinder() {
        return new SipBinder(this,this);
    }

}
