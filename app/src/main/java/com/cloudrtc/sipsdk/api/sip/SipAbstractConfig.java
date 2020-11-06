package com.cloudrtc.sipsdk.api.sip;

import android.content.Context;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.pj_turn_tp_type;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：
 **/
public abstract class SipAbstractConfig {

    public abstract String getTransportType();

    public abstract String getTransportData();

    public abstract String getIdUri();

    public abstract String getRegisterUri();

    public abstract long getTimeoutSec();

    public abstract long getDelayBeforeRefreshSec();

    public abstract AuthCredInfo getAuthCredInfo();

    public abstract String getTurnUserName();

    public abstract String getTurnPassword();

    public abstract  String getTurnServer();

    public abstract pj_turn_tp_type getTurnConnectType();

    public abstract int getTurnPasswordType();

    public abstract boolean IceEnable();

    public abstract boolean TurnEnable();

    public abstract AccountConfig getAccountConfig();

    public abstract String getAudio_codecs(Context context);

    public abstract String getVideo_codecs(Context context);




}
