package com.cloudrtc;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cloudrtc.binder.CallRecordBean;
import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.binder.XierHeader;
import com.cloudrtc.sipsdk.api.util.MMKVUtil;
import com.cloudrtc.sipsdk.db.ISipCallEventProxy;

import java.util.List;


/**
 * 创建时间：2020/6/11
 * 创建人：singleCode
 * 功能描述：
 **/
public class AppSipHelper extends ISipCallback.Stub implements ISipCallEventProxy {
    private static String TAG = AppSipHelper.class.getSimpleName();
    private static AppSipHelper helper;
    private Context mContext;
    private SipAidlServer mBinderPool;
    private String sipHost,userId,sipPwd;
    private boolean isCall = false;
    private boolean needJumpCallRecord = false;
    private boolean tryRegister = false;
    private ISipCallback callback;
    private Class sipServer;
    private ISipCallEventProxy eventProxy;
    private AppSipHelper(){
        callback = this;
    }
    public static AppSipHelper getHelper(){
        if(helper == null){
            synchronized (AppSipHelper.class){
                if(helper == null){
                    helper = new AppSipHelper();
                }
            }
        }
        return helper;
    }
    public void bindSipService(Context context,String sipHost,String userId,String sipPwd){//2、binding
        this.mContext = context;
        this.sipHost = sipHost;
        this.userId = userId;
        this.sipPwd = sipPwd;
        Log.d(TAG, "bindSipService: "+sipHost+" "+userId+" "+sipPwd);
        if(mBinderPool != null){
            if(isCall()){//如果在通话中要等通话结束后再重新注册
                tryRegister = true;
            }else {
                tryRegisterAgain();
            }
        }else {
            connectBinderPoolService();
        }
    }
    public void unBindSipService(Context context){
        if(mBinderPoolConnection != null){
            try {
                if(mBinderPool != null) {
                    mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
                    mBinderPool.unRegister();
                    context.unbindService(mBinderPoolConnection);
                    mBinderPool = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void initSipService(Application application, Class sipServer, ISipCallEventProxy eventProxy){//1、初始化
        if(isMain(application)){
            MMKVUtil.init(application);
            this.sipServer = sipServer;
            this.eventProxy =eventProxy;
            Log.d(TAG, "initSipService: in main process");
            this.mContext = application.getApplicationContext();
            Intent intent = new Intent(mContext, sipServer);
            mContext.startService(intent);
        }else {
            Log.d(TAG, "initSipService: not main process");
        }
    }
    private boolean isMain(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationInfo().processName;
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
    private synchronized void connectBinderPoolService() {
        Intent service = new Intent(mContext, sipServer);
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {  //3连接成功

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            mBinderPool =  SipAidlServer.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
                mBinderPool.register(sipHost,userId,sipPwd,callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {  //4、监听连接状态，重连机制
        @Override
        public void binderDied() {
            Log.d(TAG,"binderDied");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };
    public void setNeedJumpCallRecord(boolean needJumpCallRecord) {
        this.needJumpCallRecord = needJumpCallRecord;
    }

    /**
     * 是否要跳到通话记录界面
     * @return
     */
    public boolean isNeedJumpCallRecord(){
        return needJumpCallRecord;
    }
    public void makeCall(String toNum, String toName, String fromName,String fromNum, boolean withVideo){//5、拨打电话
        if(mBinderPool == null){
            connectBinderPoolService();
        }else {
            try {
                SipCallRequest sipCallRequest = new SipCallRequest(fromName,fromNum,toName,toNum,withVideo);
                setCall(false);
                // TODO ========= 增加头部信息，用于存电话本和通话记录的直接拨打
                XierHeader header = new XierHeader();
                header.setHeader("X-xier-from-name",fromName);
                header.setHeader("X-xier-from-number",fromNum);
                header.setHeader("X-xier-from-type","phone");
                mBinderPool.makeCall(sipCallRequest,header);
            } catch (RemoteException e) {
                e.printStackTrace();
                connectBinderPoolService();
            }
        }
    }
    public boolean isCall() {
        return isCall;
    }
    private void tryRegisterAgain(){
        Log.d(TAG, "tryRegisterAgain: 1");
        if(mBinderPool != null){
            try {
                mBinderPool.register(sipHost,userId,sipPwd,this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        tryRegister = false;
    }
    private void setCall(boolean call) {
        isCall = call;
        if(!call && tryRegister){
            tryRegisterAgain();
        }
    }
    /**
     *
     * @param responseCode
     * @param actionName
     * @param response
     * @throws RemoteException
     */
    @Override
    public void onResult(int responseCode, String actionName, String response) throws RemoteException {
        Log.d(TAG, "onResult: responseCode="+responseCode+" actionName="+actionName+" response="+response);
        if(responseCode == 100){
            setCall(true);
        }else if(responseCode == 101){
            setCall(false);
        }
       onCallResult(responseCode,actionName,response);
    }

    @Override
    public void onOpenDoor(String orgId, String uniqNum, String name, String subOrgId) throws RemoteException {

    }

    @Override
    public void onCallRecord(CallRecordBean callRecord) throws RemoteException {
        Log.d(TAG, "onCallRecord: "+callRecord);
        onSaveCallRecord(callRecord);
    }

    @Override
    public void onSaveCallRecord(CallRecordBean callRecord) {
        if(eventProxy!=null){
            eventProxy.onSaveCallRecord(callRecord);
        }
    }

    @Override
    public void onCallResult(int responseCode, String actionName, String response) {
        if(eventProxy!=null){
            eventProxy.onCallResult(responseCode,actionName,response);
        }
    }
}
