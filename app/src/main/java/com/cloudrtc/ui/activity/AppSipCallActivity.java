package com.cloudrtc.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cloudrtc.sipsdk.api.SipCallHelper;
import com.cloudrtc.sipsdk.api.SipCallRecordHelper;
import com.cloudrtc.sipsdk.api.entity.CallStatus;
import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.entity.SipCallRxStatus;
import com.cloudrtc.sipsdk.api.sip.SipAccount;
import com.cloudrtc.sipsdk.api.sip.SipCall;
import com.cloudrtc.sipsdk.api.callback.SipCallStatusListener;
import com.cloudrtc.sipsdk.event.LiveDataBusHelper;
import com.cloudrtc.ui.base.BaseCompatActivity;
import com.cloudrtc.ui.event.SipRegisterStatusEvent;
import com.cloudrtc.ui.media.MediaPlayerHelper;
import com.coder.sipsdk.R;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * 创建时间：2020/6/15
 * 创建人：singleCode
 * 功能描述：
 **/
public class AppSipCallActivity extends BaseCompatActivity implements SipCallStatusListener {
    private PowerManager.WakeLock wakeLock;
    private AppSipCallWaitFragment callWaitFragment;
    private AppSipCallConnectFragment callConnectFragment;
    private FrameLayout m_fragment;
    public static void startActivity(Context context){
        Intent intent = new Intent(context,AppSipCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sip_call;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        m_fragment = findViewById(R.id.m_fragment);
        initConfig();
        startWaitFragment();
        SipCallHelper.setListener(this);
    }
    @SuppressLint("InvalidWakeLockTag")
    private void initConfig(){
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, "WakeLockActivity");
    }

    /**
     * 会话等待界面
     */
    public void startWaitFragment(){
        if(callWaitFragment == null){
            callWaitFragment = AppSipCallWaitFragment.getInstance();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.m_fragment, callWaitFragment, "VIDEOWAITE");
        ft.commit();
    }

    /**
     * 会话接通界面
     * @param videoCall
     */
    public void startConnectFragment(boolean videoCall){
        if(callConnectFragment == null){
            callConnectFragment = AppSipCallConnectFragment.getInstance(videoCall);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.m_fragment, callConnectFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SipCallHelper.removeListener(this);
        SipCallRecordHelper.getHelper().reportCallRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wakeLock != null){
            wakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(wakeLock != null){
            wakeLock.release();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == event.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRegisterStatus(SipAccount account, RegisterStatus status) {
        boolean register;
        if(status.getCode() == 200){
            register = true;
        }else {
            register = false;
        }
        SipRegisterStatusEvent event = new SipRegisterStatusEvent(register);
        LiveDataBusHelper.post(SipRegisterStatusEvent.class,event);
    }

    @Override
    public void onCallStatus(SipAccount account, SipCall call, CallStatus status) {
        Log.d(TAG, "onCallStatus: "+status.name());
        switch (status){
            case CALL_INCOMING:
            case CALL_OUTGOING:
                break;
            case CALL_CONNECTED:
                releaseMedia();
                m_fragment.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startConnectFragment(true);
                    }
                },500);
                break;
            case CALL_FAILED:
            case CALL_END:
                finishActivity();
                break;
        }
    }

    public void finishActivity(){
        releaseMedia();
        if(rawId!=0){
            MediaPlayerHelper.getInstance().startMediaPlayer(this,"callTips",rawId,false,null);
        }
        if(!isFinishing()){
            finish();
        }
        rawId = 0;
    }
    private void releaseMedia(){
        Log.d(TAG, "releaseMedia");
        MediaPlayerHelper.getInstance().releaseMediaPlayer("callRing");
    }
    private int rawId;
    @Override
    public void onCallRxStatus(SipAccount account, SipCall call, SipCallRxStatus status) {
        Log.d(TAG, "onCallRxStatus: "+status.name());
        switch (status){
            case no_people:
                rawId= R.raw.no_people;
                break;
            case user_busy:
                rawId= R.raw.user_busy;
                break;
            case wait_call:
                rawId= R.raw.wait_call;
                break;
            case refuse_call:
                rawId= R.raw.refuse_call;
                break;
            case empty_number:
                rawId= R.raw.empty_number;
                break;
        }
    }

    @Override
    public void onVideoStatusChange(SipAccount account, SipCall call, boolean open) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(callConnectFragment != null){
                    if(!open){
                        SipCallHelper.switchToAudio();
                        callConnectFragment.closeVideo(true);
                    }else {
                        callConnectFragment.closeVideo(false);
                    }
                }
            }
        });
    }

    @Override
    public void OnVideoFrameSizeChanged(int video_channel, int width, int height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(callConnectFragment != null){
                    callConnectFragment.onVideoFrameSizeChanged(video_channel,width,height);
                }
            }
        });
    }

    @Override
    public void OnAudioIncomingSize(int size) {

    }

}
