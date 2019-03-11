package cn.com.broadlink.blappsdkdemo.mvp.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.data.BatchQueryProductModel;
import cn.com.broadlink.blappsdkdemo.data.BatchQueryProductResult;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitDirParam;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitDirResult;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitProductListParam;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitProductListResult;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitProductParam;
import cn.com.broadlink.blappsdkdemo.data.GetProductBrandListResult;
import cn.com.broadlink.blappsdkdemo.data.GetQrCodeProductParam;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.mvp.model.BLProductModel;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.sdk.BLLet;

/**
 * 云端相关产品管理接口类
 *
 * 	支持产品列表，信息，品牌查询
 *
 * @author YeJing
 * @data 2018/1/10
 */
public class BLProductPresenter implements BLProductModel {
	private static BLProductPresenter mBLProductManager;

	private HashMap<String, ProductInfoResult> mPidProductMap = new HashMap<>();

	private BLProductPresenter(){}

	public static BLProductPresenter getInstance(){
		if(mBLProductManager == null){
			mBLProductManager = new BLProductPresenter();
		}
		return mBLProductManager;
	}

	@Override
	public <T> T httpRequest(Context context, String url, String bodyStr, Class<T> clazz, boolean toastShowError) {
		UserHeadParam baseHeadParam = new UserHeadParam();
		baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setLanguage(BLCommonUtils.getLanguage());
		baseHeadParam.setLicenseid(BLLet.getLicenseId());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());
		final String countryCode = BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getCountryCode();
		
