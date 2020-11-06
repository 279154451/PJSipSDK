package com.cloudrtc.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudrtc.sipsdk.api.SipAccountHelper;
import com.cloudrtc.sipsdk.api.sip.SipVideoConfig;
import com.cloudrtc.sipsdk.api.util.SipMessageUtil;
import com.cloudrtc.sipsdk.api.SipCallHelper;
import com.cloudrtc.sipsdk.api.SipCallRecordHelper;
import com.cloudrtc.sipsdk.event.LiveDataBusHelper;
import com.cloudrtc.ui.base.BaseCompatFragment;
import com.cloudrtc.sipsdk.api.util.SipUtil;
import com.cloudrtc.ui.event.SipRegisterStatusEvent;
import com.cloudrtc.ui.util.DensityUtil;
import com.coder.sipsdk.R;

import org.json.JSONObject;
import org.webrtc.videoengine.ViERenderer;

import androidx.annotation.Nullable;


/**
 * 创建时间：2020/6/15
 * 创建人：singleCode
 * 功能描述：
 **/
public class AppSipCallConnectFragment extends BaseCompatFragment implements View.OnClickListener {
    private String INDOOR = "indoor", OUTDOOR = "outdoor", CENTER = "center", WALL = "wall";
    private LinearLayout ll_voice;
    private LinearLayout ll_hangup;
    private ImageView openDoorImg, mHangup, mVoice;
    private FrameLayout fl_user_icon, fl_video;
    private SurfaceView remoteRender;
    private RelativeLayout videoView;
    private TextView txt_name;
    private TextView txt_register_status;
    private Chronometer txt_time;
    private LinearLayout ll_open_door;
    private ProgressBar progressBar;

    public static AppSipCallConnectFragment getInstance(boolean videoCall) {
        AppSipCallConnectFragment fragment = new AppSipCallConnectFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("videoCall", videoCall);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sip_call_connect;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        txt_name = view.findViewById(R.id.txt_name);
        txt_time = view.findViewById(R.id.txt_time);
        fl_user_icon = view.findViewById(R.id.fl_user_icon);
        fl_video = view.findViewById(R.id.fl_video);
        txt_register_status = view.findViewById(R.id.txt_register_status);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        mVoice = view.findViewById(R.id.btn_voice);
        ll_voice = view.findViewById(R.id.ll_voice);
        ll_hangup = (LinearLayout) view.findViewById(R.id.ll_hangup);
        mHangup = view.findViewById(R.id.hangup);
        ll_open_door = view.findViewById(R.id.ll_open_door);
        videoView = (RelativeLayout) view.findViewById(R.id.llRemoteView);
        openDoorImg = (ImageView) view.findViewById(R.id.img_open_door);
        LiveDataBusHelper.register(this, SipRegisterStatusEvent.class, event -> {
            checkRegister();
        });
        setCallInfo(SipCallRecordHelper.getHelper().isVideoCall());
        initSurface();
        checkRegister();
        startTimeTicker();
        setClickListener();
    }

    private void setClickListener() {
        mVoice.setOnClickListener(this);
        mHangup.setOnClickListener(this);
        videoView.setOnClickListener(this);
        openDoorImg.setOnClickListener(this);
    }

    private void setCallInfo(boolean isVideoCall) {
        String fromName = SipCallRecordHelper.getHelper().getCurrentCallName();
        setVoiceVisibility(isVideoCall);
        if (isVideoCall) {
//            fl_video.setVisibility(View.VISIBLE);
            txt_name.setText("正在与" + (TextUtils.isEmpty(fromName) ? "" : fromName) + "视频通话");
        } else {
            fl_video.setVisibility(View.GONE);
            txt_name.setText("正在与" + (TextUtils.isEmpty(fromName) ? "" : fromName) + "语音通话");
        }
        String devType = SipCallRecordHelper.getHelper().getCurrentCallDevType();
        if (WALL.equals(devType) || OUTDOOR.equals(devType)) {
            ll_open_door.setVisibility(View.VISIBLE);
        } else {
            ll_open_door.setVisibility(View.GONE);
        }
    }

