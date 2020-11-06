package com.cloudrtc.sipsdk.api.util;

import android.util.Log;

import org.json.JSONObject;

public class SipMessageUtil {
    public static final String TAG = "BizOnSip";

    public static final String BIZVER = "bizVer";
    public static final String CATEGORY = "category";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String DETAILS = "details";

    public static final String FROM = "from";

    public static boolean parseOpenDoorJson( String json ) {
        try {
            JSONObject extraObj = new JSONObject(json);
            Log.e(TAG, "onBizString: "+extraObj.toString());
            if( extraObj.getInt(BIZVER)==1){
                if( extraObj.getString(CATEGORY).equals("door")){
                    if( extraObj.getString(TYPE).equals("cmd")){
                        if( extraObj.getString(TITLE).equals("openDoor")){
                            Log.i(TAG, "SipMessageEvent.EVT_CMD_OPENDOOR");
                            return true;
                        }
                    }
                }
            }else {
                Log.e(TAG, "Unknown Version: " + extraObj.getInt(BIZVER) );
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unknown format: e=" + e.toString());
            return false;
        }

        return false;
    }
    public static JSONObject createOpenDoorJson(){
        JSONObject obj = new JSONObject();
        try{
            JSONObject detailsObj = new JSONObject();
            detailsObj.put(FROM,"");

            obj.put(CATEGORY, "door");
            obj.put(TYPE, "cmd");
            obj.put(TITLE, "openDoor");
            obj.put(DETAILS, detailsObj);
            obj.put(BIZVER, 1);

        }
        catch (Exception e){
            Log.e(TAG, "Exception : " + e.toString());
            return null;
        }

        return obj;
    }
}
