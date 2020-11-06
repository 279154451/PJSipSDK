package com.cloudrtc.sipsdk.api.sip;

import android.util.Log;

import com.cloudrtc.sipsdk.api.callback.SipLogListener;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipLogWriter extends LogWriter {
    private int  logLevel;
    private SipLogListener listener;
    public SipLogWriter(int  logLevel, SipLogListener logListener){
        this.logLevel = logLevel;
        this.listener = logListener;
    }
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void write(LogEntry entry)
    {
        Log.d("SipLogWriter", "sip_msg: "+entry.getMsg());
        if(listener != null){
            listener.onSipLog(entry.getMsg());
        }
    }
}
