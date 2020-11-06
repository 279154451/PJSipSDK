package com.cloudrtc.sipsdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.cloudrtc.ISipCallback;
import com.cloudrtc.sipsdk.api.SipAccountHelper;
import com.cloudrtc.sipsdk.api.callback.SipLogListener;
import com.cloudrtc.sipsdk.api.entity.CallStatus;
import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.sip.SipAccount;
import com.cloudrtc.sipsdk.api.sip.SipCall;
import com.cloudrtc.sipsdk.api.callback.SipCallStatusListener;
import com.cloudrtc.sipsdk.api.manager.SipManager;
import com.cloudrtc.sipsdk.api.util.MMKVUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public abstract class SipService extends Service implements SipCallStatusListener , SipLogListener, LifecycleOwner {
    private static String TAG = SipService.class.getSimpleName();
    private SipBinder mBinder;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        //1、初始化SIP
        Log.d(TAG, "onCreate");
        SipAccountHelper.init(this,this,"tcp",SipManager.SIP_PORT,true,SipManager.LOG_LEVEL,this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
        Log.d(TAG, "onStart");
        //2、注册SIp
        registerSip(true,false);
    }
    private void registerSip(boolean onStart,boolean delay) {
        SipAccountHelper.setListener(this);
        String userId = (String) MMKVUtil.getUtil().get( "UserId", "");
        String sipHost = (String) MMKVUtil.getUtil().get( "SipHost", "");
        String sipPwd = (String) MMKVUtil.getUtil().get( "SipPwd", "");
        Log.d(TAG, "onStart: userId=" + userId + " sipHost=" + sipHost + " sipPwd=" + sipPwd);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(sipHost) && !TextUtils.isEmpty(sipPwd)) {
            SipAccountHelper.register(this,onStart,sipHost,userId,sipPwd,true,delay);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
        SipAccountHelper.removeListener(this);
        SipAccountHelper.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        //3、绑定SIP服务
        mBinder = getBinder();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRegisterStatus(SipAccount account, RegisterStatus status) {
        Log.d(TAG, "onRegisterStatus: " + status);
        if(status.getCode() != 200){
            registerSip(false,true);
        }
    }

    @Override
    public void onCallStatus(SipAccount account, SipCall call, CallStatus callStatus) {
        Log.d(TAG, "onCallStatus: " + callStatus.name());
        //TODO 会话状态变化：来电、去电、会话建立、会话失败、会话结束
        switch (callStatus) {
            case CALL_INCOMING:
                responseResult(100,"CALL_INCOMING");
                onStartCallActivity();
                break;
            case CALL_OUTGOING:
                responseResult(100,"CALL_OUTGOING");
                onStartCallActivity();
                break;
            case CALL_CONNECTED:
                responseResult(100,"CALL_CONNECTED");
                break;
            case CALL_FAILED:
                responseResult(101,"CALL_FAILED");
                break;
            case CALL_END:
                responseResult(101,"CALL_END");
                break;
        }
    }

    private void responseResult(int code,String message){
        Log.d(TAG, "responseResult: "+message);
       if( mBinder != null && mBinder.getCallback() != null){
           ISipCallback callback = mBinder.getCallback();
           try {
               callback.onResult(code, message, "");
           } catch (RemoteException e) {
               e.printStackTrace();
           }
       }else {
           Log.d(TAG, "responseResult: mBinder is null or CallBack is null");
       }
    }

//    @Override
//    public void onCallRxStatus(SipAccount account, SipCall call, SipCallRxStatus status) {
//
//    }
//
//    @Override
//    public void onVideoStatusChange(SipAccount account, SipCall call, boolean open) {
//        //TODO 通话视频流状态变化
//    }
//
//    @Override
//    public void OnVideoFrameSizeChanged(int video_channel, int width, int height) {
//        //TODO 通话视频流帧变化
//    }
//
//    @Override
//    public void OnAudioIncomingSize(int size) {
//        //TODO 通话音频大小，以及dtmf
//    }
//    @Override
//    public void onSipLog(String message) {
//
//    }
    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /**
     * 启动通话界面
     */
    public  abstract void onStartCallActivity();

    /**
     * 获取binder对象
     * @return
     */
    public abstract SipBinder getBinder();
}
