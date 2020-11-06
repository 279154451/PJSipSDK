
package com.cloudrtc.mediaengine;


import android.content.Context;
import android.util.Log;

public class Engine {
  static {
      System.loadLibrary("pjmediaengine");
  }

  public class TraceListener implements EngineListener {
		public void webrtc_trace(String msg, int length, int level){
			Log.i("WEBRTC_TRACE", msg+"=========length:"+length+",level:"+level);
		}		
  }
  public void registertrace(Context context){
	  RegisterTraceInfo(new TraceListener(),null,0x00ff);
  }
  public native void register(Context context);
  public native void unRegister();
  public native void RegisterTraceInfo(EngineListener listener,String filename,int level);
  public native void UnRegisterTraceInfo();
;}
