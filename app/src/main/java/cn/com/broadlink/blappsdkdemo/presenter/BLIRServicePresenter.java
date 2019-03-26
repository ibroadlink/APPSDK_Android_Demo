package cn.com.broadlink.blappsdkdemo.presenter;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.data.FilePostParam;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.data.UserBrandInfoResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostFileAccessor;
import cn.com.broadlink.sdk.BLLet;

/**
 *
 * 云端红外码接口系统
 * File description
 *
 * @author YeJing
 * @data 2017/10/9
 */

public class BLIRServicePresenter {
	
	private Context mContext;

	public BLIRServicePresenter(Context context){
		this.mContext = context;
	}


	public UserBrandInfoResult queryUserBrand(List<String> pids, String searchKey, String userid){
		try {
			RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getTimestamp(mContext);
			if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {
				JSONObject body = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				for(String pid : pids){
					jsonArray.put(pid);
				}

				if(TextUtils.isEmpty(searchKey)){
					body.put("brand","");
				}else{
					body.put("brand",searchKey);
				}
				body.put("pidArray",jsonArray);

				body.put("userid",userid);
				String json = body.toString();
				UserHeadParam baseHeadParam = new UserHeadParam();
				baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
				baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

				BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);
				return httpPostAccessor.execute(BLApiUrlConstants.QUERY_RM_USER_BRAND(),baseHeadParam,json, UserBrandInfoResult.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	//红外码服务
	public String irHttpRequest(String interfaceName, String httpBody){
		String url = BLApiUrlConstants.CLOUD_NEW_BASE  + "/publicircode/" + interfaceName;

		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		return postAccessor.execute(url, baseHeadParam, httpBody, String.class);
	}

	//上传文件
	public String upLoadFile(String interfaceName, String path){
		String url = BLApiUrlConstants.CLOUD_NEW_BASE  + "/publicircode/" + interfaceName;

		File file = new File(path);
		if(!file.exists()) return null;

		FilePostParam postParam = new FilePostParam();
		postParam.setFile(new File(path));

		UserHeadParam headParam = new UserHeadParam();
		headParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
		headParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());

		BLHttpPostFileAccessor httpAccessor = new BLHttpPostFileAccessor(mContext);
		return httpAccessor.execute(url, headParam, postParam, String.class);
	}

	private UserHeadParam irHttpCommonHeadParam(String url){
		UserHeadParam baseHeadParam = new UserHeadParam();
		baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setLanguage(BLCommonUtils.getLanguage());
		baseHeadParam.setLicenseid(BLLet.getLicenseId());
		baseHeadParam.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
		baseHeadParam.setSign(url.replaceAll("\\?.+",""), null);
		baseHeadParam.setLocate(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getCountryCode());
		return baseHeadParam;
	}

}
