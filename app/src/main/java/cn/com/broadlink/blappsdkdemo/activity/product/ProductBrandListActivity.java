package cn.com.broadlink.blappsdkdemo.activity.product;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitProductListResult;
import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;


/**
 * 项目名称：BLEControlAppV4
 * <br>类名称：DeviceAddListActivity
 * <br>类描述： 添加的设备列表页面
 * <br>创建人：YeJing
 * <br>创建时间：2015-5-6 下午2:59:34
 * <br>修改人：Administrator
 * <br>修改时间：2015-5-6 下午2:59:34
 * <br>修改备注：
 */
public class ProductBrandListActivity extends BaseProductBrandListActivity {
	private DNAKitDirInfo mCategoryInfo;

	@Override
	public void loadProductList() {
		mCategoryInfo = (DNAKitDirInfo) getIntent().getSerializableExtra(BLConstants.INTENT_VALUE);

		initData();

		new QueryProductTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
	}

	//加载本地文件数据
	private void initData() {
		if (mCategoryInfo != null) {
			String path = BLStorageUtils.PRODUCT_LIST_PATH + File.separator + mCategoryInfo.getCategoryid();
			String content = BLFileUtils.getStringByFile(path);
			if (!TextUtils.isEmpty(content)) {
				GetDNAKitProductListResult productInfoResult = JSON.parseObject(content, GetDNAKitProductListResult.class);
				if (productInfoResult != null && productInfoResult.getStatus() == BLHttpErrCode.SUCCESS) {
					refreshProductList(productInfoResult.getProductlist());
				}
			}
		}
	}

	//获取产品列表
	private class QueryProductTask extends AsyncTask<Void, Void, GetDNAKitProductListResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog("Query product list...");
		}

		@Override
		protected GetDNAKitProductListResult doInBackground(Void... params) {
			return mProductManager.queryProductList(ProductBrandListActivity.this, mCategoryInfo.getCategoryid(), null);
		}

		@Override
		protected void onPostExecute(GetDNAKitProductListResult result) {
			super.onPostExecute(result);
			if (ProductBrandListActivity.this.isFinishing()) return;
			
			dismissProgressDialog();
			
			if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS) {
				saveProductToFile(result);
				refreshProductList(result.getProductlist());
			}
		}

		private void saveProductToFile(GetDNAKitProductListResult result){
			BLFileUtils.saveStringToFile(JSON.toJSONString(result), BLStorageUtils.PRODUCT_LIST_PATH + File.separator + mCategoryInfo.getCategoryid());
		}
	}
}
