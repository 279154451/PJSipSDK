package com.cloudrtc.sipsdk.api.manager;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.cloudrtc.binder.CallRecordBean;
import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.mediaengine.Engine;
import com.cloudrtc.mediaengine.VideoCaptureObserver;
import com.cloudrtc.mediaengine.VideoStreamObserver;
import com.cloudrtc.mediaengine.VoiceStreamObserver;
import com.cloudrtc.sipsdk.api.SipCallRecordHelper;
import com.cloudrtc.sipsdk.api.callback.SipLogListener;
import com.cloudrtc.sipsdk.api.entity.CallStatus;
import com.cloudrtc.sipsdk.api.entity.Direction;
import com.cloudrtc.sipsdk.api.entity.RegisterEntity;
import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.entity.SipCallRxStatus;
import com.cloudrtc.sipsdk.api.sip.SipAbstractManager;
import com.cloudrtc.sipsdk.api.sip.SipAccount;
import com.cloudrtc.sipsdk.api.callback.SipAccountCallBack;
import com.cloudrtc.sipsdk.api.sip.SipAccountConfig;
import com.cloudrtc.sipsdk.api.sip.SipCall;
import com.cloudrtc.sipsdk.api.callback.SipCallCallBack;
import com.cloudrtc.sipsdk.api.entity.SipCallInfoEntity;
import com.cloudrtc.sipsdk.api.callback.SipCallStatusListener;
import com.cloudrtc.sipsdk.api.sip.SipLogWriter;
import com.cloudrtc.sipsdk.api.sip.SipVideoConfig;
import com.cloudrtc.sipsdk.api.util.SipMessageUtil;
import com.cloudrtc.sipsdk.event.LiveDataBusHelper;
import com.cloudrtc.sipsdk.event.SipEvent;
import com.cloudrtc.sipsdk.api.util.SipCommandUtil;
import com.cloudrtc.sipsdk.api.util.SipUtil;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.SendInstantMessageParam;
import org.pjsip.pjsua2.SipHeader;
import org.pjsip.pjsua2.SipHeaderVector;
import org.pjsip.pjsua2.SipTxOption;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import androidx.lifecycle.LifecycleOwner;


