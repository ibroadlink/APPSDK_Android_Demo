package cn.com.broadlink.blappsdkdemo.view.scans.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLPermissionConstants;
import cn.com.broadlink.blappsdkdemo.common.BLPermissionUtils;
import cn.com.broadlink.blappsdkdemo.view.scans.camera.CameraManager;


public abstract class CaptureBaseActivity extends TitleActivity implements SurfaceHolder.Callback {
	private static final String TAG = CaptureBaseActivity.class.getSimpleName();

	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private InactivityTimer inactivityTimer;
	private CameraManager cameraManager;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	private Collection<BarcodeFormat> decodeFormats;

	private ViewfinderView mViewfinderView;
	private SurfaceView mScanInputView;

	private TextView mTvScanHint;
	private Button mBtnBottomHint;

	private boolean hasSurface;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);
		
		initData();

		initView();

		setListener();
	}
	
	private void initData() {
		hasSurface = false;

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
	}
	
	private void initView() {
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mScanInputView = (SurfaceView) findViewById(R.id.capture_view);
		mTvScanHint = (TextView) findViewById(R.id.tv_scan_hint);
		mBtnBottomHint = (Button) findViewById(R.id.btn_input);
	}

	private void setListener(){
		mViewfinderView.setOnDrawListenr(new ViewfinderView.OnViewfinderDrawListenr() {
			@Override
			public void onDrawComplete(int scannerEnd) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTvScanHint.getLayoutParams();
				params.topMargin = getViewfinderView().scannerEnd;
				mTvScanHint.setLayoutParams(params);
			}
		});
	}

	protected String scanHintText(){
		return null;
	}

	protected void setScanHint2(String hint){
		mBtnBottomHint.setText(hint);
	}

	protected Button getBottomHintView(){
		return mBtnBottomHint;
	}

	private void initHintView(){
		final String hint = scanHintText();
		if(TextUtils.isEmpty(hint)) return;

		mTvScanHint.setText(hint);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraManager = new CameraManager(getApplication());

		mViewfinderView.setCameraManager(cameraManager);
		
		handler = null;

		setRequestedOrientation(getCurrentOrientation());

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
		decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);

		SurfaceHolder surfaceHolder = mScanInputView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
		}

		initHintView();
	}

	private int getCurrentOrientation() {
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_90:
					return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				default:
					return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			}
		} else {
			switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_270:
					return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				default:
					return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			}
		}
	}

	/**识别错误 重新识别**/
	protected void scanQrCodeFailed(){
		handler.sendEmptyMessage(R.id.decode_failed);
	}
	
	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			mScanInputView.getHolder().removeCallback(this);
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			BLLog.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}
	
	/**
	 * A valid barcode has been found, so give an indication of success and show the results.
	 *
	 * @param rawResult The contents of the barcode.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param barcode   A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();

		if (barcode != null){
			beepManager.playBeepSoundAndVibrate();
			String result = rawResult.getText();
			onDealQR(result);
		}
	}
	
	public abstract void onDealQR(String result);
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, null, null, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			checkCameraPermiss();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			checkCameraPermiss();
		}
	}

	private void checkCameraPermiss(){
		if(!BLPermissionUtils.isGranted(BLPermissionConstants.CAMERA, BLPermissionConstants.STORAGE)){
			BLPermissionUtils.permission(BLPermissionConstants.CAMERA, BLPermissionConstants.STORAGE).callback(new BLPermissionUtils.FullCallback() {
				@Override
				public void onGranted(List<String> permissionsGranted) {}
				@Override
				public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
					back();
				}
			}).request();
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public ViewfinderView getViewfinderView() {
		return mViewfinderView;
	}

	public void drawViewfinder() {
		mViewfinderView.drawViewfinder();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}
//
//	public void setHintText(@NonNull String text){
//		viewfinderView.setHintText(text);
//	}
}
