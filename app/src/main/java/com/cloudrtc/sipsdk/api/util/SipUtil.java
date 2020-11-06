package com.cloudrtc.sipsdk.api.util;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipUtil {

    public static String parseMediaCommand (
            String sdpDescription) {
        return getHeaderCommand(sdpDescription,"X-xier:");
    }
    public static String getHeaderCommand(String sdpDescription,String headerKey){
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
//		String mediaDescription = "X-xier:";
        for(int i=0; i<lines.length; i++) {
            if (lines[i].startsWith(headerKey)) {
                mLineIndex = i;
                continue;
            }
        }
        if (mLineIndex == -1) {
            Log.w("SipCall", "No " + headerKey + " line");
            return null;
        }
        String commands[] = lines[mLineIndex].split(":");
        String command = commands[1].trim();
        Log.d("SipCall", "=====Found====" + command);
        return command;
    }

    public static boolean isNullStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        } else if ("null".equals(str.trim().toLowerCase())) {
            return true;
        } else if(str == null){
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean checkCameraAndChoiceBetter() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            int count = Camera.getNumberOfCameras();

            for (int i = 0; i < count; i++) {

                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);

                if (info != null) {

                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        return true;
                    }
                }
            }
            return false;
        }

        return false;
    }

    public static int getNumberOfCameras() {
        int count = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            count = Camera.getNumberOfCameras();
        }
        return count;
    }
}
