package com.cloudrtc.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudrtc.sipsdk.api.SipAccountHelper;
import com.cloudrtc.sipsdk.api.SipCallHelper;
import com.cloudrtc.sipsdk.api.SipCallRecordHelper;
import com.cloudrtc.sipsdk.event.LiveDataBusHelper;
import com.cloudrtc.ui.SipCallCountTimer;
import com.cloudrtc.ui.base.BaseCompatFragment;
import com.cloudrtc.ui.event.SipRegisterStatusEvent;
import com.cloudrtc.ui.media.MediaPlayerHelper;
import com.coder.sipsdk.R;

import androidx.annotation.Nullable;


/**
 * 创建时间：2020/6/15
 * 创建人：singleCode
 * 功能描述：
 **/
public class AppSipCallWaitFragment extends BaseCompatFragment {
    private LinearLayout layoutAnswer;
    private LinearLayout layoutClose;
    private LinearLayout layout_error;
    private ImageView btnAnswer;
    private TextView answerTxt;
    private TextView txt_name;
    private TextView txt_hangup;
    private TextView txt_register_status;
    private SipCallCountTimer countTimer;
    public static AppSipCallWaitFragment getInstance() {
        AppSipCallWaitFragment fragment = new AppSipCallWaitFragment();
        return fragment;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sip_call_wait;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        countTimer = new SipCallCountTimer((AppSipCallActivity) getActivity(),30 * 1000,1000);
        layoutAnswer = (LinearLayout) view.findViewById(R.id.layout_answer);
        layoutClose = (LinearLayout) view.findViewById(R.id.layout_close);
        layout_error = view.findViewById(R.id.layout_error);
        btnAnswer = (ImageView) view.findViewById(R.id.btn_answer);
        answerTxt = (TextView) view.findViewById(R.id.txt_answer);
        txt_name = view.findViewById(R.id.txt_name);
        txt_hangup = view.findViewById(R.id.txt_hangup);
        txt_register_status = view.findViewById(R.id.txt_register_status);
        String name = SipCallRecordHelper.getHelper().getCurrentCallName();
        if (!SipCallRecordHelper.getHelper().isOutCall()) {
            MediaPlayerHelper.getInstance().startMediaPlayer(mContext,"callRing",R.raw.ringtone_long,true,null);
            layoutAnswer.setVisibility(View.VISIBLE);
            if (!SipCallRecordHelper.getHelper().isVideoCall()){
                btnAnswer.setBackgroundResource(R.drawable.btn_voice);
                answerTxt.setText(getResources().getString(R.string.accept_voice));
                txt_name.setText(TextUtils.isEmpty(name) ? "" : name+"语音来电");
            }else {
                btnAnswer.setBackgroundResource(R.drawable.btn_answer_video_phone);
                answerTxt.setText(getResources().getString(R.string.accept_video));
                txt_name.setText(TextUtils.isEmpty(name) ? "" : name+"视频来电");
            }
        } else {
            MediaPlayerHelper.getInstance().startMediaPlayer(mContext,"callRing",R.raw.outbound_ringback_tone,true,null);
            layoutAnswer.setVisibility(View.GONE);
            txt_name.setText(TextUtils.isEmpty(name) ? "" : "正在呼叫"+name);
        }
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
              SipCallHelper.answerCall();
            }
        });

        layoutClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SipCallHelper.hangUp();
                finishActivity();
            }
        });
        layout_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SipCallHelper.hangUp();
                finishActivity();
            }
        });
        checkUI(SipAccountHelper.isRegister());
        LiveDataBusHelper.register(this, SipRegisterStatusEvent.class, event -> {
            checkUI(event.isRegister());
        });
    }
    private void finishActivity(){
        AppSipCallActivity activity = (AppSipCallActivity) getActivity();
        if(activity != null){
            activity.finishActivity();
        }
    }
    private void checkUI(boolean isRegister){
        if(!isRegister){
            txt_register_status.setText("SIP注册失败："+ SipCallRecordHelper.getHelper().getSipServer());
            txt_register_status.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.VISIBLE);
            layoutAnswer.setVisibility(View.GONE);
            layoutClose.setVisibility(View.GONE);
        }else {
            txt_register_status.setVisibility(View.GONE);
            layout_error.setVisibility(View.GONE);
            if(!SipCallRecordHelper.getHelper().isOutCall()){
                layoutAnswer.setVisibility(View.VISIBLE);
                layoutClose.setVisibility(View.VISIBLE);
                txt_hangup.setText("拒绝");
            }else {
                layoutAnswer.setVisibility(View.GONE);
                layoutClose.setVisibility(View.VISIBLE);
                txt_hangup.setText("挂断");
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        countTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        countTimer.cancel();
    }
}
