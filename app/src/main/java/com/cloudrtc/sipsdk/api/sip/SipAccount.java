package com.cloudrtc.sipsdk.api.sip;

import android.text.TextUtils;
import android.util.Log;

import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.callback.SipAccountCallBack;
import com.cloudrtc.sipsdk.api.entity.SipCallInfoEntity;
import com.cloudrtc.sipsdk.api.util.SipCommandUtil;
import com.cloudrtc.sipsdk.api.util.SipUtil;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;

/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipAccount extends Account {
    private static final String TAG = "SipAccount";
    public Long id;
    private boolean isRegistered = false;
    private boolean initRegistered = false;
    private SipAccountCallBack accountCallBack;
    private AccountConfig accountConfig;
    private String sipServer;
    private String password;
    private String userId;
    private SipCallInfoEntity entity;
    private String transport;
    public SipAccount(AccountConfig config, String transport, String userId, String password, String sipServer, SipAccountCallBack callBack) {
        super();
        this.transport = transport;
        this.userId = userId;
        this.password = password;
        this.sipServer = sipServer;
        accountConfig = config;
        accountCallBack = callBack;
    }

    public String getTransport() {
        return transport;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getSipServer() {
        return sipServer;
    }

    public void unregister() {
        delete();
        accountCallBack = null;
        accountConfig = null;
    }
    private String getEquipmentId(String sdp){
        String equipmentId = SipUtil.getHeaderCommand(sdp, "X-xier-equipmentId:");
        if(TextUtils.isEmpty(equipmentId)){
            equipmentId = SipUtil.getHeaderCommand(sdp, "X-xier-from-deviceid:");
        }
        return equipmentId;
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        synchronized (this) {
            int callId = prm.getCallId();
            Log.e(TAG, "callId======" + callId);
            if (accountCallBack != null) {
                if ((accountCallBack.isCallActive())) {
                    String equipmentId = getEquipmentId(prm.getRdata().getWholeMsg());
                    Log.e(TAG, "isCallActive:" + SipUtil.parseMediaCommand(prm.getRdata().getWholeMsg()));
                    if (!TextUtils.isEmpty(equipmentId) && entity!=null && !TextUtils.equals(equipmentId, entity.getFromEquipmentId())) {
                        accountCallBack.onIncomingCall(this, callId, null);
                    }
                    return;
                }
                System.out.println("=====================onIncomingCall============:" + prm.getRdata().getWholeMsg());
                accountCallBack.onRemoteCommand(prm.getRdata().getWholeMsg());
                if (entity == null) {
                    String fromName = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier-from-name:");
                    String fromNumber = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier-from-number:");
                    String fromType = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier-from-type:");
                    String callType = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier:");
                    String equipmentId = getEquipmentId(prm.getRdata().getWholeMsg());
                    String suborgid = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier-from-suborgid:");
                    String orgId = SipUtil.getHeaderCommand(prm.getRdata().getWholeMsg(), "X-xier-orgId:");
                    Log.e(TAG, "fromName===" + fromName + "-----fromNumber==" + fromNumber + "---------------" + fromType + "-----" + callType+" equipmentId="+equipmentId);
                    boolean openVideo = true;
                    if (!TextUtils.isEmpty(callType)) {
                        if (SipCommandUtil.audioCall.equals(callType)) {
                            openVideo = false;
                        } else {
                            openVideo = true;
                        }
                    }
                    entity = new SipCallInfoEntity(sipServer, fromName, fromNumber, orgId, equipmentId, callType, fromType,openVideo ? 1 : 0, 0);
                }
                accountCallBack.onIncomingCall(this, callId,entity);
            }else {
                Log.d(TAG, "onIncomingCall: but accountCallBack is null");
            }
        }
    }

    public void resetCall(){
        entity = null;
    }

    public SipCallInfoEntity getCallInfoEntity() {
        return entity;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        initRegistered = true;
        final OnRegStateParam mPrm = prm;
        if (mPrm.getCode().swigValue() == 200) {
            this.isRegistered = true;
        } else {
            this.isRegistered = false;
        }
        if (accountCallBack != null)
            accountCallBack.onRegisterStatus(new RegisterStatus(mPrm.getCode().swigValue(), prm.getReason()));

    }

    public boolean isRegistered() {
        Log.e(TAG, "--------------initRegistered ="+initRegistered);
        if (initRegistered) {
            try {
                isRegistered = (this.getInfo().getRegStatus().swigValue() == 200);
                Log.e(TAG, "--------------isRegistered="+isRegistered);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return isRegistered;
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm) {
        final OnInstantMessageParam mPrm = prm;
        Log.e(TAG, "=======onInstantMessage=======:" + mPrm.getMsgBody());
        if (accountCallBack != null) {
            accountCallBack.onInstantMessage(prm);
        }
    }
}
