package cn.com.broadlink.blappsdkdemo.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLRmBrandInfo;
import cn.com.broadlink.blappsdkdemo.data.CityInfo;
import cn.com.broadlink.blappsdkdemo.data.CloudAcBrandResponse;
import cn.com.broadlink.blappsdkdemo.data.FilePostParam;
import cn.com.broadlink.blappsdkdemo.data.GetIrCodeResult;
import cn.com.broadlink.blappsdkdemo.data.IrCodeDownloadParam;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.data.RmBrandListACResp;
import cn.com.broadlink.blappsdkdemo.data.RmBrandListCommonResp;
import cn.com.broadlink.blappsdkdemo.data.RmIrTreeResult;
import cn.com.broadlink.blappsdkdemo.data.RmStbProviderInfo;
import cn.com.broadlink.blappsdkdemo.data.RmTvCodeInfoResult;
import cn.com.broadlink.blappsdkdemo.data.UserBrandInfoResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpGetAccessor;
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
	public static final int BRAND_AC = 3;

	public static final int BRAND_TV = 1;

	public static final int BRAND_FAN = 9;

	public static final int BRAND_STB = 2;

	public static final int STB_CHANNEL = 0;

	public static final int BRAND_LAMP = 6;

	public static final int BRAND_PROJECTOR = 11;//投影仪

	public static final int BRAND_SMART_TV_BOX = 10;//智能电视盒子

	public static final int BRAND_DVD = 15;//DVD播放机

	public static final int BRAND_AMPLIFIER = 14;//功放

	public static final int BRAND_AIR_PURIFIER = 16;//空气净化器

	public static final int BRAND_AUTOMATIC_DOOR = 13;//自动门

	public static final int BRAND_AUDIO = 12;//音响
	
	private Context mContext;

	public BLIRServicePresenter(Context context){
		this.mContext = context;
	}

	//获取云码品牌列表
	public CloudAcBrandResponse queryCloudBrandList(int mBrandType) {
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_BRAND_LIST();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);
		JSONObject jsonBodyObj = new JSONObject();

		try {
			jsonBodyObj.put("devtypeid", mBrandType);
			String bodys = jsonBodyObj.toString();

			BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
			return postAccessor.execute(url, baseHeadParam, bodys, CloudAcBrandResponse.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	//获取运营商下的红码品牌列表
	public CloudAcBrandResponse queryProviderCloudBrandList() {
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_PROVIDER_BRAND_LIST();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);
		BLHttpGetAccessor postAccessor = new BLHttpGetAccessor(mContext);
		return postAccessor.execute(url, baseHeadParam, null, CloudAcBrandResponse.class);
	}

	/**
	 * 获取电视品牌下的红外下载地址信息
	 * @param brandType
	 * @param brandid
	 * @return
	 */
	public RmBrandListCommonResp queryTVIrCodeUrlList(int brandType, int brandid){
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_BRAND_CLASS_LIST();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		RmBrandListCommonResp.RmGetBrandSubListParam rmGetBrandSubListParam = new RmBrandListCommonResp.RmGetBrandSubListParam();
		rmGetBrandSubListParam.setBrandid(brandid);
		rmGetBrandSubListParam.setDevtypeid(brandType);

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		return postAccessor.execute(url.replaceAll("\\?.+", ""), baseHeadParam, JSON.toJSONString(rmGetBrandSubListParam), RmBrandListCommonResp.class);
	}

	/**
	 * 获取红码匹配树
	 * @param brandType
	 * @param brandId
	 * @return
	 */
	public RmIrTreeResult getIrCodeTree(int brandType, int brandId){
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_GET_IR_TREE();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		RmBrandListCommonResp.RmGetBrandSubListParam rmGetBrandSubListParam = new RmBrandListCommonResp.RmGetBrandSubListParam();
		rmGetBrandSubListParam.setCountrycode(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getCountryCode());
		rmGetBrandSubListParam.setBrandid(brandId);
		rmGetBrandSubListParam.setDevtypeid(brandType);

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		return postAccessor.execute(url, baseHeadParam, JSON.toJSONString(rmGetBrandSubListParam), RmIrTreeResult.class);
	}

	/**
	 * 下载红码
	 * @param irId
	 * @return
	 */
	public RmTvCodeInfoResult getIrCode(String irId){
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_GET_IR_BY_IRID();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		IrCodeDownloadParam param = new IrCodeDownloadParam();
		param.setIrcodeid(irId);

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		GetIrCodeResult result = postAccessor.execute(url, baseHeadParam, JSON.toJSONString(param), GetIrCodeResult.class);

		if (result!=null && result.isSuccess()){
			String jsonStr = new String(BLCommonTools.aesPKCS7PaddingDecryptToByte(BLCommonTools.aeskeyDecrypt(BLConstants.STR_RM_KEY_PF + result.getRandkey()),
					BLConstants.BYTES_RM_CODE_IV, result.getData()));
			RmTvCodeInfoResult tvCodeInfoResult = JSON.parseObject(jsonStr, RmTvCodeInfoResult.class);
			tvCodeInfoResult.setIrId(Long.parseLong(irId));
			return tvCodeInfoResult;
		}
		return null;
	}

	/**
	 * 获取云空调品牌下的红外下载地址信息
	 * @param brandType
	 * @param brandid
	 * @return
	 */
	public RmBrandListACResp queryACIrCodeUrlList(int brandType, int brandid){
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_BRAND_CLASS_LIST();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		RmBrandListCommonResp.RmGetBrandSubListParam rmGetBrandSubListParam = new RmBrandListCommonResp.RmGetBrandSubListParam();
		rmGetBrandSubListParam.setBrandid(brandid);
		rmGetBrandSubListParam.setDevtypeid(brandType);

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		return postAccessor.execute(url, baseHeadParam, JSON.toJSONString(rmGetBrandSubListParam), RmBrandListACResp.class);
	}

	/***
	 * 下载机顶盒红外码url列表
	 * @param mCityInfo
	 * @param mStbProviderInfo
	 * @param mStbBrandInfo
	 * @return
	 */
	public RmBrandListCommonResp queryStbIrCodeUrlList(CityInfo mCityInfo, RmStbProviderInfo mStbProviderInfo, BLRmBrandInfo mStbBrandInfo){
		String url = BLApiUrlConstants.IrdaAPI.CLOUD_PROVINCES_CODE_LIST();
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);

		JSONObject bodyObj = new JSONObject();
		try {
			bodyObj.put("devtypeid", BRAND_STB);
			if(mCityInfo != null){
				bodyObj.put("locateid", mCityInfo.getCity());
			}

			if(mStbProviderInfo != null){
				bodyObj.put("providerid", mStbProviderInfo.getProviderid());
			}

			if(mStbBrandInfo != null){
				bodyObj.put("brandid", mStbBrandInfo.getBrandid());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String bodyStr = bodyObj.toString();

		BLHttpPostAccessor postAccessor = new BLHttpPostAccessor(mContext);
		return postAccessor.execute(url, baseHeadParam, bodyStr, RmBrandListCommonResp.class);
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


	/**
	 * 下载红外码 JSON格式
	 * @param url
	 * @param irId
	 * @param randKey
	 * @return
	 */
	public RmTvCodeInfoResult downLoadIrCodeFormatJson(String url, int irId, String randKey){
		UserHeadParam baseHeadParam = irHttpCommonHeadParam(url);
		BLHttpGetAccessor httpGetAccessor = new BLHttpGetAccessor(mContext);
		httpGetAccessor.enableJsonLog(false);
		httpGetAccessor.setToastError(false);

		byte[] encryptData = httpGetAccessor.execute(url, baseHeadParam, null, byte[].class);
		if(encryptData == null){
			return  null;
		}

		String respnseJson = new String(encryptData);
		if (encryptData.length > 0 && !respnseJson.contains("error")) {// 解密
			String jsonStr =  new String(BLCommonTools.aesPKCS7PaddingDecryptToByte(BLCommonTools.aeskeyDecrypt(BLConstants.STR_RM_KEY_PF + randKey),
					BLConstants.BYTES_RM_CODE_IV, encryptData));
			if (!TextUtils.isEmpty(jsonStr) && !jsonStr.contains("error")) {
				// BLLog.d("download tv json", jsonStr + "");
				try {
					RmTvCodeInfoResult tvCodeInfoResult = JSON.parseObject(jsonStr, RmTvCodeInfoResult.class);
					tvCodeInfoResult.setIrId(irId);
					return tvCodeInfoResult;
				} catch (com.alibaba.fastjson.JSONException e) {
					e.printStackTrace();
				}
			}
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