    private void startTimeTicker() {
        SipCallRecordHelper.getHelper().setAnswerTime(System.currentTimeMillis());
        txt_time.setBase(SystemClock.elapsedRealtime());//计时器清零
        txt_time.start();
    }

    private void checkRegister() {
        if (SipAccountHelper.isRegister()) {
            txt_register_status.setTextColor(getResources().getColor(R.color.flag_color_FFFFFF_40p));
            txt_register_status.setText("SIP注册成功：" + SipCallRecordHelper.getHelper().getSipServer());
        } else {
            txt_register_status.setTextColor(getResources().getColor(R.color.flag_color_D14836));
            txt_register_status.setText("SIP注册失败：" + SipCallRecordHelper.getHelper().getSipServer());
        }
    }

    private void initSurface() {
        int numCamera = SipUtil.getNumberOfCameras();
        boolean usingFrontCamera = SipUtil.checkCameraAndChoiceBetter();
        int cameraOrientation = SipCallHelper.getCameraOrientation(usingFrontCamera);
        remoteRender = ViERenderer.CreateRenderer(getActivity(), true);
        videoView.addView(remoteRender);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String video_size = sp.getString("sip_account_video_size", "cif");
        int rotation = getCameraOrientation(cameraOrientation);
        if (numCamera == 1) {
            SipCallHelper.startVideoChannel(mContext,false,rotation,getVideoConfig(video_size),null,remoteRender);
        } else {
            SipCallHelper.startVideoChannel(mContext,usingFrontCamera,rotation,getVideoConfig(video_size),null,remoteRender);
        }
    }

    private SipVideoConfig getVideoConfig(String defVideoSize){
        int width = 352;
        int height = 288;
        int bitrate = 384;
        int fps = 15;
        if (defVideoSize.equals("qvga")) {
            width = 320;
            height = 240;
            bitrate = 256;
            fps = 12;
        } else if (defVideoSize.equals("cif")) {
            width = 352;
            height = 288;
            bitrate = 384;
            fps = 15;

        } else if (defVideoSize.equals("vga")) {
            width = 640;
            height = 480;
            bitrate = 512;
            fps = 15;
        } else if (defVideoSize.equals("hd")) {
            width = 1280;
            height = 720;
            bitrate = 1024;
            fps = 17;
        }
        return new SipVideoConfig(width,height,fps,bitrate);
    }

    public int getCameraOrientation(int cameraOrientation) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int displatyRotation = display.getRotation();
        int degrees = 0;
        switch (displatyRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = 0;
        if (cameraOrientation > 180) {
            result = (cameraOrientation + degrees) % 360;
        } else {
            result = (cameraOrientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_voice == v.getId()) {
            SipCallHelper.switchToAudio();
        } else if (R.id.hangup == v.getId()) {
            SipCallHelper.hangUp();
            finishActivity();
        } else if (R.id.img_open_door == v.getId()) {
            JSONObject openDoorJson = SipMessageUtil.createOpenDoorJson();
            if (openDoorJson == null) {
                return;
            }
            SipCallHelper.openDoor(openDoorJson.toString());
        }
    }

    private void finishActivity() {
        AppSipCallActivity activity = (AppSipCallActivity) getActivity();
        if (activity != null) {
            activity.finishActivity();
        }
    }

    public void onVideoFrameSizeChanged(int video_channel, int frameWidth, int frameHeight) {
        if (videoView != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = width - DensityUtil.dip2px(getContext(), 40);
            int maxHeight = DensityUtil.dip2px(getContext(), 200);
            int height = (int) (((float) frameHeight) * width / frameWidth);
            if (height > maxHeight) {
                params.height = maxHeight;
            } else {
                params.height = height;
            }
            videoView.setLayoutParams(params);
            fl_video.setVisibility(View.VISIBLE);
        }
    }


    public void closeVideo(boolean close) {
        if (close) {
            setCallInfo(false);
        } else {
            setCallInfo(true);
        }
    }

    private void setVoiceVisibility(boolean isVideoCall) {
        if (isVideoCall) {
            fl_user_icon.setVisibility(View.GONE);
            ll_voice.setVisibility(View.VISIBLE);
        } else {
            fl_user_icon.setVisibility(View.VISIBLE);
            ll_voice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SipCallHelper.stopVideoChannel();
    }
}
