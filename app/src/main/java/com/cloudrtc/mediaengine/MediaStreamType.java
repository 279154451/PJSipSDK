package com.cloudrtc.mediaengine;

import java.util.Vector;

public class MediaStreamType {
	static private Vector<MediaStreamType> values = new Vector<MediaStreamType>();
	public static MediaStreamType AudioStream = new MediaStreamType("AudioStream", 1);
	public static MediaStreamType VideoStream = new MediaStreamType("VideoStream", 2);
	private String mStringValue;
	private int mIntgerValue;
	private MediaStreamType(String aStringValue,int aIntgerValue) {
		mStringValue = aStringValue;
		mIntgerValue = aIntgerValue;
		values.addElement(this);
	}
	public String toString() {
		return mStringValue;
	}
	public int IntgerValue()
	{
		return mIntgerValue;
	}
	
	public static MediaStreamType fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			MediaStreamType mtype = (MediaStreamType) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("CallState not found [" + value + "]");
	}
}
