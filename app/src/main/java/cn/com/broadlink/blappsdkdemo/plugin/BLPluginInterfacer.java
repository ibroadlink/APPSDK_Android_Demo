package cn.com.broadlink.blappsdkdemo.plugin;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.base.BLTrustManager;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.data.FilePostParam;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLCloudTimerPrensenter;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLProductPresenter;
import cn.com.broadlink.blappsdkdemo.presenter.BLFamilyTimestampPresenter;
import cn.com.broadlink.blappsdkdemo.presenter.BLIRServicePresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostFileAccessor;
import cn.com.broadlink.sdk.BLLet;


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

	/***设备联动触发条件或者执行指令选择**/
	String DEVICE_LINKAGE_PARAM_SET = "deviceLinkageParamsSet";
	
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
			BLLog.d(TAG, result);
			if(callbackContext != null && activity != null && !activity.isFinishing()) callbackContext.success(result);
		}
	}
	
	class DataServiceTask extends AsyncTask<String, Void, String>{
		//数据中心
		private static final String SERVICE_DATA = "dataservice";
		//云码服务
		private static final String SERVICE_IR = "irservice";
		//私有数据
		private static final String SERVICE_PRIVATE = "privatedataservice";
		//联动服务
		private static final String SERVICE_IFTTT = "iftttservice";
		//资源中心
		private static final String SERVICE_RESOURCE = "resourceservice";
		//产品服务
		private static final String SERVICE_PRODUCT = "productservice";
		//设备/场景云定时
		private static final String SERVICE_TIMER = "timerservice";
		//意见反馈
		private static final String FEEDBACK_TIMER = "feedbackservice";

		private Context context;

		private CallbackContext callbackContext;

		public DataServiceTask(Context context, CallbackContext callbackContext){
			this.context = context;
			this.callbackContext = callbackContext;
		}

		@Override
		protected String doInBackground(String... params) {
			String method = null;
			String serviceName = null;
			String interfaceName = null;
			String httpBody = null;
			String filePath = null;
			try {
				JSONObject jsonObject = new JSONObject(params[0]);
				method = jsonObject.optString("method");
				serviceName = jsonObject.optString("serviceName");
				interfaceName = jsonObject.optString("interfaceName");
				httpBody = jsonObject.optString("httpBody");
				filePath = jsonObject.optString("filePath");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(interfaceName == null){
				return null;
			}

			if(serviceName.equals(SERVICE_DATA)){
				DeviceH5Activity deviceH5Activity = (DeviceH5Activity) context;
				return dataService(deviceH5Activity.mBlDeviceInfo.getPid(), interfaceName, httpBody);
			}else if(serviceName.equals(SERVICE_IR)){
				return irService(method, interfaceName, httpBody, filePath);
			}else if(serviceName.equals(SERVICE_PRIVATE)){
				return privateDataService(interfaceName, httpBody);
			}else if(serviceName.equals(SERVICE_RESOURCE)){
				return resourceService(interfaceName, httpBody);
			}else if(serviceName.equals(SERVICE_PRODUCT)){
				return productService(interfaceName, httpBody);
			}else if(serviceName.equals(SERVICE_TIMER)){
				return timerService(interfaceName, httpBody);
			}else if(serviceName.equals(FEEDBACK_TIMER)){
				return feedbackService(method, interfaceName, httpBody, filePath);
			}else{
				return commonService(interfaceName, httpBody);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			BLLog.d(TAG, "cloudService ret: " + result);
			callbackContext.success(result);
		}

		private String serviceHost(String interfaceName, String url){
			String protocol = BLCommonUtils.urlProtocol(url);
			String host = BLCommonUtils.urlHost(url);
			String ipaddress = BLCommonUtils.hostInetAddress(host);

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("protocol",protocol);
				if(interfaceName.equals("URL_HOST_IP")){
					jsonObject.put("hostIP", ipaddress);
				}else{
					jsonObject.put("hostName", host);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return jsonObject.toString();
		}

		//数据服务
		private String timerService(String interfaceName, String httpBody){
			String url = BLApiUrlConstants.BASE_CLOUD_TIMERURL + interfaceName;
			return BLCloudTimerPrensenter.getInstance().httpRequest(context, url, httpBody, String.class);
		}

		//数据服务
		private String dataService(String pid, String interfaceName, String httpBody){
			String url = String.format((BLApiUrlConstants.BASE_DATASERVICE + interfaceName), pid);

			String licenseid = BLLet.getLicenseId();
			UserHeadParam headParam = new UserHeadParam();
			headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
			headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
			headParam.setLicenseid(licenseid);

			BLHttpPostAccessor httpAccessor = new BLHttpPostAccessor(context);
			httpAccessor.setToastError(false);
			return httpAccessor.execute(url, headParam, httpBody, String.class);
		}

		//数据服务
		private String privateDataService(String interfaceName, String httpBody){
			String url = (BLApiUrlConstants.BASE_CLOUD_TIMERURL) + interfaceName;

			BLHttpPostAccessor httpAccessor = new BLHttpPostAccessor(context);
			httpAccessor.setToastError(false);

			RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getTimestamp(context);
			if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {

				UserHeadParam headParam = new UserHeadParam(timestampResult.getTimestamp(),
						BLCommonTools.md5(httpBody + BLConstants.STR_BODY_ENCRYPT + timestampResult.getTimestamp() + BLApplication.mBLUserInfoUnits.getUserid()));
				headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
				headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
				headParam.setLicenseid(BLLet.getLicenseId());
				headParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());

				byte[] loginBytes = BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(timestampResult.getKey()), httpBody);
				return httpAccessor.execute(url, headParam, loginBytes, String.class);
			}
			return null;
		}

		private String resourceService(String interfaceName, String httpBody){
			String url = BLApiUrlConstants.BASE_RESOURCE_URL + interfaceName;


			BLHttpPostAccessor httpAccessor = new BLHttpPostAccessor(context);
			httpAccessor.setToastError(false);

			RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getTimestamp(context);
			if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {

				UserHeadParam headParam = new UserHeadParam(timestampResult.getTimestamp(),
						BLCommonTools.md5(httpBody + BLConstants.STR_BODY_ENCRYPT + timestampResult.getTimestamp() + BLApplication.mBLUserInfoUnits.getUserid()));
				headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
				headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
				headParam.setLicenseid(BLLet.getLicenseId());
				headParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());

				byte[] loginBytes = BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(timestampResult.getKey()), httpBody);
				return httpAccessor.execute(url, headParam, loginBytes, String.class);
			}
			return null;
		}

		//没有serviceName
		private String commonService(String interfaceName, String httpBody){
			if(interfaceName.equals("URL_HOST_IP") || interfaceName.equals("URL_HOST_NAME")){
				return serviceHost(interfaceName, (BLApiUrlConstants.BASE_APP_MANAGE));
			}else{
				String url = (BLApiUrlConstants.BASE_APP_MANAGE) + (interfaceName.startsWith("/")?"":"/") + interfaceName;
				BLProductPresenter presenter = BLProductPresenter.getInstance();
				return  presenter.httpRequest(context, url, httpBody, String.class);
			}
		}

		//产品中心服务
		private String productService(String interfaceName, String httpBody){
			if(interfaceName.equals("URL_HOST_IP") || interfaceName.equals("URL_HOST_NAME")){
				return serviceHost(interfaceName, (BLApiUrlConstants.BASE_APP_MANAGE));
			}else{
				String url = (BLApiUrlConstants.BASE_APP_MANAGE) + "/ec4/" + interfaceName;
				BLProductPresenter presenter = BLProductPresenter.getInstance();
				return  presenter.httpRequest(context, url, httpBody, String.class);
			}
		}


		//红外码服务
		private String irService(String method, String interfaceName, String httpBody, String filePath){
			if(interfaceName.equals("URL_HOST_IP") || interfaceName.equals("URL_HOST_NAME")){
				return serviceHost(interfaceName, BLApiUrlConstants.CLOUD_NEW_BASE);
			}else{
				BLIRServicePresenter presenter = new BLIRServicePresenter(context);

				if(method != null && method.equals("multipart")){
					return presenter.upLoadFile(interfaceName, filePath);
				}else{
					return presenter.irHttpRequest(interfaceName, httpBody);
				}
			}
		}

		//意见反馈
		private String feedbackService(String method, String interfaceName, String httpBody, String filePath){
			if(interfaceName.equals("URL_HOST_IP") || interfaceName.equals("URL_HOST_NAME")){
				return serviceHost(interfaceName, BLApiUrlConstants.BASE_FEEDBACK_URL);
			}else{
				String url = BLApiUrlConstants.BASE_FEEDBACK_URL  + interfaceName;

				if(method != null && method.equals("multipart")){
					File file = new File(filePath);
					if(!file.exists()) return null;

					FilePostParam postParam = new FilePostParam();
					postParam.setFile(new File(filePath));

					UserHeadParam headParam = new UserHeadParam();
					headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
					headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());

					BLHttpPostFileAccessor httpAccessor = new BLHttpPostFileAccessor(context);
					return httpAccessor.execute(url, headParam, postParam, String.class);
				}else{
					RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getTimestamp(context);
					if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {

						UserHeadParam baseHeadParam = new UserHeadParam(timestampResult.getTimestamp(),
								BLCommonTools.md5(httpBody + BLConstants.STR_BODY_ENCRYPT + timestampResult.getTimestamp() + BLApplication.mBLUserInfoUnits.getUserid()));
						baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
						baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
						baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

						BLHttpPostAccessor httpAccessor = new BLHttpPostAccessor(context);

						return httpAccessor.execute(url, baseHeadParam, httpBody, String.class);
					}
				}
			}

			return null;
		}

	}
}
