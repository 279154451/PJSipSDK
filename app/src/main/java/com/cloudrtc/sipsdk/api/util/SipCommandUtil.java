package com.cloudrtc.sipsdk.api.util;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipCommandUtil {
    public static String audioCall = "0x0001"; //本地语音呼叫
    public static String videoCall = "0x0010";//本地视频呼叫
    public static String fullCall = "0x0011";//本地语音视频呼叫

    public static String raudioCall = "0x0100";//远程语音呼叫
    public static String rvideoCall = "0x1000";//远程视频呼叫
    public static String rfullCall = "0x1100";//远程语音视频呼叫

    public static String SurveillanceCommand = "0x1111";
}
