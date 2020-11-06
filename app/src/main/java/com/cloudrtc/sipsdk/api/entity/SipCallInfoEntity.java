package com.cloudrtc.sipsdk.api.entity;

/**
 * 创建时间：2020/11/2
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipCallInfoEntity {
    private String fromName;//来电人名字
    private String fromNumber;//来电人电话
    private String fromOrgId;//来电机构ID
    private String fromEquipmentId;//来电设备号
    private String callType;//来电类型,0x0001 | 0x0010 | 0x0011 (本地语音，视频，语音视频呼叫)
    private String toNumber;//去电号码
    private String toName;//去电姓名
    private long startTime;//通话开始时间
    private int direction;//通话方向 来电：0 去电 1
    private int openVideo;//1：视频通话 0：语音通话
    private String sipServer;//sip服务地址
    private String devType;//（设备类型）：phone | indoor | outdoor | center (分别对应手机 室内机 室外机 中心机)
    public SipCallInfoEntity(String sipServer,String name,String number,String orgId,String equipmentId,String callType,String devType,int openVideo,int direction){
        if(direction>0){
            this.toName = name;
            this.toNumber = number;
        }else {
            this.fromName = name;
            this.fromNumber = number;
        }
        this.fromOrgId = orgId;
        this.fromEquipmentId = equipmentId;
        this.devType = devType;
        this.callType = callType;
        this.openVideo =openVideo;
        this.sipServer = sipServer;
        startTime = System.currentTimeMillis();
    }

    public String getDevType() {
        return devType;
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

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getOpenVideo() {
        return openVideo;
    }

    public void setOpenVideo(int openVideo) {
        this.openVideo = openVideo;
    }

    public String getSipServer() {
        return sipServer;
    }

    public void setSipServer(String sipServer) {
        this.sipServer = sipServer;
    }

    @Override
    public String toString() {
        return "SipCallInfoEntity{" +
                "fromName='" + fromName + '\'' +
                ", fromNumber='" + fromNumber + '\'' +
                ", fromOrgId='" + fromOrgId + '\'' +
                ", fromEquipmentId='" + fromEquipmentId + '\'' +
                ", callType='" + callType + '\'' +
                ", toNumber='" + toNumber + '\'' +
                ", toName='" + toName + '\'' +
                ", startTime=" + startTime +
                ", direction=" + direction +
                ", openVideo=" + openVideo +
                ", sipServer='" + sipServer + '\'' +
                ", devType='" + devType + '\'' +
                '}';
    }
}
