package com.cloudrtc.sipsdk.api;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.cloudrtc.binder.CallRecordBean;
import com.cloudrtc.ISipCallback;
import com.cloudrtc.sipsdk.api.util.SipCommandUtil;


/**
 * 创建时间：2020/8/28
 * 创建人：singleCode
 * 功能描述：通话记录帮助类
 **/
public class SipCallRecordHelper {
    private String TAG = SipCallRecordHelper.class.getSimpleName();
    private static SipCallRecordHelper helper;
    private CallRecordBean recordBean;
    private boolean isRegister = false;
    private ISipCallback sipCallback;

    public void setSipCallback(ISipCallback sipCallback) {
        this.sipCallback = sipCallback;
    }

    public void setRegister(boolean register) {
        Log.d(TAG, "setRegister: " + register);
        isRegister = register;
    }

    /**
     * 注册状态
     * @return
     */
    protected boolean isRegister() {
        return isRegister;
    }

    private SipCallRecordHelper() {

    }

    /**
     * 是否为向外拨打电话
     * @return
     */
    public boolean isOutCall(){
        if(recordBean != null){
            if(recordBean.getDirection()>0){
                return true;
            }
        }
        return false;
    }

    public String getCurrentCallDevType(){
        if(recordBean != null){
            return recordBean.getDevType();
        }else {
            return "";
        }
    }

    public String getCurrentCallType(){
        if(recordBean != null){
            return recordBean.getCallType();
        }else {
            return "";
        }
    }

    public boolean isVideoCall(){
        String callType = getCurrentCallType();
        if (SipCommandUtil.audioCall.equals(callType)){
            return false;
        }else {
            return true;
        }
    }
    public String getSipServer(){
        if(recordBean != null){
            return recordBean.getSipServer();
        }else {
            return "";
        }
    }

    public String getCurrentCallName(){
        if(isOutCall()){
            if(recordBean != null){
                return recordBean.getToName();
            }else {
                return "";
            }
        }else {
            if(recordBean != null){
                if(TextUtils.equals(recordBean.getDevType(),"outdoor")){
                    return recordBean.getFromName()+"室外机";
                }else {
                    return recordBean.getFromName();
                }
            }else {
                return "";
            }
        }
    }

    public static SipCallRecordHelper getHelper() {
        if (helper == null) {
            synchronized (SipCallRecordHelper.class) {
                if (helper == null) {
                    helper = new SipCallRecordHelper();
                }
            }
        }
        return helper;
    }

    public void newCall(int direction, String sipServer, boolean openVideo) {
        if (recordBean == null) {
            recordBean = new CallRecordBean(direction, sipServer, openVideo ? 1 : 0, System.currentTimeMillis());
            recordBean.setRegister(isRegister ? 1 : 0);
        }
    }

    /**
     * @param name   通话名称
     * @param number 通话号码
     */
    public void setCallInfo(String name, String number) {
        if (recordBean != null) {
            if (recordBean.getDirection() > 0) {
                recordBean.setToName(name);
                recordBean.setToNumber(number);
            } else {
                recordBean.setFromName(name);
                recordBean.setFromNumber(number);
            }
        }
    }

    /**
     * @param fromOrgId       //来电机构ID
     * @param fromEquipmentId //来电设备号
     */
    public void setDeviceInfo(String fromOrgId, String fromEquipmentId, String devType) {
        if (recordBean != null) {
            recordBean.setFromOrgId(fromOrgId);
            recordBean.setFromEquipmentId(fromEquipmentId);
            recordBean.setDevType(devType);
        }
    }

    public void setCallType(String callType) {
        if (recordBean != null) {
            recordBean.setCallType(callType);
        }
    }

    public void setAnswerTime(long time) {
        if (recordBean != null) {
            recordBean.setAnswerTime(time);
        }
    }

    /**
     * @param duration 通话时长
     */
    private void setCallDuration(long duration) {
        if (recordBean != null) {
            if (duration > 0) {//已接听
                recordBean.setCallStatus(1);
            } else {//未接
                recordBean.setCallStatus(0);
            }
            recordBean.setDuration(duration);
        }
    }

    public void reportCallRecord() {
        CallRecordBean recordBean = getRecordBean(true);
        if (recordBean != null && sipCallback != null) {
            Log.d(TAG, "reportCallRecord: " + recordBean);
            try {
                sipCallback.onCallRecord(recordBean);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public CallRecordBean getRecordBean(boolean copy) {
        if (recordBean != null) {
            if (copy) {
                setCallDuration(recordBean.getAnswerTime() == 0 ? 0 : (System.currentTimeMillis() - recordBean.getAnswerTime()));
                CallRecordBean callRecordBean = new CallRecordBean(recordBean.getFromName(), recordBean.getFromNumber()
                        , recordBean.getFromOrgId(), recordBean.getFromEquipmentId(), recordBean.getCallType(), recordBean.getToNumber(),
                        recordBean.getToName(), recordBean.getStartTime(), recordBean.getCallStatus(), recordBean.getDuration(),
                        recordBean.getDirection(), recordBean.getMoreMark(), recordBean.getMoreDesc(), recordBean.getOpenVideo()
                        , recordBean.getDevType(), recordBean.getRegister(), recordBean.getAnswerTime(), recordBean.getSipServer());
                recordBean = null;
                return callRecordBean;
            } else {
                return recordBean;
            }
        }
        return null;
    }

}
