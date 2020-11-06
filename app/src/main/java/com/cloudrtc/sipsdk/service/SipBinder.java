package com.cloudrtc.sipsdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.cloudrtc.ISipCallback;
import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.SipAidlServer;
import com.cloudrtc.binder.XierHeader;
import com.cloudrtc.sipsdk.api.SipAccountHelper;
import com.cloudrtc.sipsdk.api.SipCallHelper;
import com.cloudrtc.sipsdk.api.SipCallRecordHelper;
import com.cloudrtc.sipsdk.api.util.MMKVUtil;
import com.cloudrtc.sipsdk.api.util.SipCommandUtil;

import org.pjsip.pjsua2.SipHeader;
import org.pjsip.pjsua2.SipHeaderVector;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 创建时间：2020/6/11
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipBinder extends SipAidlServer.Stub {
    private SipService sipService;
    private Context context;
    private ISipCallback callback;

    public ISipCallback getCallback() {
        return callback;
    }

    public SipBinder(Context context, SipService sipService) {
        this.sipService = sipService;
        this.context = context;
    }

    @Override
    public void makeCall(SipCallRequest request, XierHeader header) throws RemoteException {
        if (header != null) {
            SipHeaderVector mSipHeaderV = new SipHeaderVector();
            SipHeader xierHeader = new SipHeader();
            xierHeader.setHName("X-xier");
            if (request.isOpenVideo()) {
                xierHeader.setHValue(SipCommandUtil.fullCall);
            } else {
                xierHeader.setHValue(SipCommandUtil.audioCall);
            }
            mSipHeaderV.add(xierHeader);
            Map<String, String> headerMap = header.getHeadMap();
            Set<Map.Entry<String, String>> entries = headerMap.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String name = next.getKey();
                String value = next.getValue();
                Log.d("SipBinder", "makeCall: name=" + name + " value=" + value);
                SipHeader sipHeader = new SipHeader();
                sipHeader.setHName(name);
                sipHeader.setHValue(value);
                mSipHeaderV.add(sipHeader);
            }
            request.setHeaderVector(mSipHeaderV);
            SipCallHelper.makeCall(request);
        }else {
            Log.d("SipBinder", "makeCall: header is null");
        }
    }

    @Override
    public void unRegister() throws RemoteException {
        Log.d("SipService", "unRegister: ");
        MMKVUtil.getUtil().put("UserId","");
        MMKVUtil.getUtil().put("SipHost","");
        MMKVUtil.getUtil().put("SipPwd","");
        SipAccountHelper.unRegister();
    }


    @Override
    public void register(final String sipHost, final String phoneNumber, final String password, ISipCallback callback) throws RemoteException {
        this.callback = callback;
        Log.d("SipService", "register: sipHost=" + sipHost + " phoneNumber=" + phoneNumber + " password=" + password);
        MMKVUtil.getUtil().put("UserId",phoneNumber);
        MMKVUtil.getUtil().put("SipHost",sipHost);
        MMKVUtil.getUtil().put("SipPwd",password);
        SipCallRecordHelper.getHelper().setSipCallback(callback);
        SipAccountHelper.register(context,false,sipHost,phoneNumber,password,false,false);
    }
}
