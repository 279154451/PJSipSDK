package com.cloudrtc.sipsdk.api.entity;

import java.util.Vector;

public class Direction {
	private static Vector<Direction> values = new Vector<Direction>();
	public static Direction Incoming = new Direction("Callincoming",0);
	public static Direction Outgoing = new Direction("CallOutgoing",1);
	private String mStringValue;
	private int mIntgerValue;
	
	private Direction(String aStringValue, int aIntgerValue) {
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
	
	public static Direction fromInt(int value) {
		for (int i = 0; i < values.size(); i++) {
			Direction mtype = (Direction) values.elementAt(i);
			if (mtype.mIntgerValue == value) return mtype;
		}
		throw new RuntimeException("Direction not found [" + value + "]");
	}
}