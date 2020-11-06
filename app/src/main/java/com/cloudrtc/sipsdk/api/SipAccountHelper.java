package com.cloudrtc.sipsdk.api;

import android.content.Context;

import com.cloudrtc.sipsdk.api.callback.SipCallStatusListener;
import com.cloudrtc.sipsdk.api.callback.SipLogListener;
import com.cloudrtc.sipsdk.api.entity.RegisterEntity;
import com.cloudrtc.sipsdk.api.manager.SipMediaManager;
import com.cloudrtc.sipsdk.api.manager.SipManager;
import com.cloudrtc.sipsdk.event.SipEvent;

import androidx.lifecycle.LifecycleOwner;

/**
 * 创建时间：2020/11/3
 * 创建人：singleCode
 * 功能描述：账户帮助类
 **/
public class SipAccountHelper {
    /**
     * sip库初始化
     * @param context
     * @param lifecycleOwner
     * @param transportType   传输方式 tcp 、udp
     * @param port  端口号
     * @param useIce  是否使用ice网络穿透
     * @param logLevel   日志级别
     * @param listener  日志监听回调
     */
    public static void init(Context context, LifecycleOwner lifecycleOwner, String transportType, int port, boolean useIce, int logLevel, SipLogListener listener){
        SipManager.getInstance().init(context,lifecycleOwner,transportType,port,useIce,logLevel,listener);
    }

    /**
     * 注册sip账号
     * @param context
     * @param onStart 第一次注册
     * @param sipHost sip服务地址
     * @param phoneNumber  sip账号
     * @param password  sip账号密码
     * @param restart  是否重新注册
     * @param delay   注册请求是否延迟发送
     */
    public static void register(Context context,boolean onStart,String sipHost,String phoneNumber,String password,boolean restart,boolean delay){
       if(onStart){
           SipMediaManager.getManager().getAudio(context).setLoudspeakerStatus(true);
       }
        RegisterEntity registerEntity = new RegisterEntity(sipHost,phoneNumber,password,restart);
        if(delay){
            SipManager.getInstance().postEventDelay(new SipEvent(SipEvent.REGISTER,registerEntity),6666);
        }else {
            SipManager.getInstance().postEvent(new SipEvent(SipEvent.REGISTER,registerEntity));
        }
    }

    /**
     * 注销账户
     */
    public static void unRegister(){
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.UNREGISTER));
    }

    /**
     * 设置sip通话监听回调
     * @param listener
     */
    public static void setListener(SipCallStatusListener listener){
        SipManager.getInstance().setListener(listener);
    }

    /**
     * 移除sip通话监听回调
     * @param listener
     */
    public static void removeListener(SipCallStatusListener listener){
        SipManager.getInstance().removeListener(listener);
    }

    /**
     * sip进程销毁时调用释放资源
     */
    public static void onDestroy(){
        SipManager.getInstance().onDestroy();
        SipMediaManager.getManager().closeRtcAudio();
    }

    public static boolean isRegister(){
       return SipCallRecordHelper.getHelper().isRegister();
    }


}
