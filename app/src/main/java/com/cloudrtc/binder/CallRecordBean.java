package com.cloudrtc.binder;

import android.os.Parcel;
import android.os.Parcelable;

import com.cloudrtc.sipsdk.event.IBaseEvent;


/**
 * 创建时间：2020/8/28
 * 创建人：singleCode
 * 功能描述：
 **/
public class CallRecordBean implements IBaseEvent, Parcelable{
    private String fromName;//来电人名字
    private String fromNumber;//来电人电话
    private String fromOrgId;//来电机构ID
    private String fromEquipmentId;//来电设备号
    private String callType;//来电类型,0x0001 | 0x0010 | 0x0011 (本地语音，视频，语音视频呼叫)
    private String toNumber;//拨打号码
    private String toName;//拨打姓名
    private long startTime;//通话开始时间
    private int callStatus;//通话状态，接听：1、未接：0
    private long duration;//通话时长(startTime-answerTime)
    private int direction;//通话方向 来电：0 去电 1
    private int moreMark;//预留字段
    private String moreDesc;//预留字段
    private int openVideo;//1：视频通话 0：语音通话
    private String devType;//（设备类型）：phone | indoor | outdoor | center (分别对应手机 室内机 室外机 中心机)
    private int register;//0：未注册 1：注册
    private long answerTime;//通话接听时间
    private String sipServer;//sip服务地址
    public CallRecordBean(){

    }

    public CallRecordBean(String fromName, String fromNumber) {
        this.fromName = fromName;
        this.fromNumber = fromNumber;
    }

    public CallRecordBean(String fromName, String fromNumber, String fromOrgId, String fromEquipmentId, String callType, String toNumber, String toName, long startTime, int callStatus, long duration, int direction, int moreMark, String moreDesc, int openVideo, String devType, int register, long answerTime, String sipServer) {
        this.fromName = fromName;
        this.fromNumber = fromNumber;
        this.fromOrgId = fromOrgId;
        this.fromEquipmentId = fromEquipmentId;
        this.callType = callType;
        this.toNumber = toNumber;
        this.toName = toName;
        this.startTime = startTime;
        this.callStatus = callStatus;
        this.duration = duration;
        this.direction = direction;
        this.moreMark = moreMark;
        this.moreDesc = moreDesc;
        this.openVideo = openVideo;
        this.devType = devType;
        this.register = register;
        this.answerTime = answerTime;
        this.sipServer = sipServer;
    }

    public CallRecordBean(int direction, String sipServer,int openVideo, long startTime){
        this.direction = direction;
        this.startTime = startTime;
        this.openVideo = openVideo;
        this.sipServer = sipServer;
    }

    protected CallRecordBean(Parcel in) {
        fromName = in.readString();
        fromNumber = in.readString();
        fromOrgId = in.readString();
        fromEquipmentId = in.readString();
        callType = in.readString();
        toNumber = in.readString();
        toName = in.readString();
        startTime = in.readLong();
        callStatus = in.readInt();
        duration = in.readLong();
        direction = in.readInt();
        moreMark = in.readInt();
        moreDesc = in.readString();
        openVideo = in.readInt();
        devType = in.readString();
        register = in.readInt();
        answerTime = in.readLong();
        sipServer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromName);
        dest.writeString(fromNumber);
        dest.writeString(fromOrgId);
        dest.writeString(fromEquipmentId);
        dest.writeString(callType);
        dest.writeString(toNumber);
        dest.writeString(toName);
        dest.writeLong(startTime);
        dest.writeInt(callStatus);
        dest.writeLong(duration);
        dest.writeInt(direction);
        dest.writeInt(moreMark);
        dest.writeString(moreDesc);
        dest.writeInt(openVideo);
        dest.writeString(devType);
        dest.writeInt(register);
        dest.writeLong(answerTime);
        dest.writeString(sipServer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CallRecordBean> CREATOR = new Creator<CallRecordBean>() {
        @Override
        public CallRecordBean createFromParcel(Parcel in) {
            return new CallRecordBean(in);
        }

        @Override
        public CallRecordBean[] newArray(int size) {
            return new CallRecordBean[size];
        }
    };

    public void setOpenVideo(int openVideo) {
        this.openVideo = openVideo;
    }



    public void readFromParcel(Parcel in){
        fromName = in.readString();
        fromNumber = in.readString();
        fromOrgId = in.readString();
        fromEquipmentId = in.readString();
        callType = in.readString();
        toNumber = in.readString();
        toName = in.readString();
        startTime = in.readLong();
        callStatus = in.readInt();
        duration = in.readLong();
        direction = in.readInt();
        moreMark = in.readInt();
        moreDesc = in.readString();
        openVideo = in.readInt();
        devType = in.readString();
        register = in.readInt();
        answerTime=  in.readLong();
        sipServer = in.readString();
    }

    public void setSipServer(String sipServer) {
        this.sipServer = sipServer;
    }

    public String getSipServer() {
        return sipServer;
    }

    public int getOpenVideo() {
        return openVideo;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getFromOrgId() {
        return fromOrgId;
    }

    public void setFromOrgId(String fromOrgId) {
        this.fromOrgId = fromOrgId;
    }

    public String getFromEquipmentId() {
        return fromEquipmentId;
    }

    public void setFromEquipmentId(String fromEquipmentId) {
        this.fromEquipmentId = fromEquipmentId;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getMoreMark() {
        return moreMark;
    }

    public void setMoreMark(int moreMark) {
        this.moreMark = moreMark;
    }

    public String getMoreDesc() {
        return moreDesc;
    }

    public void setMoreDesc(String moreDesc) {
        this.moreDesc = moreDesc;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    public void setAnswerTime(long answerTime) {
        this.answerTime = answerTime;
    }

    public long getAnswerTime() {
        return answerTime;
    }

    @Override
    public String toString() {
        return "CallRecordBean{" +
                "fromName='" + fromName + '\'' +
                ", fromNumber='" + fromNumber + '\'' +
                ", fromOrgId='" + fromOrgId + '\'' +
                ", fromEquipmentId='" + fromEquipmentId + '\'' +
                ", fromCallType='" + callType + '\'' +
                ", toNumber='" + toNumber + '\'' +
                ", toName='" + toName + '\'' +
                ", startTime=" + startTime +
                ", callStatus=" + callStatus +
                ", duration=" + duration +
                ", direction=" + direction +
                ", moreMark=" + moreMark +
                ", moreDesc='" + moreDesc + '\'' +
                ", openVideo=" + openVideo +
                ", devType='" + devType + '\'' +
                ", register=" + register +
                ", answerTime=" + answerTime +
                ", sipServer='" + sipServer + '\'' +
                '}';
    }
}
