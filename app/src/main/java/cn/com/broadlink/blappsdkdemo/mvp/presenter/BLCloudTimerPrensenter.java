package cn.com.broadlink.blappsdkdemo.mvp.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.mvp.model.BLCloudTimerModel;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.sdk.BLLet;

/**
 * File description
 *
 * @author YeJing
 * @data 2018/1/12
 */

public class BLCloudTimerPrensenter implements BLCloudTimerModel {
	private static BLCloudTimerPrensenter mBLCloudTimerPrensenter;

	private BLCloudTimerPrensenter(){}

	public static BLCloudTimerPrensenter getInstance(){
		synchronized (BLCloudTimerPrensenter.class){
			if(mBLCloudTimerPrensenter == null){
				mBLCloudTimerPrensenter = new BLCloudTimerPrensenter();
			}
		}
		return mBLCloudTimerPrensenter;
	}

	@Override
	public <T> T httpRequest(Context context, String url, String bodyStr, Class<T> clazz) {
		UserHeadParam baseHeadParam = new UserHeadParam();
		baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setLanguage(BLCommonUtils.getLanguage());
		baseHeadParam.setLicenseid(BLLet.getLicenseId());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());

		baseHeadParam.setIdentity(BLCommonUtils.Base64(JSON.toJSONString(baseHeadParam).getBytes()));

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(context);
		return httpPostAccessor.execute(url, baseHeadParam, bodyStr,clazz);
	}
}
