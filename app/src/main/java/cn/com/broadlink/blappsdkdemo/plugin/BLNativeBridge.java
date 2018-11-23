package cn.com.broadlink.blappsdkdemo.plugin;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.activity.Device.WebControlActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLUserInfoUnits;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class BLNativeBridge extends CordovaPlugin implements BLPluginInterfacer{

	private CallbackContext mNotificationCallbackContext;
	
    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "action:" + action);
    	//判断Action是否为空，为空不处理
    	if(!TextUtils.isEmpty(action)){
			switch (action){
				case DEVICEINO:
					return deviceInfo(callbackContext);
				case NOTIFICATION:
					return saveNotification(jsonArray, callbackContext);
				case DNA_CONTROL:
					return deviceControl(jsonArray, callbackContext);
				case GET_WIFI_INFO:
					return getWifiInfo(callbackContext);
				case HTTP_REQUERT:
					return httpRequest(jsonArray, callbackContext);
			}
    	}

		return false;
    }

	@Override
	public Boolean shouldAllowNavigation(String url) {
		return true;
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		return true;
	}

	//获取WIFI信息
	public boolean getWifiInfo(CallbackContext callbackContext){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("ssid", BLCommonUtils.getWIFISSID(cordova.getActivity()));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		callbackContext.success(jsonObject.toString());
		return true;
	}

    /**保存当前推送JS的回调**/
    private boolean saveNotification(JSONArray jsonArray, CallbackContext callbackContext){
    	mNotificationCallbackContext = callbackContext;
    	return true;
    }
  
    /**
     * 获取设备信息
     * 
     * @param callbackContext
     * 			回调
     * @return
     */
    private boolean deviceInfo(CallbackContext callbackContext){

		WebControlActivity activity = (WebControlActivity) cordova.getActivity();
    	if(activity != null && activity.mDNADevice != null){
			BLDNADevice deviceInfo = activity.mDNADevice;
			String did = TextUtils.isEmpty(deviceInfo.getpDid()) ? deviceInfo.getDid() : deviceInfo.getpDid();
			String sdid = TextUtils.isEmpty(deviceInfo.getpDid()) ? null : deviceInfo.getDid();

    		BLJSDeviceInfo startUpInfo = new BLJSDeviceInfo();
    		startUpInfo.setDeviceStatus(BLLet.Controller.queryDeviceState(did));
			startUpInfo.setDeviceID(did);
			startUpInfo.setSubDeviceID(sdid);
			startUpInfo.setProductID(deviceInfo.getPid());
			startUpInfo.setDeviceName(deviceInfo.getName());
		    startUpInfo.setDeviceMac(deviceInfo.getMac());
    		startUpInfo.getNetworkStatus().setStatus(BLCommonUtils.checkNetwork(activity)
					? BLPluginInterfacer.NETWORK_AVAILAVLE : BLPluginInterfacer.NETWORK_UNAVAILAVLE);


			BLUserInfoUnits blUserInfoUnits = BLApplcation.mBLUserInfoUnits;
			String username = "";
			if (blUserInfoUnits.getPhone() != null) {
				username = blUserInfoUnits.getPhone();
			} else if (blUserInfoUnits.getEmail() != null) {
				username = blUserInfoUnits.getEmail();
			}

    		startUpInfo.getUser().setName(username);
    		callbackContext.success(JSON.toJSONString(startUpInfo));
    	}
		return true;
    }
    
    /**
     * 设备控制
     * @param jsonArray
     * @param callbackContext
     * @return
     */
    private boolean deviceControl(JSONArray jsonArray, CallbackContext callbackContext){
    	try {
			String deviceMac = jsonArray.getString(0);
		 	String subDeviceID = jsonArray.getString(1);
	    	String cmd = jsonArray.getString(2);
	    	String method = jsonArray.getString(3);

			String extendStr = null;
			if(jsonArray.length() >= 5){
				extendStr = jsonArray.getString(4);
			}

	    	//判断mac地址是否为空，以及method是否为空
	    	if(TextUtils.isEmpty(deviceMac) || TextUtils.isEmpty(method)){
				BLJsBaseResult pluginBaseResult = new BLJsBaseResult();
				pluginBaseResult.setCode(ERRCODE_PARAM);
				callbackContext.error(JSON.toJSONString(pluginBaseResult));
	    	}else{
				new ControlTask(cordova.getActivity(), extendStr, callbackContext).execute(deviceMac, subDeviceID, cmd, method);
	    	}
		} catch (JSONException e) {
			e.printStackTrace();

			BLJsBaseResult pluginBaseResult = new BLJsBaseResult();
			pluginBaseResult.setCode(ERRCODE_PARAM);
			callbackContext.error(JSON.toJSONString(pluginBaseResult));
		}
    	return true;
    }
    
    /**
     * 推送消息给JS
     * 
     * @param param
     *			JsonStr
     */
    public void pushJSNotification(String param){
    	if(mNotificationCallbackContext != null){
    		CallbackContext ctx = mNotificationCallbackContext;
    		mNotificationCallbackContext = null;
    		ctx.success(param);
    	}
    }


	@Override
	public Boolean shouldAllowBridgeAccess(String url) {
		return true;
	}

	private boolean httpRequest(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
		String cmdJsonStr = jsonArray.getString(0);
		new HttpRequestTask(cordova.getActivity(), callbackContext).execute(cmdJsonStr);
		return true;
	}
}