/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipManager extends SipAbstractManager implements SipCallCallBack, VoiceStreamObserver, SipAccountCallBack, VideoStreamObserver, VideoCaptureObserver {
    private static String TAG = "SipManager";
    private static volatile SipManager sipManager;
    private SipCall sipCall;
    private SipAccount sipAccount;
    private Engine mEngine;
    private Endpoint ep = null;
    private String localCommand, remoteCommand;
    public static final int SIP_PORT = 6080;
    public static final int LOG_LEVEL = 4;
    private String transportType;
    private boolean useIce;
    private EpConfig epConfig;
    private TransportConfig sipTpConfig = null;
    private SipLogWriter logWriter;
    private Set<SipCallStatusListener> listenerSet = new CopyOnWriteArraySet<>();

    static {
        System.loadLibrary("pjsua2");
        System.loadLibrary("pjmediaengine");
    }

    private SipManager() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d(TAG, "finalize: ");
        listenerSet.clear();
        onDestroy();
    }

    public void setListener(SipCallStatusListener listener) {
        if (listener != null && !listenerSet.contains(listener)) {
            Log.d(TAG, "setListener: ");
            this.listenerSet.add(listener);
        }
    }

    public void removeListener(SipCallStatusListener listener) {
        if (listener != null && listenerSet.contains(listener)) {
            this.listenerSet.remove(listener);
        }
    }


    public static SipManager getInstance() {
        if (sipManager == null) {
            synchronized (SipManager.class) {
                if (sipManager == null) {
                    sipManager = new SipManager();
                }
            }
        }
        return sipManager;
    }

    public void init(Context context, LifecycleOwner lifecycleOwner, String transportType, int port, boolean useIce, int logLevel, SipLogListener listener) {
        Log.d(TAG, "init: transportType=" + transportType + " port=" + port + " useIce=" + useIce + " logLevel=" + logLevel);
        this.transportType = transportType;
        this.useIce = useIce;
        mEngine = new Engine();
        mEngine.register(context);
        ep = new Endpoint();
        epConfig = new EpConfig();
        sipTpConfig = new TransportConfig();
        sipTpConfig.setPort(port);
        epConfig.getLogConfig().setLevel(logLevel);
        epConfig.getLogConfig().setConsoleLevel(logLevel);

        LogConfig log_cfg = epConfig.getLogConfig();
        logWriter = new SipLogWriter(logLevel, listener);
        log_cfg.setWriter(logWriter);
        log_cfg.setDecor(log_cfg.getDecor() &
                ~(pj_log_decoration.PJ_LOG_HAS_CR.swigValue() |
                        pj_log_decoration.PJ_LOG_HAS_NEWLINE.swigValue()));

        try {
            ep.libCreate();
        } catch (Exception e) {
            //return;
        }

        UaConfig ua_cfg = epConfig.getUaConfig();
        ua_cfg.setUserAgent("cloudrtc");
        StringVector stun_servers = new StringVector();
        //stun_servers.add("120.25.211.29");
        ua_cfg.setStunServer(stun_servers);

        /* Init endpoint */
        try {
            ep.libInit(epConfig);
        } catch (Exception e) {
            return;
        }

        sipTpConfig.setPort(port);
        try {
            int status = ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
        } catch (Exception e) {

        }

        sipTpConfig.setPort(port);
        try {
            int status = ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
                    sipTpConfig);
        } catch (Exception e) {
        }

        sipTpConfig.setPort(port + 1);
        try {
            int status = ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS,
                    sipTpConfig);
        } catch (Exception e) {
        }

        try {
            ep.libStart();
        } catch (Exception e) {
            //return;
        }
        SipMediaManager.getManager().init(context);
        handlerEvent(lifecycleOwner);
    }

    public void postEvent(SipEvent event) {
        LiveDataBusHelper.post(SipEvent.class, event);
    }

    public void postEventDelay(SipEvent event, long delay) {
        LiveDataBusHelper.postDelay(SipEvent.class, event, delay);
    }

    private void handlerEvent(LifecycleOwner lifecycleOwner) {
        LiveDataBusHelper.register(lifecycleOwner, SipEvent.class, event -> {
            Log.d(TAG, "handlerEvent: " + event.toString());
            switch (event.getEventCode()) {
                case SipEvent.MAKE_CALL:
                    SipCallRequest request = (SipCallRequest) event.getEventData();
                    SipHeaderVector sipHeaderVector = request.getHeaderVector();
                    SipManager.getInstance().makeCall(request, sipHeaderVector);
                    break;
                case SipEvent.ANSWER_CALL:
                    SipManager.getInstance().answerCall(true);
                    break;
                case SipEvent.HANG_UP_CALL:
                    SipManager.getInstance().hangUp(true);
                    break;
                case SipEvent.SEND_IM:
                    String json = (String) event.getEventData();
                    Log.d(TAG, "SEND_IM: json=" + json);
                    if (!TextUtils.isEmpty(json)) {
                        SipManager.getInstance().sendSipMessage(json);
                    }
                    break;
                case SipEvent.SWITCH_TO_AUDIO:
                    SipManager.getInstance().swapAudioVideo(SipCommandUtil.audioCall);
                    SipManager.getInstance().senSwitchMessage(false);
                    break;
                case SipEvent.REGISTER:
                    RegisterEntity entity = (RegisterEntity) event.getEventData();
                    if (entity != null) {
                        String userId = entity.getUserId();
                        String sipHost = entity.getServer();
                        String sipPwd = entity.getSipPwd();
                        Log.d(TAG, "REGISTER: userId=" + userId + " sipHost=" + sipHost + " sipPwd=" + sipPwd);
                        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(sipHost) && !TextUtils.isEmpty(sipPwd)) {
                            SipManager.getInstance().register(entity.isRestart(), userId, sipPwd, sipHost);
                        }
                    }
                    break;
                case SipEvent.UNREGISTER:
                    SipManager.getInstance().unregister();
                    break;
            }
        });
    }

    @Override
    protected void register(boolean restart, String user_id, String password, String server) {
        Log.d(TAG, "-------------sip注册开始----------- ");
        Log.d(TAG, "restart=" + restart + "->" + user_id + ":" + password + ":" + server);
        if (restart) {
            if (sipAccount != null) {
                Log.d(TAG, "-----删除现有账户--->" + sipAccount.getUserId() + "-" + sipAccount.getPassword() + "-" + sipAccount.getSipServer());
                unregister();
            }
        }
        boolean isSameRegister = false;
        if (sipAccount != null) {
            if (!sipAccount.getUserId().equals(user_id) || !sipAccount.getPassword().equals(password) || !sipAccount.getSipServer().equals(server)) {
                Log.d(TAG, "----账户信息变更，重新注册----");
                unregister();
            }else {
                isSameRegister = true;
            }
        }
        SipAccountConfig accountConfig = new SipAccountConfig(transportType, useIce, user_id, server, server, password);
        if (!isRegistered()) {
            if (sipAccount == null) {
                AccountConfig config = accountConfig.getAccountConfig();
                sipAccount = new SipAccount(config, accountConfig.getTransportData(), user_id, password, server, this);
                try {
                    if (!sipAccount.libIsThreadRegistered()) {
                        Log.e(TAG, "注册线程");
                        this.sipAccount.libRegisterThread("cloudrtc");
                    }
                    sipAccount.create(config);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                if(isSameRegister){
                    return;
                }
                Log.e(TAG, "account.modify(accCfg);");
                try {
                    if (!sipAccount.libIsThreadRegistered()) {
                        this.sipAccount.libRegisterThread("cloudrtc");
                    }
                    sipAccount.modify(accountConfig.getAccountConfig());

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        Log.e(TAG, "---------------sip注册完毕--------------");
    }

    private boolean isRegistered() {
        return ((this.sipAccount != null) && (this.sipAccount.isRegistered()));
    }

    @Override
    protected void unregister() {
        Log.e(TAG, "unregister");
        if (isRegistered()) {
            sipAccount.unregister();
            sipAccount = null;
        } else if (sipAccount != null) {
            sipAccount.delete();
            sipAccount = null;
        }
    }


    @Override
    protected void makeCall(SipCallRequest request, SipHeaderVector headerVector) {
        Log.d(TAG, "makeCall: " + request.toString());
        SipCallRecordHelper.getHelper().newCall(1, sipAccount.getSipServer(), request.isOpenVideo());
        SipCallRecordHelper.getHelper().setCallInfo(request.getToName(), request.getToNum());
        SipCallRecordHelper.getHelper().setDeviceInfo(request.getFromNum(), "", "phone");
        SipCallRecordHelper.getHelper().setCallType(request.isOpenVideo() ? SipCommandUtil.fullCall : SipCommandUtil.audioCall);
        if (request.isOpenVideo()) {
            localCommand = SipCommandUtil.fullCall;
        } else {
            localCommand = SipCommandUtil.audioCall;
        }
        if (this.sipAccount == null) {
            Log.e(TAG, "current_account==============null");
            /* busy */
            return;
        }
        if (this.sipCall != null && sipCall.isCallActive()) {
            Log.e(TAG, "isCallActive");
            return;
        } else if (sipCall != null) {
            Log.d(TAG, "makeCall: to deleteCall");
            sipCall.delete();
            sipCall = null;
        }
        SipCall call = new SipCall(this.sipAccount, -1);
        CallOpParam prm = new CallOpParam(true);
        SipTxOption mStxOption = prm.getTxOption();
        if (headerVector != null) {
            mStxOption.setHeaders(headerVector);
        }
        try {
            /**
             * 语音视频拨打
             */
            if (!request.isOpenVideo()) {
                CallSetting csetting = prm.getOpt();
                csetting.setVideoCount(0);
                csetting.setFlag(2);//flag=2表示不带没有的sdp信息，默认值0
                prm.setOpt(csetting);
            }
            call.makeCall("sip:" + request.getToNum() + "@" + sipAccount.getSipServer() + this.sipAccount.getTransport(), prm);
        } catch (Exception e) {
            Log.d(TAG, "MakeCall: " + e.toString());
            call.delete();
            return;
        }
        sipCall = call;
        sipCall.setDirection(Direction.Outgoing);
        sipCall.setSipCallCallBack(this);
        notifyNewCall(false);
    }

    private void notifyNewCall(boolean incomingCall) {
        Log.d(TAG, "notifyNewCall: incomingCall =" + incomingCall + " localCommand=" + localCommand);
        //TODO 通知上层新会话建立
        if (listenerSet != null) {
            if (incomingCall) {
                Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
                while (iterator.hasNext()) {
                    SipCallStatusListener listener = iterator.next();
                    Log.d(TAG, "notifyNewCall: CALL_INCOMING");
                    listener.onCallStatus(sipAccount, sipCall, CallStatus.CALL_INCOMING);
                }
            } else {
                if (TextUtils.isEmpty(localCommand)) {
                    Log.d(TAG, "notifyNewCall: localCommand is empty");
                    return;
                } else {
                    Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
                    while (iterator.hasNext()) {
                        SipCallStatusListener listener = iterator.next();
                        Log.d(TAG, "notifyNewCall: CALL_OUTGOING");
                        listener.onCallStatus(sipAccount, sipCall, CallStatus.CALL_OUTGOING);
                    }
                }
            }
        }
    }

    @Override
    protected void answerCall(boolean videoCall) {
        Log.d(TAG, "answerCall: " + videoCall);
        synchronized (this) {
            if (sipCall != null && sipAccount != null) {
                CallOpParam prm = new CallOpParam();
                SipTxOption mStxOption = prm.getTxOption();
                SipHeader mSipHeader = new SipHeader();
                mSipHeader.setHName("X-xier");
                CallRecordBean recordBean = SipCallRecordHelper.getHelper().getRecordBean(false);
                if (recordBean == null || TextUtils.isEmpty(recordBean.getCallType())) {
                    if (videoCall) {
                        localCommand = SipCommandUtil.fullCall;
                    } else {
                        localCommand = SipCommandUtil.audioCall;
                    }
                } else {
                    localCommand = recordBean.getCallType();
                }
                mSipHeader.setHValue(localCommand);
                SipHeaderVector mSipHeaderV = new SipHeaderVector();
                mSipHeaderV.add(mSipHeader);
                mStxOption.setHeaders(mSipHeaderV);

                prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
                try {
                    if (!sipCall.libIsThreadRegistered()) {
                        sipCall.libRegisterThread("call");
                    }
                    sipCall.answer(prm);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void hangUp(boolean callConnect) {
        Log.d(TAG, "------hangUp");
        declineCall();
    }

    @Override
    protected void declineCall() {
        if (sipCall != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                if (!sipCall.libIsThreadRegistered()) {
                    sipCall.libRegisterThread("call");
                }
                sipCall.hangup(prm);
                sipCall = null;
            } catch (Exception e) {
                deleteCall(true);
                System.out.println(e);
            }
        }

    }


    @Override
    protected void deleteCall(boolean resetInfo) {
        Log.d(TAG, "deleteCall: ");
        if (sipCall != null) {
            sipCall.delete();
            sipCall = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        unregister();
        deleteCall(true);
        if (ep != null) {
            try {
                ep.libDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ep.delete();
            ep = null;
        }

        if (mEngine != null) {
            mEngine.unRegister();
            mEngine = null;
        }
        sipManager = null;
    }

    @Override
    public void onRegisterStatus(RegisterStatus status) {
        Log.d(TAG, "onRegisterStatus: " + status.getCode());
        if (status.getCode() == 200) {
            SipCallRecordHelper.getHelper().setRegister(true);
        } else {
            SipCallRecordHelper.getHelper().setRegister(false);
        }
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.onRegisterStatus(sipAccount, status);
            }
        }
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm) {
        Log.d(TAG, "onInstantMessage: " + prm.getMsgBody());
        swapAudioVideo(prm.getMsgBody());
        if (prm.getMsgBody().equals("refusing call")) {
            notifyRxCallStatus(SipCallRxStatus.refuse_call);
        } else if (prm.getMsgBody().equals("no people")) {
            notifyRxCallStatus(SipCallRxStatus.no_people);
        } else if (prm.getMsgBody().equals("waiting call")) {
            notifyRxCallStatus(SipCallRxStatus.wait_call);
        } else if (prm.getMsgBody().equals("change voice")) {
            swapAudioVideo(SipCommandUtil.audioCall);
        } else if (prm.getMsgBody().equals("number is empty")) {
            notifyRxCallStatus(SipCallRxStatus.empty_number);
        } else if (prm.getMsgBody().equals("answered")) {
            if (sipCall != null && !sipCall.isConnected()) {
                hangUp(false);
            }
        } else if (prm.getMsgBody().equals("user busy")) {
            //TODO 用户忙,语音提示
            notifyRxCallStatus(SipCallRxStatus.user_busy);
        }else if(SipMessageUtil.parseOpenDoorJson(prm.getMsgBody())){
            notifyRxCallStatus(SipCallRxStatus.open_door);
        }
    }

    private void notifyRxCallStatus(SipCallRxStatus status) {
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.onCallRxStatus(sipAccount, sipCall, status);
            }
        }
    }

    protected void swapAudioVideo(String command) {
        if (!command.equalsIgnoreCase(remoteCommand)) {
            if (command.equalsIgnoreCase(SipCommandUtil.audioCall)) {
                stopVideoReceiving();
                //TODO 通知界面关闭视频
                if (listenerSet != null) {
                    Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
                    while (iterator.hasNext()) {
                        SipCallStatusListener listener = iterator.next();
                        listener.onVideoStatusChange(sipAccount, sipCall, false);
                    }
                }
            }

            if (command.equalsIgnoreCase(SipCommandUtil.fullCall)) {
                restartVideoReceiving();
                if (listenerSet != null) {
                    Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
                    while (iterator.hasNext()) {
                        SipCallStatusListener listener = iterator.next();
                        listener.onVideoStatusChange(sipAccount, sipCall, true);
                    }
                }
            }
            remoteCommand = command;
        }
        Log.d(TAG, "swapAudioVideo: command=" + command + " remoteCommand=" + remoteCommand);
    }

    protected boolean senSwitchMessage(boolean video_call) {
        SendInstantMessageParam msg = new SendInstantMessageParam();
        try {
            msg.setContent("change voice");
            sipCall.sendInstantMessage(msg);
        } catch (Exception e) {

        }
        String swapCommand = null;

        if (this.sipCall != null) {
            try {
                SendInstantMessageParam prm = new SendInstantMessageParam();
                if (video_call) swapCommand = SipCommandUtil.fullCall;
                else swapCommand = SipCommandUtil.audioCall;
                prm.setContent(swapCommand);
                this.sipCall.sendInstantMessage(prm);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (!swapCommand.equalsIgnoreCase(this.localCommand)) {
                if (swapCommand.equalsIgnoreCase(SipCommandUtil.fullCall)) {
                    restartVideoSending();
                } else if (swapCommand.equalsIgnoreCase(SipCommandUtil.audioCall)) {
                    stopVideoSending();
                }

                this.localCommand = swapCommand;
            }
        }
        return true;
    }

    //发送sip指令
    protected void sendSipMessage(String openDoorMsg) {
        if (this.sipCall != null) {
            try {
                SendInstantMessageParam prm = new SendInstantMessageParam();
                prm.setContent(openDoorMsg);
                this.sipCall.sendInstantMessage(prm);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onIncomingCall(SipAccount account, int callId, SipCallInfoEntity entity) {
        if (sipCall != null && sipCall.isCallActive()) {
            Log.e(TAG, "======================iscalling");
            SendInstantMessageParam msg = new SendInstantMessageParam();
            SipCall pjSipCall = new SipCall(account, callId);
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                if (!pjSipCall.libIsThreadRegistered()) {
                    pjSipCall.libRegisterThread("call");
                }
                msg.setContent("waiting call");
                pjSipCall.hangup(prm);
                pjSipCall.delete();
            } catch (Exception e) {
                System.out.println(e);
            }
            return;
        }

        // TODO Auto-generated method stub
        if (sipAccount != null) {
            if (sipCall != null) {
                deleteCall(false);
            }
            sipCall = new SipCall(account, callId);
            sipCall.setDirection(Direction.Incoming);
            sipCall.setSipCallCallBack(this);
            if (sipCall != null) {
                try {
                    if (!sipCall.libIsThreadRegistered()) {
                        sipCall.libRegisterThread("call");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                CallOpParam prm = new CallOpParam();
                prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                try {
                    sipCall.answer(prm);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (entity != null) {
                Log.d(TAG, "onIncomingCall: " + entity);
                SipCallRecordHelper.getHelper().newCall(0, entity.getSipServer(), entity.getOpenVideo() > 0 ? true : false);
                SipCallRecordHelper.getHelper().setCallInfo(entity.getFromName(), entity.getFromNumber());
                SipCallRecordHelper.getHelper().setDeviceInfo(entity.getFromOrgId(), entity.getFromEquipmentId(), entity.getDevType());
                SipCallRecordHelper.getHelper().setCallType(entity.getCallType());
            }
            notifyNewCall(true);
        }
    }

    @Override
    public void onRemoteCommand(String sdpDescription) {
        this.remoteCommand = SipUtil.parseMediaCommand(sdpDescription);
        Log.d(TAG, "onRemoteCommand: "+remoteCommand);
    }

    @Override
    public boolean isCallActive() {
        if (sipCall != null) {
//            return sipCall.isCallActive();
            return sipCall.isActive();
        }
        return false;
    }

    @Override
    public void onCallConnected(String uri) {
        Log.d(TAG, "onCallConnected: " + uri);
        SipCallRecordHelper.getHelper().setAnswerTime(System.currentTimeMillis());
        SipMediaManager.getManager().registerStreamObserver(this, this, this);
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.onCallStatus(sipAccount, sipCall, CallStatus.CALL_CONNECTED);//会话建立成功
            }
        }
    }

    @Override
    public void onCallEnd() {
        localCommand = null;
        remoteCommand = null;
        Log.d(TAG, "onCallEnd: listenerSet " + listenerSet.size());
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.onCallStatus(sipAccount, sipCall, CallStatus.CALL_END);
            }
        }
        deleteCall(true);
    }

    @Override
    public void onCallFailed(int code) {
        Log.d(TAG, "onCallFailed: ");
        localCommand = null;
        remoteCommand = null;
        Log.d(TAG, "onCallFailed: listenerSet " + listenerSet.size());
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.onCallStatus(sipAccount, sipCall, CallStatus.CALL_FAILED);
            }
        }
        deleteCall(true);
    }

    @Override
    public void onCallDtmf() {

    }

    /**
     * 配置视频宽高大小、帧率、比特率
     * @param defVideoSize
     */
    @Override
    public void configVideoChannel(SipVideoConfig defVideoSize) {
        SipMediaManager.getManager().initVideoFormat(sipCall, defVideoSize.getWidth(), defVideoSize.getHeight(), defVideoSize.getFps(), defVideoSize.getBitrate());
    }


    /**
     * 开启视频流、音频流接收传输
     * @param camera_index 摄像头ID
     * @param rotation 摄像头旋转角度
     * @param local_renderer
     * @param remote_renderer
     */
    @Override
    public void startVideoChannel(int camera_index, int rotation, SurfaceView local_renderer, SurfaceView remote_renderer) {
        if (SipCommandUtil.fullCall.equalsIgnoreCase(this.remoteCommand)) {
            SipMediaManager.getManager().startVideoChannel(sipCall, true, camera_index, local_renderer, remote_renderer);
        } else {
            SipMediaManager.getManager().startVideoChannel(sipCall, false, camera_index, local_renderer, remote_renderer);
        }
        SipManager.getInstance().setCameraOutputRotation(rotation);
    }

    /**
     * 关闭视频流、音频流传输
     */
    @Override
    public void stopVideoChannel() {
        SipMediaManager.getManager().stopVideoChannel();
    }

    /**
     * 开启音频流传输
     */
    @Override
    public void startVoiceChannel() {
        SipMediaManager.getManager().stopReceiveVoice();
        SipMediaManager.getManager().stopSendVoice();
    }

    /**
     * 音频转视频时重新开启视频流传输
     */
    @Override
    public void restartVideoSending() {
        SipMediaManager.getManager().restartSendVideoWithCall(sipCall);
    }
    /**
     * 音频转视频时重新开启视频流接收
     */
    @Override
    public void restartVideoReceiving() {
        SipMediaManager.getManager().restartReceiveVideoWithCall(sipCall);
    }

    /**
     * 视频转音频时关闭视频流接收
     */
    @Override
    public void stopVideoReceiving() {
        SipMediaManager.getManager().stopReceiveVideo();
    }

    /**
     * 视频转音频时关闭视频流发送
     */
    @Override
    public void stopVideoSending() {
        SipMediaManager.getManager().stopSendVideo();
    }

    @Override
    public void setCameraOutputRotation(int rotation) {
        SipMediaManager.getManager().changeCaptureRotation(sipCall, rotation);
    }

    @Override
    public int getCameraOrientation(int cam_index) {
        return SipMediaManager.getManager().getCameraOrientation(cam_index);
    }

    @Override
    public void swapCamera(int camera_index, int rotation_new,SurfaceView local_renderer) {
        SipMediaManager.getManager().swapCameraWithCall(sipCall, camera_index, rotation_new, local_renderer);
    }


    @Override
    public void OnIncomingRate(int video_channel, int framerate, int bitrate) {

    }

    @Override
    public void OnOutgoingRate(int video_channel, int framerate, int bitrate) {

    }

    @Override
    public void OnVideoFrameSizeChanged(int video_channel, int width, int height) {
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.OnVideoFrameSizeChanged(video_channel, width, height);
            }
        }
    }

    @Override
    public void OnCaptureFrame(byte[] data, int rotation, long captureMs) {
        //TODO 摄像头数据
    }

    @Override
    public void OnAudioIncomingSize(int size) {
        if (listenerSet != null) {
            Iterator<SipCallStatusListener> iterator = listenerSet.iterator();
            while (iterator.hasNext()) {
                SipCallStatusListener listener = iterator.next();
                listener.OnAudioIncomingSize(size);
            }
        }
    }
}
