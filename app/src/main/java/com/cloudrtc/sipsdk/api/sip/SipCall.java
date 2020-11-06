
package com.cloudrtc.sipsdk.api.sip;

import android.util.Log;

import com.cloudrtc.sipsdk.api.entity.Direction;
import com.cloudrtc.sipsdk.api.callback.SipCallCallBack;

import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnCallTransferStatusParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_media_status;


/**
 * Singleton class to manage pjsip calls. It allows to convert retrieve pjsip
 * calls information and convert that into objects that can be easily managed on
 * Android side
 */
public class SipCall extends Call {
	private Direction mDirection;
	private boolean connected = false;
	private SipAccount sipAccount ;
	private void setConnected(boolean connected) {
		this.connected = connected;
		if(!connected){
			if(sipAccount != null){
				sipAccount.resetCall();
			}
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public SipCall(SipAccount acc, int call_id) {
    	super(acc, call_id);
    	this.sipAccount = acc;
    }
    private SipCallCallBack sipCallCallBack;

    private static final String THIS_FILE = "SipCall";
    
    private CallInfo ci;
    private String cur_call_id_;
    private pjsip_inv_state callstatus;

	public void setSipCallCallBack(SipCallCallBack sipCallCallBack) {
		this.sipCallCallBack = sipCallCallBack;
	}

	@Override
    public void onCallState(OnCallStateParam prm)
    {
	    try {
		ci = getInfo();
		cur_call_id_ = ci.getCallIdString();
		callstatus = ci.getState();
		Log.e(THIS_FILE, "=======================Call state <<========:" +callstatus);
		if (callstatus == 
		    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
		{
			if(ci.getLastStatusCode() == pjsip_status_code.PJSIP_SC_OK || ci.getLastStatusCode() == pjsip_status_code.PJSIP_SC_DECLINE)
			{
				Log.e(THIS_FILE, "=======================Call state <<========:OnCallEnded");
				this.sipCallCallBack.onCallEnd();
			} else {
				Log.e(THIS_FILE, "=======================Call state <<========:OnCallFailed");
				this.sipCallCallBack.onCallFailed(ci.getLastStatusCode().swigValue());
			}
			setConnected(false);
		} else if(callstatus ==
			    pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
	    	if(sipCallCallBack != null) {
	    		mDirection = Direction.Outgoing;
	    	}
	    } else if(callstatus == 
			    pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
            if(sipCallCallBack != null) {
            	mDirection = Direction.Incoming;
	    	}
	    } else if(callstatus == 
			    pjsip_inv_state.PJSIP_INV_STATE_EARLY) {

	    }  else if(callstatus == 
			    pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
			Log.e(THIS_FILE, "======================PJSIP_INV_STATE_CONNECTING<<========:\n" +prm.getE().getBody().getTsxState().getSrc().getRdata().getWholeMsg());
			if(mDirection == Direction.Outgoing)
			{
				if(sipCallCallBack != null)
				{
					String sdp = prm.getE().getBody().getTsxState().getSrc().getRdata().getWholeMsg();
					sipCallCallBack.onRemoteCommand(sdp);
				}
			}
	    }  else if(callstatus == 
			    pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
			setConnected(true);
            if(sipCallCallBack != null) {
				sipCallCallBack.onCallConnected(ci.getRemoteUri());
	    	}
	    } else if(callstatus == 
			    pjsip_inv_state.PJSIP_INV_STATE_NULL) {

	    } 
	    } catch (Exception e) {
		    return;
	    } 	    
    }

	@Override
	public void hangup(CallOpParam prm) throws Exception {
		super.hangup(prm);
		setConnected(false);
	}

	@Override
    public void onCallMediaState(OnCallMediaStateParam prm)
    {
	   CallInfo ci;
	   try {
	     ci = getInfo();
	  } catch (Exception e) {
	     return;
	 }
	  CallMediaInfoVector cmiv = ci.getMedia();

	  for (int i = 0; i < cmiv.size(); i++) {
	    CallMediaInfo cmi = cmiv.get(i);
	    if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
		(cmi.getStatus() == 
		 	pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
		 cmi.getStatus() == 
			pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
	    {
		
	    	
	    } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
		       cmi.getStatus() == 
			    pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE )
	    {

	    }
	  }
  }
    
    @Override
    public void onCallTransferStatus(OnCallTransferStatusParam prm) {
    
    }
    
    public Direction getDirection() {
    	return this.mDirection;
    }
    
    public void setDirection(Direction mDirection) {
        this.mDirection = mDirection;
    }

	/*public void SetCallOpParam(CallOpParam prm) {
		// TODO Auto-generated method stub
		this.mCallOpParam = prm;
	}
	
	public CallOpParam GetCallOpParam() {
		// TODO Auto-generated method stub
		return this.mCallOpParam;
	}*/
    
    @Override
    public void onCallDtmf()
    {
    	if(sipCallCallBack!=null){
    		sipCallCallBack.onCallDtmf();
		}
    }

	public boolean isCallActive(){
    	return isConnected();
//		try{
//			ci = getInfo();
//			callstatus = ci.getState();
//		}catch (Exception e){
//		}
//		if(callstatus == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED || callstatus == pjsip_inv_state.PJSIP_INV_STATE_EARLY){
//			return true;
//		}else
//		{
//			return false;
//		}
	}
 }
