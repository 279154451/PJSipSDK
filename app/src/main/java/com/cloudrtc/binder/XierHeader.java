package com.cloudrtc.binder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间：2020/11/2
 * 创建人：singleCode
 * 功能描述：
 **/
public class XierHeader implements Parcelable {
    private Map<String,String> headMap = new HashMap<>();

    public XierHeader() {
    }

    protected XierHeader(Parcel in) {
        headMap = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(headMap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<XierHeader> CREATOR = new Creator<XierHeader>() {
        @Override
        public XierHeader createFromParcel(Parcel in) {
            return new XierHeader(in);
        }

        @Override
        public XierHeader[] newArray(int size) {
            return new XierHeader[size];
        }
    };

    public void setHeader(String name, String value) {
        this.headMap.put(name,value);
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }
}
