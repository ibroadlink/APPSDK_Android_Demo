package cn.com.broadlink.blappsdkdemo.activity.family.result;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class BLSEndpointInfo {

    private String endpointId;
    private String friendlyName;
    private String mac;
    private String gatewayId;
    private String productId;
    private String icon;
    private String roomId;
    private int order;
    private String userId;
    private String cookie;
    private String irData;
    private String vGroup;
    private String extend;

    public BLSEndpointInfo() {
        super();
    }

    public BLSEndpointInfo(BLDNADevice bldnaDevice) {
        super();
        if (bldnaDevice != null) {
            this.mac = bldnaDevice.getMac();
            this.endpointId = bldnaDevice.getDid();
            this.productId = bldnaDevice.getPid();
            this.gatewayId = bldnaDevice.getpDid();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", bldnaDevice.getPassword());
                jsonObject.put("devtype", bldnaDevice.getType());
                jsonObject.put("devname", bldnaDevice.getName());
                jsonObject.put("lock", bldnaDevice.isLock());
                jsonObject.put("aeskey", bldnaDevice.getKey());
                jsonObject.put("terminalid", bldnaDevice.getId());
                jsonObject.put("extend", bldnaDevice.getExtend());

                String cookie = jsonObject.toString();
                this.cookie = Base64.encodeToString(cookie.getBytes(), Base64.NO_WRAP);

            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        }
    }

    public BLDNADevice toDnadeviceInfo() {

        BLDNADevice device = new BLDNADevice();
        device.setDid(this.endpointId);
        device.setMac(this.mac);
        device.setPid(this.productId);
        device.setpDid(this.gatewayId);

        if (this.cookie != null) {
             byte[] cookieBytes = Base64.decode(this.cookie, Base64.NO_WRAP);
             String cookie = new String(cookieBytes);
             try {
                 JSONObject jsonObject = new JSONObject(cookie);
                 device.setPassword(jsonObject.optLong("password"));
                 device.setType(jsonObject.optInt("devtype"));
                 device.setName(jsonObject.optString("devname", null));
                 device.setKey(jsonObject.optString("aeskey", null));
                 device.setId(jsonObject.optInt("terminalid"));
                 device.setExtend(jsonObject.optString("extend", null));

             } catch (JSONException e) {
                 BLCommonTools.handleError(e);
             }
        }

        return device;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getvGroup() {
        return vGroup;
    }

    public void setvGroup(String vGroup) {
        this.vGroup = vGroup;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getIrData() {
        return irData;
    }

    public void setIrData(String irData) {
        this.irData = irData;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
