package com.cloudrtc.ui;

import android.os.CountDownTimer;
import android.util.Log;

import com.cloudrtc.sipsdk.api.SipCallHelper;
import com.cloudrtc.ui.activity.AppSipCallActivity;

import java.lang.ref.WeakReference;

/**
 * 创建时间：2020/11/3
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipCallCountTimer extends CountDownTimer {
    private String TAG = SipCallCountTimer.class.getSimpleName();
    private WeakReference<AppSipCallActivity> weakReference;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SipCallCountTimer(AppSipCallActivity activity,long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        weakReference = new WeakReference<>(activity);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        Log.d(TAG, "CountTimer onFinish ");
        AppSipCallActivity appSipCallActivity = weakReference.get();
        if(appSipCallActivity != null){
            SipCallHelper.hangUp();
            appSipCallActivity.finishActivity();
        }
    }
}
