package cn.com.broadlink.blappsdkdemo.plugin;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.base.BLTrustManager;
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

	/***check设备授权**/
	String CHECK_DEVICE_AUTH = "checkDeviceAuth";

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

	/***获取网关下的子设备列表**/
	String GET_GETWAY_SUBDEVLIST = "getGetwaySubDeviceList";

	/***打开设备控制页面**/
	String OPEN_DEV_CRTL_PAGE = "openDeviceControlPage";

	/***打开设备控制页面**/
	String GPS_LOCATION = "gpsLocation";

	/***保存场景内容**/
	String SAVE_SENE_CMDS = "saveSceneCmds";

	/***读取缓存数据**/
	String GET_PRESET_DATA = "getPresetData";

	/***获取验证到手机或者邮箱**/
	String ACCOUNT_SEND_VCODE = "accountSendVCode";

	/***打开另外一个HTML页面**/
	String OPEN_URL = "openUrl";

	/***删除家庭中的设备列表**/
	String DELETE_FAMILY_DEVICE_LIST = "deleteFamilyDeviceList";

	/***云端数据服务接口操作**/
	String CLOUD_SERVICE = "cloudServices";

	/***添加设备到家庭**/
	String ADD_DEVICE_TO_FAMILY = "addDeviceToFamily";

	/***打开设备属性页面**/
	String OPEN_DEVICE_PROPERTY_PAGE = "openDevicePropertyPage";

	/***添加设备到APP SDK初始化**/
	String ADD_DEVICE_TO_NETWORK_INIT = "addDeviceToNetworkInit";

	/***获取设备所接的负载类型**/
	String GET_DEVICE_LOAD_INGO = "getDeviceLoadInfo";

	/***设置设备所接的负载类型**/
	String SET_DEVICE_LOAD_INGO = "setDeviceLoadInfo";

	/***H5数据上报**/
	String H5_DATA_UPLOAD = "h5DataUpload";

	/**打开添加网关下子设备产品分类页面**/
	String OPEN_GATEWAY_SUB_PRODUCT_CATEGORY_LIST_PAGE = "openGatewaySubProductCategoryListPage";

	/***手机振动**/
	String PHONE_VIBRATE = "phoneVibrate";

	/***打开客服/技术支持页面**/
	String OPEN_SUPPORT_PAGE = "openSupportPage";

	/***上传文件**/
	String RESOURCE_UPLOADE = "resourceUpload";


	String ONECONTROLLAMPSCENE = "OneControlLampScene";

	class HttpRequestTask extends AsyncTask<String, Void, String> {
		private CallbackContext callbackContext;
		private Context context;

		public HttpRequestTask(Context context, CallbackContext callbackContext){
			this.context = context;
			this.callbackContext = callbackContext;
		}

		@Override
		protected String doInBackground(String... params) {
			String cmdJsonStr = params[0];
			try {
				if(!TextUtils.isEmpty(cmdJsonStr)){
					BLLog.d(TAG, cmdJsonStr);

					JSONObject jsonObject = new JSONObject(cmdJsonStr);
					String method = jsonObject.optString("method");
					String url = jsonObject.optString("url");
					JSONObject headerJson = jsonObject.optJSONObject("headerJson");
					JSONArray bodys = jsonObject.optJSONArray("bodys");

					BLLog.i(TAG, "method:" + method);
					BLLog.i(TAG, "url:" + url);
					BLLog.i(TAG, "headerJson:" + headerJson);

					if(url != null && method != null && (method.equals("get") || method.equals("post"))){
						//获取头部信息
						HashMap headMap = null;
						if(headerJson != null){
							headMap = new HashMap();
							Iterator<String> keyIterator = headerJson.keys();
							while (keyIterator.hasNext()) {
								String key = keyIterator.next();
								headMap.put(key, headerJson.opt(key));
							}
						}

						//获取body数据
						byte[] bodysData = null;
						if(bodys != null){
							bodysData = new byte[bodys.length()];
							for (int i = 0; i < bodys.length(); i++) {
								bodysData[i] = (byte) bodys.getInt(i);
							}
						}

						if (method.equals("get")) {
							return BLBaseHttpAccessor.get(url, null, headMap, 10 * 1000, new BLTrustManager());
						} else {
							return BLBaseHttpAccessor.post(url, headMap, bodysData, 10 * 1000, new BLTrustManager());
						}
					}
				}
			}catch (Exception e){
				BLLog.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(callbackContext != null) callbackContext.success(result);
		}
	}

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

					int localTimeout = jsonObject.optInt("localTimeout", 3000);
					int remoteTimeout = jsonObject.optInt("remoteTimeout", 5000);
					int sendcount=jsonObject.optInt("sendCount", -1);
					configParam = new BLConfigParam();
					if(localTimeout > 0){
						configParam.put(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT, String.valueOf(localTimeout));
					}

					if(remoteTimeout > 0){
						configParam.put(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT, String.valueOf(remoteTimeout));
					}

					if(sendcount>0){
						configParam.put(BLConfigParam.CONTROLLER_SEND_COUNT, String.valueOf(sendcount));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return BLLet.Controller.dnaControl(params[0], params[1], params[2], params[3], configParam);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(callbackContext != null && activity != null && !activity.isFinishing()) callbackContext.success(result);
		}
	}

}
