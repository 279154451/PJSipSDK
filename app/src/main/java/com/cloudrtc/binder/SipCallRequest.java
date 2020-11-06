package com.cloudrtc.binder;

import android.os.Parcel;
import android.os.Parcelable;

import org.pjsip.pjsua2.SipHeaderVector;

/**
 * Created by seven on 2016/9/27.
 */

public class SipCallRequest implements Parcelable{
    private String fromName;
    private String fromNum;
    private String toName;
    private String toNum;
    private boolean openVideo;
    private SipHeaderVector headerVector;

    public SipCallRequest(String fromName, String fromNum, String toName, String toNum, boolean openVideo) {
        this.fromName = fromName;
        this.fromNum = fromNum;
        this.toName = toName;
        this.toNum = toNum;
        this.openVideo = openVideo;
    }

    public SipCallRequest() {
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromNum() {
        return fromNum;
    }

    public void setFromNum(String fromNum) {
        this.fromNum = fromNum;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToNum() {
        return toNum;
    }

    public void setToNum(String toNum) {
        this.toNum = toNum;
    }

    public boolean isOpenVideo() {
        return openVideo;
    }

    public void setOpenVideo(boolean openVideo) {
        this.openVideo = openVideo;
    }

    public void setHeaderVector(SipHeaderVector headerVector) {
        this.headerVector = headerVector;
    }

    public SipHeaderVector getHeaderVector() {
        return headerVector;
    }

    protected SipCallRequest(Parcel in) {
        fromName = in.readString();
        fromNum = in.readString();
        toName = in.readString();
        toNum = in.readString();
        openVideo = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromName);
        dest.writeString(fromNum);
        dest.writeString(toName);
        dest.writeString(toNum);
        dest.writeByte((byte) (openVideo ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SipCallRequest> CREATOR = new Creator<SipCallRequest>() {
        @Override
        public SipCallRequest createFromParcel(Parcel in) {
            return new SipCallRequest(in);
        }

        @Override
        public SipCallRequest[] newArray(int size) {
            return new SipCallRequest[size];
        }
    };

    @Override
    public String toString() {
        return "SipCallRequest{" +
                "fromName='" + fromName + '\'' +
                ", fromNum='" + fromNum + '\'' +
                ", toName='" + toName + '\'' +
                ", toNum='" + toNum + '\'' +
                ", openVideo=" + openVideo +
                '}';
    }
}
