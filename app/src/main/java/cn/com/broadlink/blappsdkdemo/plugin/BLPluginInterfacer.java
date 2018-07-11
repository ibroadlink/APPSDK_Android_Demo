package cn.com.broadlink.blappsdkdemo.plugin;


import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.blappsdkdemo.common.BLLog;

import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;


public interface BLPluginInterfacer {
	String TAG = "BLNativeBridge";

	/**参数错误**/
	int ERRCODE_PARAM = 2000;
	
	/**网络可用**/
	String NETWORK_AVAILAVLE = "available";

	/**网络不可用**/
	String NETWORK_UNAVAILAVLE = "unavailable";

	/**BL BSDK 启动时，获取 deviceID、user 信息和网络状态**/
	String DEVICEINO = "deviceinfo";
	
	/**设备控制**/
	String DNA_CONTROL = "devicecontrol";
	
	/**PSDK 通过这个接口获取来自 native 层的通知**/
	String NOTIFICATION = "notification";

	/** 从H5跳转到native的定时界面 **/
	String OPEN_TIMER = "openTimer";

	/** 从H5跳转到native的定时界面 **/
	String CLOSE_WEBVIEW = "closeWebView";

	/***设备认证**/
	String DEVICE_AUTH = "deviceAuth";

	/***获取用户信息**/
	String GET_USERINFO = "getUserInfo";

	/***获取家庭信息**/
	String GET_FAMILYINFO = "getFamilyInfo";

	/***获取家庭场景列表**/
	String GET_GETFAMILY_SCENELIST = "getFamilySceneList";

	/***获取家庭设备列表**/
	String GET_DEVICE_LIST = "getDeviceList";

	/***获取设备所在的房间**/
	String GET_DEVICEROOM = "getDeviceRoom";

	/***获取设备的profile信息**/
	String GET_DEV_PROFILE = "getDeviceProfile";

	/***获取联动列表**/
	String GET_LINKAGE_LIST = "getLinkageList";

	/***http请求接口**/
	String HTTP_REQUERT = "httpRequest";

	/***设备状态查询**/
	String DEVICE_STATUS_QUERY = "devStatusQuery";

	/***获取WIFI信息**/
	String GET_WIFI_INFO = "wifiInfo";


	/**设备控制**/
	class ControlTask extends AsyncTask<String, Void, String> {
		private CallbackContext callbackContext;
		private String extendStr;
		private Activity activity;

		public ControlTask(Activity activity, String extendStr, CallbackContext callbackContext){
			this.activity = activity;
			this.extendStr = extendStr;
			this.callbackContext = callbackContext;
		}

		@Override
		protected String doInBackground(String... params) {
			BLConfigParam configParam = null;
			if(!TextUtils.isEmpty(extendStr)){
				try {
					JSONObject jsonObject = new JSONObject(extendStr);

					Integer localTimeout = jsonObject.optInt("localTimeout");
					Integer remoteTimeout = jsonObject.optInt("remoteTimeout");

					configParam = new BLConfigParam();
					if(localTimeout != null&& localTimeout > 0){
						configParam.put(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT, String.valueOf(localTimeout));
					}

					if(remoteTimeout != null && remoteTimeout > 0){
						configParam.put(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT, String.valueOf(remoteTimeout));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String resulet = null;

			//重试三次
			for (int i = 0; i < 3; i++) {
				BLLog.d(TAG, "H5 Send Data Count:" + i);
				resulet = BLLet.Controller.dnaControl(params[0], params[1], params[2], params[3], configParam);
				if(resulet != null){
					try {
						JSONObject resultJsonObjece = new JSONObject(resulet);
						if(resultJsonObjece.optInt("status") == BLControllerErrCode.SUCCESS){
							return resulet;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			return resulet;				}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(callbackContext != null && activity != null) callbackContext.success(result);
		}
	}

}
