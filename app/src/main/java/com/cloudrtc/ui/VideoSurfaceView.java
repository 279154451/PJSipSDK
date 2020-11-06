package com.cloudrtc.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class VideoSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private Context context;
	private boolean is_tvbox = false;

	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public VideoSurfaceView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public Camera camera;
	private SurfaceHolder holder;

	@SuppressLint("NewApi")
	private void init() {
		//is_tvbox = PhoneService.instance().isTvBoxMode();
		boolean hasFront = false;//UIUtils.checkCameraAndChoiceBetter();
		is_tvbox = true;
		
		/*if(is_tvbox)
			hasFront = true;
		if (hasFront) {
			if (android.os.Build.VERSION.SDK_INT > 8) {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			} else {
				camera = Camera.open();
			}
		} else {
			if (android.os.Build.VERSION.SDK_INT > 8) {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			} else {
				camera = Camera.open();
			}
		}
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/
	}

	private int getDeviceOrientation() {
		int orientation = 0;
		if (context != null) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			switch (wm.getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_90:
				orientation = 90;
				break;
			case Surface.ROTATION_180:
				orientation = 180;
				break;
			case Surface.ROTATION_270:
				orientation = 270;
				break;
			case Surface.ROTATION_0:
			default:
				orientation = 0;
				break;
			}
		}
		return orientation;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		/*try {
			if (camera != null) {
				camera.setPreviewDisplay(holder);
				int rotation = getDeviceOrientation();
				rotation = (90 + rotation) % 360;
				if (rotation == 0) {
					rotation = 180;
				} else if (rotation == 180) {
					rotation = 0;
				}
				if(is_tvbox)
					camera.setDisplayOrientation(0);
				else
					camera.setDisplayOrientation(rotation);
				camera.startPreview();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//if (camera != null) {
		//	setDisplayOrientation();
		//}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//if (camera != null) {
			//camera.release();
			//camera = null;
		//}
	}

	public void setDisplayOrientation() {
		int rotation = getDeviceOrientation();
		rotation = (90 + rotation) % 360;
		if (rotation == 0) {
			rotation = 180;
		} else if (rotation == 180) {
			rotation = 0;
		}
		//if(is_tvbox)
			//camera.setDisplayOrientation(0);
		//else
			//camera.setDisplayOrientation(rotation);
	}
}
