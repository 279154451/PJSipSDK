package com.cloudrtc.sipsdk.api.sip;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pj_turn_tp_type;
import org.pjsip.pjsua2.pjsip_cred_data_type;
import org.pjsip.pjsua2.pjsua_ipv6_use;

import static java.lang.Thread.sleep;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipAccountConfig extends SipAbstractConfig {
    private static String TAG = SipAccountConfig.class.getSimpleName();
    private volatile String turn_server = "fs.zhimahezi.net";
    private String phoneNumber = "user";
    public String maudio_codecs = "opus,isac,g729,pcma,pcmu";
    public String mvideo_codecs = "vp8,vp9,h264,red,ulpfec,rtx";
    private String server;
    private String password;
    private String transport_type_ = "tcp";
    private boolean useIce;
    public SipAccountConfig(String transport_type_,boolean useIce,String phoneNumber, String server,String turn_server, String password) {
        this.transport_type_ = transport_type_;
        this.useIce = useIce;
        this.phoneNumber = phoneNumber;
        this.server = server;
        this.password = password;
        this.turn_server = turn_server;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getServer() {
        return server;
    }

    public String getPassword() {
        return password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setServer(String server) {
        this.server = server;
        this.turn_server = server;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void clearConfig(){
        this.phoneNumber = "";
        this.server = "";
        this.password = "";
        this.turn_server = "";
    }

    @Override
    public String getTransportType() {
//        String transport_type_ = "tcp";
//        if("Xiaomi".equals(Build.MANUFACTURER) || "LENOVO".equals(Build.MANUFACTURER)){
//            transport_type_="tls";
//        }else {
//            transport_type_="tcp";
//        }
        return transport_type_;
    }

    @Override
    public String getTransportData() {
        String transport_type_ = getTransportType();
        String transport_data = "";
        if(transport_type_.equalsIgnoreCase("udp")) {
            transport_data = "";
        } else if(transport_type_.equalsIgnoreCase("tcp")) {
            transport_data = ";transport=tcp";
        } else if(transport_type_.equalsIgnoreCase("tls")) {
            transport_data = ";transport=tls";
        }
        return transport_data;
    }

    @Override
    public String getIdUri() {
        return "sip:" +phoneNumber +"@" +server +getTransportData();
    }

    @Override
    public String getRegisterUri() {
        return "sip:" +server +getTransportData();
    }

    @Override
    public long getTimeoutSec() {
        return 40;
    }

    @Override
    public long getDelayBeforeRefreshSec() {
        return 5;
    }

    @Override
    public AuthCredInfo getAuthCredInfo() {
        return new AuthCredInfo("Digest", "*", phoneNumber, 0,password);
    }

    @Override
    public String getTurnUserName() {
        return "xier_ice";
    }

    @Override
    public String getTurnPassword() {
        return "6yhNji9";
    }

    @Override
    public String getTurnServer() {
        return turn_server;
    }

    @Override
    public pj_turn_tp_type getTurnConnectType() {
        return pj_turn_tp_type.PJ_TURN_TP_UDP;
    }

    @Override
    public int getTurnPasswordType() {
        return pjsip_cred_data_type.PJSIP_CRED_DATA_PLAIN_PASSWD.swigValue();
    }

    @Override
    public boolean IceEnable() {
        return useIce;
    }

    @Override
    public boolean TurnEnable() {
        return true;
    }

    @Override
    public AccountConfig getAccountConfig() {
        AccountConfig accCfg =  new AccountConfig();
        accCfg.getNatConfig().setIceEnabled(IceEnable());
        accCfg.getNatConfig().setTurnEnabled(TurnEnable());
        accCfg.getNatConfig().setTurnPasswordType(getTurnPasswordType());
        accCfg.getNatConfig().setTurnConnType(getTurnConnectType());
        accCfg.getNatConfig().setTurnServer(getTurnServer());
        accCfg.getNatConfig().setContactRewriteUse(2);//allow_contact_rewrite
        accCfg.getNatConfig().setContactRewriteMethod(2);
        accCfg.getNatConfig().setViaRewriteUse(1);//allow_via_rewrite
        accCfg.getNatConfig().setSdpNatRewriteUse(1);//allow_sdp_nat_rewrite
        accCfg.getNatConfig().setIceAlwaysUpdate(true);
        accCfg.getMediaConfig().setIpv6Use(pjsua_ipv6_use.PJSUA_IPV6_ENABLED);
//				accCfg.getNatConfig().setTurnUserName(this.turn_user);
//				accCfg.getNatConfig().setTurnPassword(this.turn_password);
        //临时配置写死
        accCfg.getNatConfig().setTurnUserName(getTurnUserName());
        accCfg.getNatConfig().setTurnPassword(getTurnPassword());
//			}

        accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
        accCfg.getVideoConfig().setAutoShowIncoming(true);
        Log.e(TAG, Build.MANUFACTURER);
        Log.e(TAG,"transport_data======="+getTransportData());
        accCfg.setIdUri(getIdUri());
        accCfg.getRegConfig().setRegistrarUri(getRegisterUri());
        accCfg.getRegConfig().setDelayBeforeRefreshSec(getDelayBeforeRefreshSec());//注册超时前（timeoutSec）多长时间刷新客户端注册状态，默认5秒
        accCfg.getRegConfig().setTimeoutSec(getTimeoutSec());//40s自动注册时间间隔，默认300秒，即每5分钟注册一次
        accCfg.getRegConfig().setRetryIntervalSec(10);//注册失败后，重新发起注册的时间间隔，默认300秒
        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        if (phoneNumber.length() != 0) {
            creds.add(getAuthCredInfo());
        }
        StringVector proxies = accCfg.getSipConfig().getProxies();
        proxies.clear();
        return accCfg;
    }

    @Override
    public String getAudio_codecs(Context context) {
        String audio_codecs = "";
//        boolean audio_codecs_isac = (boolean) MMKVUtil.getUtil().get("audio_codecs_isac", true);
//        if(audio_codecs_isac)
//        {
//            audio_codecs += "ISAC";
//        }
//        boolean audio_codecs_opus = (boolean) MMKVUtil.getUtil().get("audio_codecs_opus", true);
//        if(audio_codecs_opus)
//        {
//            if(audio_codecs.length() > 0 )
//                audio_codecs += ",";
//            audio_codecs += "opus";
//        }
//        boolean audio_codecs_pcmu = (boolean) MMKVUtil.getUtil().get("audio_codecs_pcmu", true);
//        if(audio_codecs_pcmu)
//        {
//            if(audio_codecs.length() > 0 )
//                audio_codecs += ",";
//            audio_codecs += "PCMU";
//        }
//        boolean audio_codecs_pcma = (boolean) MMKVUtil.getUtil().get("audio_codecs_pcma", true);
//        if(audio_codecs_pcma)
//        {
//            if(audio_codecs.length() > 0 )
//                audio_codecs += ",";
//            audio_codecs += "PCMA";
//        }

        Log.d(TAG, "Set Audio Codecs : " + audio_codecs);

        this.maudio_codecs = audio_codecs;
        return maudio_codecs;
    }

    @Override
    public String getVideo_codecs(Context context) {
        String video_codecs = "";
//        boolean video_codecs_h264 = (boolean) MMKVUtil.getUtil().get("video_codecs_h264", true );
        if(true)
        {
            if(video_codecs.length() > 0 )
                video_codecs += ",";
            video_codecs += "H264";
        }

        Log.d(TAG, "Set Video Codecs : " + video_codecs);

        mvideo_codecs = video_codecs;
        return mvideo_codecs;
    }
}
