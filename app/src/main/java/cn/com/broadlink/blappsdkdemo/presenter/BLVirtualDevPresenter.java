package cn.com.broadlink.blappsdkdemo.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.data.VirtualDevApplyParam;
import cn.com.broadlink.blappsdkdemo.data.VirtualDevApplyResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * 虚拟设备信息申请
 * Created by YeJin on 2016/9/10.
 */
public class BLVirtualDevPresenter {

    private Context mContext;

    public BLVirtualDevPresenter(Context context){
        mContext = context;
    }

    public BLDNADevice create(String devName, String roomid, String pid, Map<String, Object> extend){
        BLDNADevice deviceInfo = null;
        VirtualDevApplyParam virtualDevApplyParam = new VirtualDevApplyParam();
        virtualDevApplyParam.setCompanyid(BLLet.getCompanyid());

        VirtualDevApplyParam.ApplyInfo applyInfo = new VirtualDevApplyParam.ApplyInfo();
        applyInfo.setPid(pid);
        applyInfo.setUniquesn(roomid);
        virtualDevApplyParam.getDevlist().add(applyInfo);

        BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);
        UserHeadParam headParam = new UserHeadParam();
        headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
        headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());

        VirtualDevApplyResult result = httpPostAccessor.execute(BLApiUrlConstants.AppManager.APPLY_VIRTUAL_DEV(),
                headParam, JSON.toJSONString(virtualDevApplyParam), VirtualDevApplyResult.class);
        if(result != null && result.isSuccess() && result.getDevlist().size() > 0){
            VirtualDevApplyResult.ApplyDevInfo applyDevInfo = result.getDevlist().get(0);

            deviceInfo = new BLDNADevice();
            deviceInfo.setName(devName);
            deviceInfo.setPid(applyDevInfo.getPid());
            deviceInfo.setMac(BLCommonUtils.formatMac(applyDevInfo.getDid()));
            deviceInfo.setDid("00000000000000000000" + applyDevInfo.getDid());
            deviceInfo.setType((int) pidToDevType(applyDevInfo.getPid()));
            deviceInfo.setKey(TextUtils.isEmpty(applyDevInfo.getToken()) ? getkey() : applyDevInfo.getToken());
            deviceInfo.setExtend(getExtend(extend));
        }
        return deviceInfo;
    }
    
    private String getExtend(Map<String, Object> extend) {
        JSONObject jsonObject = new JSONObject();

        if(extend == null){
            extend = new HashMap<>();
        }
        extend.put("protocol", Integer.parseInt("8"));

        if (extend != null && extend.size() > 0) {
            try {
                for (String item : extend.keySet()) {
                    jsonObject.put(item, extend.get(item));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return jsonObject.toString();
    }
    
    private String getkey(){
        byte[] keys = new byte[16];
        return BLCommonTools.bytes2HexString(keys);
    }

    private long pidToDevType(String pid){
        return BLCommonUtils.hexto10(pid.substring(24, 28));
    }
}
