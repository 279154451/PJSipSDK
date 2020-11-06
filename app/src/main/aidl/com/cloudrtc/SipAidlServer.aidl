// SipAidlServer.aidl
package com.cloudrtc;
import com.cloudrtc.ISipCallback;
import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.binder.XierHeader;
// Declare any non-default types here with import statements

interface SipAidlServer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

            void makeCall(in SipCallRequest sipCallRequest,in XierHeader header);

              void unRegister();

              void register(String sipHost,String phoneNumber,String password,in ISipCallback callback);

}
