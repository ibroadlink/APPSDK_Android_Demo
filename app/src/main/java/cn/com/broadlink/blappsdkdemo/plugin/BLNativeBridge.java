package cn.com.broadlink.blappsdkdemo.plugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.base.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.device.DevGatewayManageActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSEndpointInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.BLUserInfoUnits;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.data.auth.AuthQueryDevInfoListResult;
import cn.com.broadlink.blappsdkdemo.data.auth.DevAuthInfo;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.presenter.BLFamilyTimestampPresenter;
import cn.com.broadlink.blappsdkdemo.presenter.BLVirtualDevPresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;

public class BLNativeBridge extends CordovaPlugin implements BLPluginInterfacer {
    private DeviceH5Activity mBaseActivity;
    private CallbackContext mNotificationCallbackContext;
    private String mCachedRoomId;

    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        mBaseActivity = (DeviceH5Activity) cordova.getActivity();

        Log.d(TAG, "action:" + action);
        Log.d(TAG, "jsonArray:" + jsonArray.toString());

        if (!checkFamilyLogin()) {
            callbackContext.success("");
            return true;
        }

        //判断Action是否为空，为空不处理
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
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
                case CLOSE_WEBVIEW:
                    return closeWebViewActivity();
                case DEVICE_AUTH:
                    return toDevAuthActivity(jsonArray, callbackContext);
                case CHECK_DEVICE_AUTH:
                    return queryAuth(jsonArray, callbackContext);
                case GET_USERINFO:
                    return getUserInfo(callbackContext);
                case GET_FAMILYINFO:
                    return getFamilyInfo(callbackContext);
                case GET_DEVICE_LIST:
                    return getDeviceList(callbackContext);
                case GET_DEV_PROFILE:
                    return queryDevProfile(jsonArray, callbackContext);
                case DEVICE_STATUS_QUERY:
                    return queryDevStatus(jsonArray, callbackContext);
                case OPEN_DEV_CRTL_PAGE:
                    return openControlPage(jsonArray, callbackContext);
                case GET_PRESET_DATA:
                    return readPresetData(callbackContext);
                case ACCOUNT_SEND_VCODE:
                    return accountSendVCode(jsonArray, callbackContext);
                case OPEN_URL:
                    return openUrl(jsonArray, callbackContext);
                case CLOUD_SERVICE:
                    return cloudService(jsonArray, callbackContext);
                case ADD_DEVICE_TO_NETWORK_INIT:
                    return addDeviceToNetworkInit(jsonArray, callbackContext);
                case GET_DEVICE_LOAD_INGO:
                    return getDeviceLoadInfo(jsonArray, callbackContext);
                case SET_DEVICE_LOAD_INGO:
                    return setDeviceLoadInfo(jsonArray, callbackContext);
                case PHONE_VIBRATE:
                    return phoneVibrate(callbackContext);
                case GET_GETWAY_SUBDEVLIST:
                    return getSubDeviceList(jsonArray, callbackContext);
                case ADD_DEVICE_TO_FAMILY:
                    return addDeviceToFamily(jsonArray, callbackContext);
                case OPEN_DEVICE_PROPERTY_PAGE:
                    return openDevicePropertyPage(jsonArray, callbackContext);
                case GPS_LOCATION:
                    return gpsLocation(callbackContext);
                case DEVICE_LINKAGE_PARAM_SET:
                    return setDeviceLinkageParam(jsonArray, callbackContext);
                case OPEN_GATEWAY_SUB_PRODUCT_CATEGORY_LIST_PAGE:
                    return openGatewaySubProductCategoryListPage(jsonArray, callbackContext);
            }
        }

        return false;
    }


    //跳转到设备属性页面
    public boolean openGatewaySubProductCategoryListPage(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException{
        if(jsonArray.length() > 0){
            String deviceJsonStr = jsonArray.getString(0);
            JSONObject jsonObject = new JSONObject(deviceJsonStr);
            String did = jsonObject.optString("did");

            if(TextUtils.isEmpty(did)){
                return false;
            }

            //查询本地的设备信息
            BLDNADevice deviceInfo = BLLocalDeviceManager.getInstance().getCachedDevice(did);
            if(deviceInfo == null){
                return false;
            }

//            Intent intent = new Intent();
//            intent.putExtra(BLConstants.INTENT_DEVICE, deviceInfo);
//            intent.putExtra(BLConstants.INTENT_TITLE, mBaseActivity.getString(R.string.add_parts));
//            intent.setClass(mBaseActivity, GatewaySubProductCategoryListActivity.class);
//            mBaseActivity.startActivity(intent);

            BLCommonUtils.toActivity(mBaseActivity, DevGatewayManageActivity.class, deviceInfo);
            return true;
        }

        return false;
    }



    //跳转到设备属性页面
    public boolean setDeviceLinkageParam(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            String deviceJsonStr = jsonArray.getString(0);
            JSONObject jsonObject = new JSONObject(deviceJsonStr);
            String act = jsonObject.optString("act");
            String did = jsonObject.optString("did");
            JSONArray pids = jsonObject.optJSONArray("pids");
            JSONArray protocols = jsonObject.optJSONArray("protocols");

            ArrayList<String> pidList = new ArrayList<>();
            if(pids != null){
                for(int i=0; i<pids.length(); i++){
                    pidList.add(pids.getString(i));
                }
            }

            ArrayList<String> protocolsList = new ArrayList<>();
            if(protocols != null){
                for(int i=0; i<protocols.length(); i++){
                    protocolsList.add(protocols.getString(i));
                }
            }

            if (act.equals("action")) {
                mBaseActivity.openLinkagePage(callbackContext, did, pidList, protocolsList);
                return true;
            } else if(act.equals("trigger")){
                // TODO: 2018/12/3 暂不支持trigger
                return false;
            }else{
                return false;
            }
        }
        return false;
    }
    
    
    private boolean gpsLocation(final CallbackContext callbackContext) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("city", "杭州市");
            jsonObject.put("address", "浙江省杭州市");
            jsonObject.put("longitude", 120.20191);
            jsonObject.put("latitude", 30.183091);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!mBaseActivity.isFinishing()) {
            callbackContext.success(jsonObject.toString());
        }

        return true;
    }

    //跳转到设备属性页面
    public boolean openDevicePropertyPage(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            mBaseActivity.openAppPropertyPage(callbackContext);
            return true;
        }

        return false;
    }

    public boolean addDeviceToFamily(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            String deviceJsonStr = jsonArray.getString(0);
            String productJsonStr = jsonArray.getString(1);


            BLDNADevice device = parseDeviceInfo(deviceJsonStr);
            ProductInfoResult.ProductDninfo productDninfo = JSON.parseObject(productJsonStr, ProductInfoResult.ProductDninfo.class);
            if (TextUtils.isEmpty(device.getDid()) || (TextUtils.isEmpty(device.getpDid()) && TextUtils.isEmpty(device.getKey()))) {
                return false;
            }

            if (TextUtils.isEmpty(productDninfo.getPid()) || TextUtils.isEmpty(productDninfo.getName())) {
                return false;
            }

            new AddEndpointTask(callbackContext, device, productDninfo).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            return true;
        }

        return false;
    }

    //获取子设备列表
    private boolean getSubDeviceList(JSONArray paramJsonArray, CallbackContext callbackContext) throws JSONException {

        String gateWayDid = null;
        if (paramJsonArray != null && paramJsonArray.length() >= 1) {
            String didJSONStr = paramJsonArray.getString(0);
            JSONObject jsonObject = new JSONObject(didJSONStr);
            String did = jsonObject.optString("did");
            if (!TextUtils.isEmpty(did)) {
                gateWayDid = did;
            }
        }

        //强转为设备的主页面Activity
        if (TextUtils.isEmpty(gateWayDid)) {
            gateWayDid = ((DeviceH5Activity) cordova.getActivity()).mBlDeviceInfo.getDid();
        }

        new GetAddedSubDevsTask(callbackContext).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, gateWayDid);
        return true;
    }


    private boolean phoneVibrate(CallbackContext callbackContext) {
        Vibrator vibrator = (Vibrator) cordova.getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
        callbackContext.success();
        return true;
    }

    //获取设备的负载类型
    public boolean getDeviceLoadInfo(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
        callbackContext.success(activity.mBlDeviceInfo.getExtend());
        return true;
    }

    //获取设备的负载类型
    public boolean setDeviceLoadInfo(JSONArray jsonArray, final CallbackContext callbackContext) throws JSONException {
        return true;
    }

    //设备add app sdk 初始化
    private boolean addDeviceToNetworkInit(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            String deviceJsonStr = jsonArray.getString(0);

            BLDNADevice device = parseDeviceInfo(deviceJsonStr);
            BLLet.Controller.addDevice(device);

            JSONObject initResult = new JSONObject();
            initResult.put("status", 0);
            callbackContext.success(initResult.toString());

            return true;
        }

        return false;
    }

    /***
     * 解析设备信息
     * @param deviceJsonStr
     * @return
     */
    private BLDNADevice parseDeviceInfo(String deviceJsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(deviceJsonStr);

        BLDNADevice device = new BLDNADevice();
        device.setpDid(jsonObject.optString("pDid"));
        device.setDid(jsonObject.optString("did"));
        device.setPid(jsonObject.optString("pid"));
        device.setName(jsonObject.optString("name"));
        device.setId(jsonObject.optInt("id"));
        device.setKey(jsonObject.optString("key"));

        String mac = device.getDid().substring(20, 32);
        device.setMac(BLCommonUtils.formatMac(mac));
        device.setType((int) BLCommonUtils.hexto10(device.getPid().substring(24, 28)));
        return device;
    }

    private boolean cloudService(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {

        String cmdJsonStr = jsonArray.getString(0);
        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();

        new DataServiceTask(activity, callbackContext).execute(cmdJsonStr);

        return true;
    }

    private boolean openUrl(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            JSONObject jsonObject = new JSONObject(jsonArray.getString(0));
            String openUrl = jsonObject.optString("url");
            String platform = jsonObject.optString("platform");

            if (openUrl != null) {
                if (platform == null || platform.equals("app")) {
                    Intent intent = new Intent();
                    if (openUrl.startsWith("http")) {
                        intent.setClass(mBaseActivity, DeviceH5Activity.class);
                    } else {
                        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
                        openUrl = "file:///" + BLStorageUtils.languageFolder(activity.mBlDeviceInfo.getPid()) + File.separator + openUrl;
                        intent.setClass(mBaseActivity, DeviceH5Activity.class);
                        intent.putExtra(BLConstants.INTENT_DEVICE, activity.mBlDeviceInfo);
                    }
                    intent.putExtra(BLConstants.INTENT_URL, openUrl);
                    mBaseActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(openUrl);
                    intent.setData(content_url);
                    mBaseActivity.startActivity(intent);
                }

                callbackContext.success();
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean accountSendVCode(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            JSONObject jsonObject = new JSONObject(jsonArray.getString(0));
            String account = jsonObject.optString("account");
            String countrycode = jsonObject.optString("countrycode");
            if (account != null) {
                new AccountSendVCodeTask(callbackContext).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, account, countrycode);
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean openControlPage(JSONArray jsonArray, CallbackContext callbackContext) {
        if (jsonArray.length() > 0) {
            String devDid = null, pDid = null, param = null;
            try {
                String didJSONStr = jsonArray.getString(0);
                JSONObject jsonObject = new JSONObject(didJSONStr);
                String did = jsonObject.optString("did");
                String sdid = jsonObject.optString("sdid");
                param = jsonObject.optString("extend");
                String data = jsonObject.optString("data");

                //如果data数据不为空 将数据保存文件，供下次H5调用,数据大小约1M
                writeH5CacheFileData(data);

                devDid = TextUtils.isEmpty(sdid) ? did : sdid;
                pDid = TextUtils.isEmpty(sdid) ? null : TextUtils.isEmpty(did) ? mBaseActivity.mBlDeviceInfo.getDid() : did;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (param != null) {
                BLLog.e(TAG, "extend value:" + param);
            }

            BLDNADevice deviceInfo = null;
            if (!TextUtils.isEmpty(devDid)) {
                deviceInfo = BLLocalDeviceManager.getInstance().getCachedDevice(devDid);
                deviceInfo.setpDid(pDid);
            }

            if (deviceInfo != null) {
                Intent intent = new Intent();
                intent.setClass(mBaseActivity, DeviceH5Activity.class);
                intent.putExtra(BLConstants.INTENT_DEVICE, deviceInfo);
                intent.putExtra(BLConstants.INTENT_PARAM, param);
                mBaseActivity.startActivity(intent);
            } else {
                BLLog.e(TAG, "DeviceInfo is Null");
                mBaseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BLCommonUtils.toastShow(mBaseActivity, R.string.str_main_free_device);
                    }
                });
            }
        }
        return true;
    }

    private void writeH5CacheFileData(String data) {
        if (!TextUtils.isEmpty(data)) {
            String path = BLStorageUtils.CACHE_PATH + File.separator + "webwiewCache.data";
            BLFileUtils.saveStringToFile(data, path);
        }
    }

    private String readH5CacheFileData() {
        String path = BLStorageUtils.CACHE_PATH + File.separator + "webwiewCache.data";
        return BLFileUtils.readTextFileContent(path);
    }

    private boolean readPresetData(CallbackContext callbackContext) {
        String data = readH5CacheFileData();
        JSONObject jsonObject = new JSONObject();
        try {
            if (data != null) jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.success(jsonObject.toString());
        return true;
    }

    private boolean queryDevStatus(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        String cmdJsonStr = jsonArray.getString(0);
        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();

        new QueryDeviceStatusTask(activity, activity.mBlDeviceInfo, callbackContext).execute(cmdJsonStr);
        return true;
    }


    //获取设备的profile
    private boolean queryDevProfile(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (jsonArray.length() > 0) {
            String pid = null;
            String didJSONStr = jsonArray.getString(0);
            JSONObject jsonObject = new JSONObject(didJSONStr);
            pid = jsonObject.optString("pid");

            if (pid != null) {
                BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(pid);
                JSONObject resultJsonObject = new JSONObject();
                resultJsonObject.put("profile", devProfileResult);
                callbackContext.success(resultJsonObject.toString());
            }
        }
        return true;
    }

    //获取设备列表
    private boolean getDeviceList(CallbackContext callbackContext) {
        try {
            List<BLDNADevice> devList = BLLocalDeviceManager.getInstance().getDevicesAddInSDK();
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for (BLDNADevice deviceInfo : devList) {
                JSONObject devObject = new JSONObject();
                devObject.put("did", deviceInfo.getDid());
                devObject.put("pdid", deviceInfo.getpDid());
                devObject.put("mac", deviceInfo.getMac());
                devObject.put("pid", deviceInfo.getPid());
                devObject.put("name", deviceInfo.getName());
                devObject.put("lock", deviceInfo.isLock());
                devObject.put("password", deviceInfo.getPassword());
                int deviceStatus = BLLet.Controller.queryDeviceState(deviceInfo.getDid());
                devObject.put("deviceStatus", deviceStatus);
                jsonArray.put(devObject);
            }

            jsonObject.put("deviceList", jsonArray);
            callbackContext.success(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    //获取用户信息
    private boolean getUserInfo(CallbackContext callbackContext) {
        if (BLLocalFamilyManager.getInstance().getCurrentFamilyInfo() == null) {
            BLToastUtils.show("Please login first!");
            callbackContext.success("");
            return true;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", BLApplication.mBLUserInfoUnits.getUserid());
            jsonObject.put("nickName", BLApplication.mBLUserInfoUnits.getNickname());
            jsonObject.put("userName", BLApplication.mBLUserInfoUnits.getPhone());
            jsonObject.put("userIcon", BLApplication.mBLUserInfoUnits.getIconpath());
            jsonObject.put("loginSession", BLApplication.mBLUserInfoUnits.getLoginsession());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callbackContext.success(jsonObject.toString());
        return true;
    }

    private boolean checkFamilyLogin() {
        if (BLLocalFamilyManager.getInstance().getCurrentFamilyInfo() == null || BLApplication.mBLUserInfoUnits.getUserid() == null) {
            BLAlert.showAlert(mBaseActivity, null, "Please login and select a family first!", new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    closeWebViewActivity();
                }
            });
            return false;
        }
        return true;
    }

    //获取家庭信息
    private boolean getFamilyInfo(final CallbackContext callbackContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("familyId", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());
            jsonObject.put("familyName", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getName());
            jsonObject.put("familyIcon", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getIconpath());
            
//            jsonObject.put("countryCode", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getCountryCode());
//            jsonObject.put("provinceCode", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getProvinceCode());
//            jsonObject.put("cityCode", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getCityCode());
            jsonObject.put("countryCode", "1");
            jsonObject.put("provinceCode", "33");
            jsonObject.put("cityCode", "6");
            
            jsonObject.put("isAdmin", BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getMaster().equalsIgnoreCase(BLApplication.mBLUserInfoUnits.getUserid()));
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callbackContext.success(jsonObject.toString());
        BLLog.d(TAG, jsonObject.toString());
        return true;
    }

    //设备认证
    private boolean toDevAuthActivity(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
        if (jsonArray.length() > 0) {
            activity.toDevAuthActivity(jsonArray.getString(0), callbackContext);
        }
        return true;
    }

    private boolean closeWebViewActivity() {
        clearH5CacheFileData();
        cordova.getActivity().finish();
        return true;
    }

    private void clearH5CacheFileData() {
        String path = BLStorageUtils.CACHE_PATH + File.separator + "webwiewCache.data";
        File file = new File(path);
        if (new File(path).exists()) {
            file.delete();
        }
    }

    //check设备授权
    private boolean queryAuth(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
        if (jsonArray.length() > 0) {
            JSONObject jsonObject = new JSONObject(jsonArray.getString(0));

            String ticket = jsonObject.optString("ticket");
            new QueryAuthDevListTask(activity, ticket, callbackContext).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
        }
        return true;
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
    public boolean getWifiInfo(CallbackContext callbackContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ssid", BLCommonUtils.getWIFISSID(cordova.getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callbackContext.success(jsonObject.toString());
        return true;
    }

    /**
     * 保存当前推送JS的回调
     **/
    private boolean saveNotification(JSONArray jsonArray, CallbackContext callbackContext) {
        mNotificationCallbackContext = callbackContext;
        return true;
    }

    /**
     * 获取设备信息
     *
     * @param callbackContext 回调
     * @return
     */
    private boolean deviceInfo(CallbackContext callbackContext) {

        DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
        if (activity != null && activity.mBlDeviceInfo != null) {
            BLDNADevice deviceInfo = activity.mBlDeviceInfo;
            String did = TextUtils.isEmpty(deviceInfo.getpDid()) ? deviceInfo.getDid() : deviceInfo.getpDid();
            String sdid = TextUtils.isEmpty(deviceInfo.getpDid()) ? null : deviceInfo.getDid();

            BLJSDeviceInfo startUpInfo = new BLJSDeviceInfo();
            startUpInfo.setDeviceStatus(BLLet.Controller.queryDeviceState(did));
            startUpInfo.setDeviceID(did);
            startUpInfo.setSubDeviceID(sdid);
            startUpInfo.setProductID(deviceInfo.getPid());
            startUpInfo.setDeviceName(deviceInfo.getName());
            startUpInfo.setDeviceMac(deviceInfo.getMac());
            startUpInfo.getNetworkStatus().setStatus(BLCommonUtils.checkNetwork(activity) ? BLPluginInterfacer.NETWORK_AVAILAVLE : BLPluginInterfacer.NETWORK_UNAVAILAVLE);


            BLUserInfoUnits blUserInfoUnits = BLApplication.mBLUserInfoUnits;
            String username = "";
            if (blUserInfoUnits.getPhone() != null) {
                username = blUserInfoUnits.getPhone();
            } else if (blUserInfoUnits.getEmail() != null) {
                username = blUserInfoUnits.getEmail();
            }

            startUpInfo.getUser().setName(username);
            BLLog.d(TAG, "deviceInfo: " + JSON.toJSONString(startUpInfo));
            callbackContext.success(JSON.toJSONString(startUpInfo));
        }
        return true;
    }

    /**
     * 设备控制
     *
     * @param jsonArray
     * @param callbackContext
     * @return
     */
    private boolean deviceControl(JSONArray jsonArray, CallbackContext callbackContext) {
        try {
            String deviceMac = jsonArray.getString(0);
            String subDeviceID = jsonArray.getString(1);
            String cmd = jsonArray.getString(2);
            String method = jsonArray.getString(3);

            String extendStr = null;
            if (jsonArray.length() >= 5) {
                extendStr = jsonArray.getString(4);
            }

            //判断mac地址是否为空，以及method是否为空
            if (TextUtils.isEmpty(deviceMac) || TextUtils.isEmpty(method)) {
                BLJsBaseResult pluginBaseResult = new BLJsBaseResult();
                pluginBaseResult.setCode(ERRCODE_PARAM);
                callbackContext.error(JSON.toJSONString(pluginBaseResult));
            } else {
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
     * @param param JsonStr
     */
    public void pushJSNotification(String param) {
        if (mNotificationCallbackContext != null) {
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

    class QueryAuthDevListTask extends AsyncTask<Void, Void, String> {
        private CallbackContext callbackContext;
        private String ticket;
        private DeviceH5Activity activity;

        public QueryAuthDevListTask(DeviceH5Activity activity, String ticket, CallbackContext callbackContext) {
            this.activity = activity;
            this.ticket = ticket;
            this.callbackContext = callbackContext;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getTimestamp(activity);
            if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {

                byte[] loginBytes = BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(timestampResult.getKey()), "{}");

                UserHeadParam baseHeadParam = new UserHeadParam(timestampResult.getTimestamp(),
						BLCommonTools.md5("{}" + BLConstants.STR_BODY_ENCRYPT + timestampResult.getTimestamp() + BLApplication.mBLUserInfoUnits.getUserid()));
                baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
                baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
                baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

                BLHttpPostAccessor httpAccessor = new BLHttpPostAccessor(activity);

                AuthQueryDevInfoListResult result = httpAccessor.execute(BLApiUrlConstants.AppManager.DEVICE_AUTH_LIST(), baseHeadParam, loginBytes,
						AuthQueryDevInfoListResult.class);
                if (result != null && result.succeed() && result.getData() != null) {

                    try {
                        JSONObject jObject = null;
                        for (DevAuthInfo devAuthInfo : result.getData()) {
                            if (devAuthInfo.getDid() != null && devAuthInfo.getDid().equals(activity.mBlDeviceInfo.getDid())) {
                                jObject = new JSONObject();
                                jObject.put("did", devAuthInfo.getDid());
                                jObject.put("ticket", devAuthInfo.getTicket());

                                if (TextUtils.isEmpty(devAuthInfo.getTicket())) {
                                    if (TextUtils.isEmpty(ticket)) {
                                        jObject.put("status", 0);
                                    } else {
                                        jObject.put("status", 2);
                                    }
                                } else {
                                    if (TextUtils.isEmpty(ticket) || !ticket.equals(devAuthInfo.getTicket())) {
                                        jObject.put("status", 1);
                                    } else {
                                        jObject.put("status", 0);
                                    }
                                }
                                return jObject.toString();
                            }
                        }

                        if (jObject == null) {//云端没查到此did相关授权信息
                            jObject = new JSONObject();
                            jObject.put("did", activity.mBlDeviceInfo.getDid());
                            jObject.put("ticket", "");
                            if (TextUtils.isEmpty(ticket)) {
                                jObject.put("status", 0);
                            } else {
                                jObject.put("status", 2);
                            }
                            return jObject.toString();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            callbackContext.success(result);
        }
    }

    class QueryDeviceStatusTask extends AsyncTask<String, Void, String> {
        private Context context;

        private CallbackContext callbackContext;

        private BLDNADevice deviceInfo;

        public QueryDeviceStatusTask(Context context, BLDNADevice deviceInfo, CallbackContext callbackContext) {
            this.context = context;
            this.deviceInfo = deviceInfo;
            this.callbackContext = callbackContext;
        }

        @Override
        protected String doInBackground(String... params) {
            String requestStr = null;
            try {
                JSONObject jsonObject = new JSONObject(params[0]);
                requestStr = jsonObject.optString("params");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String licenseid = BLLet.getLicenseId();
            UserHeadParam headParam = new UserHeadParam();
            headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
            headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
            headParam.setLicenseid(licenseid);

            BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(context);
            httpPostAccessor.setToastError(false);

            return httpPostAccessor.execute(String.format(BLApiUrlConstants.QUERY_DEV_HISTORY, deviceInfo.getPid()), headParam, requestStr, String.class);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            callbackContext.success(result);
        }
    }

    class AccountSendVCodeTask extends AsyncTask<String, Void, String> {
        private CallbackContext callbackContext;

        public AccountSendVCodeTask(CallbackContext callbackContext) {
            this.callbackContext = callbackContext;
        }


        @Override
        protected String doInBackground(String... params) {
            String countrycode = null;
            if (params.length > 1) {
                countrycode = params[1];
            }
            BLBaseResult result = BLAccount.sendFastLoginVCode(params[0], countrycode);
            return JSON.toJSONString(result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            callbackContext.success(result);
        }
    }

    private class GetAddedSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {
        private CallbackContext callbackContext;
        private String gatewayDid;

        public GetAddedSubDevsTask(CallbackContext callbackContext) {
            this.callbackContext = callbackContext;
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {
            final DeviceH5Activity activity = (DeviceH5Activity) cordova.getActivity();
            int index = 0;
            final int QUERY_MAX_COUNT = 5;
            gatewayDid = params[0];
            BLSubDevListResult result = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);

            if (result != null && result.succeed() && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
                int total = result.getData().getTotal();
                int currListSize = result.getData().getList().size();
                if (currListSize < total) {
                    while (true) {
                        index = result.getData().getList().size();
                        BLSubDevListResult queryResult = null;
                        //循环最多查询3次，成功之后跳出循环
                        for (int i = 0; i < 3; i++) {
                            queryResult = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);
                            if (queryResult != null && queryResult.succeed()) {
                                break;
                            }
                        }

                        if (queryResult != null && queryResult.succeed()) {
                            if (queryResult.getData() != null && queryResult.getData().getList() != null && !result.getData().getList().isEmpty()) {
                                result.getData().getList().addAll(queryResult.getData().getList());
                                result.getData().setIndex(queryResult.getData().getIndex());

                                if (result.getData().getList().size() >= total) {
                                    break;
                                }
                            } else {
                                result = queryResult;
                                break;
                            }
                        } else {
                            result = queryResult;
                            break;
                        }
                    }
                }
            }

            if (result != null && result.getData() != null) {
                List<BLDeviceInfo> list = new ArrayList<>();
                try {
                    for (BLDNADevice deviceInfo : result.getData().getList()) {
                        deviceInfo.setpDid(gatewayDid);
                        BLLocalDeviceManager.getInstance().addDeviceIntoSDK(deviceInfo);
                        list.add(new BLDeviceInfo(deviceInfo));
                    }
                    BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(((DeviceH5Activity) cordova.getActivity()).getHelper());
                    blDeviceInfoDao.insertData(list);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                if (blBaseResult != null && blBaseResult.getData() != null) {

                    for (BLDNADevice deviceInfo : blBaseResult.getData().getList()) {
                        JSONObject devObject = new JSONObject();
                        devObject.put("did", deviceInfo.getDid());
                        devObject.put("pDid", deviceInfo.getpDid());
                        devObject.put("pid", deviceInfo.getPid());
                        devObject.put("name", deviceInfo.getName());
                        jsonArray.put(devObject);
                    }
                }
                
                jsonObject.put("deviceList", jsonArray);
                callbackContext.success(jsonObject.toString());
                BLLog.d(TAG,"subList: "+ jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class AddEndpointTask extends AsyncTask<Integer, Void, BLBaseResult> {
        private CallbackContext callbackContext;
        private BLDNADevice device;
        private ProductInfoResult.ProductDninfo productDninfo;

        public AddEndpointTask(CallbackContext callbackContext, BLDNADevice device, ProductInfoResult.ProductDninfo productDninfo) {
            this.callbackContext = callbackContext;
            this.device = device;
            this.productDninfo = productDninfo;
        }

        private BLDNADevice getDeviceInfo() {
            if (device == null) {
                BLVirtualDevPresenter virtualDevPresenter = new BLVirtualDevPresenter(cordova.getActivity());
                BLDNADevice virDev = virtualDevPresenter.create(productDninfo.getName(), mCachedRoomId, productDninfo.getPid(), null);
                if (virDev != null) {
                    device = virDev;
                }
            }
            return device;
        }

        @Override
        protected BLBaseResult doInBackground(Integer... integers) {
            final String mFamilyId = BLLocalFamilyManager.getInstance().getCurrentFamilyId();
            BLSQueryRoomListResult result = BLSFamilyManager.getInstance().queryRoomList(mFamilyId);

            if (TextUtils.isEmpty(mCachedRoomId)) {
                if (result != null && result.succeed() && result.getData() != null && result.getData().getRoomList() != null && result.getData().getRoomList().size() > 0) {
                    mCachedRoomId = result.getData().getRoomList().get(0).getRoomid();
                }
            }

            getDeviceInfo();

            BLSEndpointInfo endpointInfo = new BLSEndpointInfo(device);
            endpointInfo.setFriendlyName("AddByH5-" + device.getName());
            endpointInfo.setRoomId(mCachedRoomId);

            List<BLSEndpointInfo> infos = new ArrayList<>();
            infos.add(endpointInfo);

            final BLBaseResult blBaseResult = BLSFamilyManager.getInstance().addEndpoint(mFamilyId, infos);
            if (blBaseResult != null && blBaseResult.succeed()) {
                try {
                    BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(((DeviceH5Activity) cordova.getActivity()).getHelper());
                    BLDeviceInfo deviceInfo = new BLDeviceInfo(device);
                    List<BLDeviceInfo> list = new ArrayList<>();
                    list.add(deviceInfo);
                    blDeviceInfoDao.insertData(list);
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(device);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return blBaseResult;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            try {
                JSONObject initResult = new JSONObject();
                initResult.put("status", 0);
                callbackContext.success(initResult.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result != null && result.succeed()) {
                BLToastUtils.show("Add device to family success!");
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }
}
