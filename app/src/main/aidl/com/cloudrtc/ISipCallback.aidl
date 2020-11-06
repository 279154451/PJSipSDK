// ISipCallback.aidl
package com.cloudrtc;
import com.cloudrtc.binder.CallRecordBean;
// Declare any non-default types here with import statements

interface ISipCallback {

    void onResult(int responseCode, String actionName, String response);

    void onOpenDoor( String orgId,String uniqNum,String name,String subOrgId);

    void onCallRecord(inout CallRecordBean callRecord);
}