		baseHeadParam.setCountryCode(TextUtils.isEmpty(countryCode) ? "1" : countryCode);
		baseHeadParam.setLocate(TextUtils.isEmpty(countryCode) ? "1" : countryCode);

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(context);
		httpPostAccessor.setToastError(toastShowError);
		return httpPostAccessor.execute(url, baseHeadParam, bodyStr,clazz);
	}

	public <T> T httpRequest(Context context, String url, String bodyStr, Class<T> clazz) {
		return httpRequest(context, url, bodyStr, clazz,true);
	}

	@Override
	public GetDNAKitDirResult queryProductCategoryList(Context context, List<String> protocol) {
		GetDNAKitDirParam getDNAKitDirParam = new GetDNAKitDirParam();
		getDNAKitDirParam.setProtocols(protocol);
		getDNAKitDirParam.setBrandid("");
		return httpRequest(context, BLApiUrlConstants.AppManager.ADD_PRODUCT_DIRECTORY(), JSON.toJSONString(getDNAKitDirParam), GetDNAKitDirResult.class);
	}

	@Override
	public GetDNAKitProductListResult queryProductList(Context context, String categoryId, List<String> protocol) {
		return queryProductList(context, categoryId, null, protocol);
	}

	@Override
	public GetDNAKitProductListResult queryProductList(Context context, String categoryid, String brandName, List<String> protocol) {
		GetDNAKitProductListParam productParam = new GetDNAKitProductListParam();
		productParam.setCategoryid(categoryid);
		productParam.setBrandid("");
		productParam.setProtocols(protocol);
		productParam.setBrandname(brandName);

		String url = TextUtils.isEmpty(brandName) ? BLApiUrlConstants.AppManager.ADD_PRODUCT_LIST() :  BLApiUrlConstants.AppManager.QUERY_PRODUCT_LIST_BY_BRADN();
		return httpRequest(context, url, JSON.toJSONString(productParam), GetDNAKitProductListResult.class);
	}

	@Override
	public GetProductBrandListResult queryBrandList(Context context, String categoryid, List<String> protocol) {
		GetDNAKitProductListParam productParam = new GetDNAKitProductListParam();
		productParam.setCategoryid(categoryid);
		productParam.setBrandid("");
		productParam.setProtocols(protocol);
		return httpRequest(context, BLApiUrlConstants.AppManager.QUERY_BRADN_LIST(), JSON.toJSONString(productParam), GetProductBrandListResult.class);
	}

	@Override
	public ProductInfoResult queryQrProducInfo(Context context, String productQrCode) {
		GetQrCodeProductParam productParam = new GetQrCodeProductParam();
		productParam.setQrcode(productQrCode);
		return httpRequest(context, BLApiUrlConstants.AppManager.ADD_QR_PRODUCT_DETAIL(), JSON.toJSONString(productParam), ProductInfoResult.class);
	}

	@Override
	public ProductInfoResult queryProducInfo(Context context, String pid) {
		return queryProducInfo(context, pid, true);
	}

	@Override
	public ProductInfoResult queryProducInfo(Context context, String productId, boolean toastShowError) {
		//先从缓存获取数据
		ProductInfoResult productInfoResult = mPidProductMap.get(productId);
		if (productInfoResult != null) {
			return productInfoResult;
		}

		//查询本地是否缓存设备产品信息
		String path = BLStorageUtils.PRODUCT_INFO_PATH + File.separator + productId + BLCommonUtils.getLanguage();
		String content = BLFileUtils.getStringByFile(path);
		if (!TextUtils.isEmpty(content)) {
			try {
				productInfoResult = JSON.parseObject(content, ProductInfoResult.class);
				mPidProductMap.put(productId, productInfoResult);
				return productInfoResult;
			} catch (Exception e) {
			}
		}

		GetDNAKitProductParam param = new GetDNAKitProductParam();
		param.setPid(productId);
		param.setBrandid("");

		ProductInfoResult result = httpRequest(context, BLApiUrlConstants.AppManager.ADD_PRODUCT_DETAIL(), JSON.toJSONString(param), ProductInfoResult.class, toastShowError);
		if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS && result.getProductinfo() != null) {
			saveProductInfoToFileAndCache(context, result.getProductinfo());
		}

		return result;
	}

	@Override
	public void batchCheckDevListProductDetailVersion(Context context, List<String> pidList) {
		batchCheckDevListProductDetail(context, pidList, null);
	}

	@Override
	public void batchCheckDevListProductDetail(Context context, List<String> pidList, CheckListener listener) {
		new CheckProductDetailVersionTask(context, listener).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, pidList);
	}

	//保存产品信息到本地文件
	private void saveProductInfoToFileAndCache(Context context, ProductInfoResult.ProductDninfo productDninfo){
		//产品信息存储到本地
		ProductInfoResult productInfoResult = new ProductInfoResult();
		productInfoResult.setStatus(BLHttpErrCode.SUCCESS);
		productInfoResult.setProductinfo(productDninfo);
		BLFileUtils.saveStringToFile(JSON.toJSONString(productInfoResult), BLStorageUtils.PRODUCT_INFO_PATH + File.separator + productDninfo.getPid() + BLCommonUtils.getLanguage());

		//保存到缓存
		mPidProductMap.put(productDninfo.getPid(), productInfoResult);
	}

	private class CheckProductDetailVersionTask extends AsyncTask<List<String>, Void, BatchQueryProductResult> {
		private Context context;
		private CheckListener mListener;

		public CheckProductDetailVersionTask(Context context, CheckListener listener){
			this.context = context;
			mListener = listener;
		}

		@Override
		protected BatchQueryProductResult doInBackground(List<String>... params) {
			List<BatchQueryProductModel.ProductversionlistBean> dataParam = getProductInfo(context, params[0]);
			BatchQueryProductModel batchQueryProductModel = new BatchQueryProductModel();
			batchQueryProductModel.setBrandid("");
			batchQueryProductModel.setProductversionlist(dataParam);
			return httpRequest(context, BLApiUrlConstants.AppManager.BATCH_QUERY_PRODUCT_LIST(), JSON.toJSONString(batchQueryProductModel), BatchQueryProductResult.class);
		}

		@Override
		protected void onPostExecute(BatchQueryProductResult result) {
			super.onPostExecute(result);
			if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS) {
				List<ProductInfoResult.ProductDninfo> productDninfos = result.getAllproductinfo();
				if (productDninfos != null && productDninfos.size() > 0) {
					mPidProductMap.clear();
					for (ProductInfoResult.ProductDninfo productDninfo : productDninfos) {
						saveProductInfoToFileAndCache(context, productDninfo);
					}
				}
			}

			if (mListener != null){
				mListener.onResult(result);
			}
		}

		private List<BatchQueryProductModel.ProductversionlistBean> getProductInfo(Context context, List<String> pids) {
			List productInfoList = new ArrayList();
			for (String pid : pids) {
				BatchQueryProductModel.ProductversionlistBean productVersionBean = new BatchQueryProductModel.ProductversionlistBean();
				productVersionBean.setPid(pid);
				productVersionBean.setProductversion("");
				productInfoList.add(productVersionBean);
			}
			return productInfoList;
		}
	}

	//保存热门产品列表
	public void setHotproductsToFile(List<ProductInfoResult.ProductDninfo> hotproducts, List<String> protocols){
		if(hotproducts != null) {
			StringBuffer key = new StringBuffer("hotproducts-");
			key.append(toProtocolStr(protocols));
			key.append(BLCommonUtils.getLanguage());
			BLFileUtils.saveStringToFile(JSON.toJSONString(hotproducts), BLStorageUtils.CACHE_PATH + File.separator + key);
		}
	}

	//获取热门产品列表
	public List<ProductInfoResult.ProductDninfo> getHotproductsByFile(List<String> protocols){
		StringBuffer key = new StringBuffer("hotproducts-");
		key.append(toProtocolStr(protocols));
		key.append(BLCommonUtils.getLanguage());
		String content = BLFileUtils.readTextFileContent(BLStorageUtils.CACHE_PATH + File.separator + key);
		if(content != null) {
			return JSON.parseArray(content, ProductInfoResult.ProductDninfo.class);
		}

		return null;
	}

	private String toProtocolStr(List<String> protocolList){
		if(protocolList != null){
			StringBuffer protocolStr = new StringBuffer();
			for (String protocol : protocolList) {
				protocolStr.append(protocol);
				protocolStr.append("_");
			}

			return protocolStr.toString();
		}
		return null;
	}
}
