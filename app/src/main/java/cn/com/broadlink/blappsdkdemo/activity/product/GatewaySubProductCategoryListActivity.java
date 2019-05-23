package cn.com.broadlink.blappsdkdemo.activity.product;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLDevSrvConstans;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.common.BLProfileTools;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.data.CustomDefaultConfigInfo;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitDirResult;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.data.UserBrandInfoResult;
import cn.com.broadlink.blappsdkdemo.db.dao.DNAKitDirInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLProductPresenter;
import cn.com.broadlink.blappsdkdemo.presenter.BLIRServicePresenter;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

import static cn.com.broadlink.blappsdkdemo.common.BLDevSrvConstans.Category.RM_CLOUD_LIGHT_SWITCH;
import static cn.com.broadlink.blappsdkdemo.common.BLDevSrvConstans.Category.RM_CLOUD_SWITCH;


/***
 * 网关下子设备产品分类页面
 * Project: BLEControlAppV4</p>
 * Title: GatewaySubProductCategoryListActivity</p>
 * Company: BroadLink</p>
 * @author YeJing
 * @date 2015-10-12
 */
public class GatewaySubProductCategoryListActivity extends TitleActivity {
	private ApplianceAdapter mApplianceAdapter;

	private GridView mApplianceGridView;

	private BLDNADevice mDeviceInfo;

	private TextView mTVCooperation;

	private List<Object> mAddDeviceList = new ArrayList<>();

	private final int REQUEST_EDIT = 101;

	private String mTitle;

	private BLDevProfileInfo mProfileInfo;
	
	private BLProductPresenter mBLProductManager;

	/** 自定义空调 **/
	public static final int RM_CUSTOM_AC = 23;
	/** RM AC 模块 **/
	public static final int RM_AC = 10;
	/** RM 新电视 集成频道内容 **/
	public static final int RM_NEW_TV = 24;
	/** RM 新机顶盒 集成频道内容**/
	public static final int RM_NEW_STB = 25;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rm_temp_list_layout);
		setBackWhiteVisible();

		mBLProductManager = BLProductPresenter.getInstance();
		
		initIntentData();

		findView();

		setListener();

		initView();

		initData();
	}

	private void initIntentData(){
		//必须传
		mDeviceInfo =  getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);

		mProfileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());

		mTitle = getIntent().getStringExtra(BLConstants.INTENT_TITLE);
	}

	private void findView() {
		mApplianceGridView = (GridView) findViewById(R.id.often_listview);

		mTVCooperation = (TextView) findViewById(R.id.tv_cooperation);
		mApplianceAdapter = new ApplianceAdapter(GatewaySubProductCategoryListActivity.this, mAddDeviceList);
	}

	private void initView(){
		mApplianceGridView.setAdapter(mApplianceAdapter);

		if(mProfileInfo == null || !BLDevSrvConstans.isRMCategory(mProfileInfo.getSrvs())){
			mTVCooperation.setVisibility(View.GONE);
		}else{
			mTVCooperation.setVisibility(View.VISIBLE);
		}

		if(!TextUtils.isEmpty(mTitle)){
			setTitle(mTitle);
		}else{
			setTitle(R.string.str_devices_appliance_type);
		}
	}

	private void setListener() {
		mApplianceGridView.setOnItemClickListener(new OnSingleItemClickListener() {
			@Override
			public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
				toActivity(mAddDeviceList.get(position));
			}
		});

		mApplianceGridView.setOnItemClickListener(new OnSingleItemClickListener() {
			@Override
			public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
				toActivity(mAddDeviceList.get(position));
			}
		});
	}

	private void initData(){
		queryColumnList();
        if(mProfileInfo == null || !BLDevSrvConstans.isRMCategory(mProfileInfo.getSrvs())){
			new GetDNAKitDirListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
		}
	}


	//获取DNA产品目录
	private class GetDNAKitDirListTask extends AsyncTask<Void, Void, GetDNAKitDirResult> {

		@Override
		protected GetDNAKitDirResult doInBackground(Void... params) {
			BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());
			//如果产品Profile为null，下载脚本
			if(profileInfo == null){
				profileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());
			}

			if(profileInfo != null){
				return mBLProductManager.queryProductCategoryList(GatewaySubProductCategoryListActivity.this, profileInfo.getProtocol());
			}

			return null;
		}

		@Override
		protected void onPostExecute(GetDNAKitDirResult result) {
			super.onPostExecute(result);
			if(GatewaySubProductCategoryListActivity.this.isFinishing()) return;

			if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS) {
				saveCategoryList(result);
				queryColumnList();
			}
		}
	}

	private void saveCategoryList(GetDNAKitDirResult result){
		BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());

		if(profileInfo != null){
			//保存分类列表
			if(result.getCategorylist() != null){
				List<DNAKitDirInfo> categroyList = result.getCategorylist();
				try {
					DNAKitDirInfoDao dao = new DNAKitDirInfoDao(getHelper());
					dao.createList(categroyList, profileInfo.getProtocol());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			//保存热门产品列表
			if(result.getHotproducts() != null){
				mBLProductManager.setHotproductsToFile(result.getHotproducts(), profileInfo.getProtocol());
			}
		}

	}

	private void addRmProudctFromLocal(){
		String rmProuduct = null;
		rmProuduct = BLFileUtils.readAssetsFile(GatewaySubProductCategoryListActivity.this, "rm_product_en.json");

		if(rmProuduct != null){
			GetDNAKitDirResult result = JSON.parseObject(rmProuduct,GetDNAKitDirResult.class);
			if(result != null){
				List<ProductInfoResult.ProductDninfo> productInfos = result.getHotproducts();
				Collections.sort(productInfos, new Comparator<ProductInfoResult.ProductDninfo>(){
					public int compare(ProductInfoResult.ProductDninfo p1, ProductInfoResult.ProductDninfo p2) {
						return p1.getRank() - p2.getRank();
					}
				});
				mAddDeviceList.addAll(productInfos);
				mAddDeviceList.addAll(10,result.getCategorylist());
			}
		}
	}

	//查询添加的功能模块下的列表
	private void queryColumnList() {
		BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());
		if(profileInfo != null){
			mAddDeviceList.clear();
			//RM希望加入的电器类型
			if(BLDevSrvConstans.isRMCategory(profileInfo.getSrvs())){
				addRmProudctFromLocal();
				String more = "more";
				mAddDeviceList.add(more);
			}else{
				//查询热门产品
				List<ProductInfoResult.ProductDninfo> productList = mBLProductManager.getHotproductsByFile(profileInfo.getProtocol());
				if(productList != null){
					mAddDeviceList.addAll(productList);
				}

				//查询分类列表
				try {
					DNAKitDirInfoDao dao = new DNAKitDirInfoDao(getHelper());
					List<DNAKitDirInfo> categoryInfoList = dao.queryList(profileInfo.getProtocol());
					mAddDeviceList.addAll(categoryInfoList);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		mApplianceAdapter.notifyDataSetChanged();

	}

    private void toActivity(Object product) {
		if(mProfileInfo == null || !BLDevSrvConstans.isRMCategory(mProfileInfo.getSrvs())){
			toConfig(product);
			return;
		}
		if(product instanceof DNAKitDirInfo){//添加子设备
			DNAKitDirInfo dnaKitDirInfo = (DNAKitDirInfo) product;
			new QueryUserBackUpData().execute(dnaKitDirInfo);
		}else if(product instanceof ProductInfoResult.ProductDninfo){ //添加RM类设备
			ProductInfoResult.ProductDninfo productDninfo = (ProductInfoResult.ProductDninfo) product;
			new QueryUserBackUpData().execute(productDninfo);
		}
    }

	private void toConfig(Object product) {
		if(product instanceof DNAKitDirInfo){//添加子设备
			Intent intent = new Intent();
			intent.putExtra(BLConstants.INTENT_DEVICE, mDeviceInfo);
			DNAKitDirInfo dnaKitDirInfo = (DNAKitDirInfo) product;
			intent.putExtra(BLConstants.INTENT_VALUE, dnaKitDirInfo);
			intent.setClass(GatewaySubProductCategoryListActivity.this, ProductBrandListActivity.class);
			startActivity(intent);
		}else if(product instanceof ProductInfoResult.ProductDninfo){
			
		}
	}

    private class ApplianceAdapter extends ArrayAdapter<Object> {

		BLImageLoaderUtils blImageLoaderUtils;

        public ApplianceAdapter(Context context, List<Object> objects) {
            super(context, 0, 0, objects);
			blImageLoaderUtils = BLImageLoaderUtils.getInstence(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.product_classify_list_item_layout, null);
                viewHolder.tempName = (TextView) convertView.findViewById(R.id.add_device_name_view);
                viewHolder.tempIcon = (ImageView) convertView.findViewById(R.id.add_device_icon_view);
                viewHolder.tempMore = (TextView) convertView.findViewById(R.id.tv_more);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(getItem(position) instanceof DNAKitDirInfo){
				viewHolder.tempName.setVisibility(View.VISIBLE);
				viewHolder.tempIcon.setVisibility(View.VISIBLE);
				viewHolder.tempMore.setVisibility(View.GONE);
				DNAKitDirInfo dnaKitDirInfo = (DNAKitDirInfo) getItem(position);
				viewHolder.tempName.setText(dnaKitDirInfo.getName());
				blImageLoaderUtils.displayImage(BLCommonUtils.dnaKitIconUrl(dnaKitDirInfo.getLink()), viewHolder.tempIcon, null);
			}else  if(getItem(position) instanceof ProductInfoResult.ProductDninfo){
				viewHolder.tempName.setVisibility(View.VISIBLE);
				viewHolder.tempIcon.setVisibility(View.VISIBLE);
				viewHolder.tempMore.setVisibility(View.GONE);
				ProductInfoResult.ProductDninfo productDninfo = (ProductInfoResult.ProductDninfo) getItem(position);
				viewHolder.tempName.setText(productDninfo.getName());
				Glide.with(GatewaySubProductCategoryListActivity.this).load(BLCommonUtils.dnaKitIconUrl(productDninfo.getIcon())).diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.tempIcon);
			}else if (getItem(position) instanceof String){
				viewHolder.tempName.setVisibility(View.GONE);
				viewHolder.tempIcon.setVisibility(View.GONE);
				viewHolder.tempMore.setVisibility(View.VISIBLE);
			}

            return convertView;
        }

        private class ViewHolder {
            TextView tempName;
            ImageView tempIcon;
            TextView tempMore;
        }
    }

    //查询备份的数据
    private class QueryUserBackUpData extends AsyncTask<Object,Void ,Void> {
    	private BLProgressDialog progressDialog;
		private List<UserBrandInfoResult.UserBrandInfo> meUserBrandData = new ArrayList<>();
		private Object product;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = BLProgressDialog.createDialog(GatewaySubProductCategoryListActivity.this,getString(R.string.str_common_loading));
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Object... products) {
			product = products[0];
			BLIRServicePresenter blirServicePresenter = new BLIRServicePresenter(GatewaySubProductCategoryListActivity.this);
			UserBrandInfoResult userBrandInfoResult = blirServicePresenter.queryUserBrand(getPids(product),null, BLApplication.mBLUserInfoUnits.getUserid());
			if(userBrandInfoResult != null && userBrandInfoResult.getStatus() == BLHttpErrCode.SUCCESS){
				if(userBrandInfoResult.getIrcodeList() != null && userBrandInfoResult.getIrcodeList().size() > 0){
					meUserBrandData.addAll(userBrandInfoResult.getIrcodeList());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
			toConfig(product);
		}

		private String getPid(Object product){
			if(product instanceof ProductInfoResult.ProductDninfo){
				ProductInfoResult.ProductDninfo productDninfo = (ProductInfoResult.ProductDninfo)product;
				String category = BLDevSrvConstans.getDevCategory(productDninfo.profileDetailInfo().getSrvs());
				if (category.equals(BLDevSrvConstans.Category.RM_CLOUD_TV )) {
					return BLCommonUtils.rmModuleType2Pid(RM_NEW_TV);
				}else if(category.equals(BLDevSrvConstans.Category.RM_CLOUD_STB)){
					return BLCommonUtils.rmModuleType2Pid(RM_NEW_STB);
				}else if(BLDevSrvConstans.isCustomCommonCategory(category)){
					return productDninfo.getPid();
				}
				return null;
			}else if(product instanceof DNAKitDirInfo){
				DNAKitDirInfo dnaKitProductInfo = (DNAKitDirInfo)product;
				return getPidFromAsset(dnaKitProductInfo.getCategoryid());
			}
			return null;
		}

		private List<String> getPids(Object product){
			List<String> pids = new ArrayList<>();
			//开关需要查3路
			if(product instanceof DNAKitDirInfo){
				DNAKitDirInfo dnaKitDirInfo = (DNAKitDirInfo)product;
				String categoryId = dnaKitDirInfo.getCategoryid();
				if(categoryId.equals(RM_CLOUD_SWITCH) || categoryId.equals(RM_CLOUD_LIGHT_SWITCH)){
					String pid1 = getPidFromAsset("1",categoryId);
					String pid2 = getPidFromAsset("2",categoryId);
					String pid3 = getPidFromAsset("3",categoryId);
					pids.add(pid1);
					pids.add(pid2);
					pids.add(pid3);
					return pids;
				}
			}else {
				//空调需要查自定义空调和云空调
				ProductInfoResult.ProductDninfo productDninfo = (ProductInfoResult.ProductDninfo)product;
				String category = BLDevSrvConstans.getDevCategory(productDninfo.profileDetailInfo().getSrvs());
				if(category.equals(BLDevSrvConstans.Category.RM_CLOUD_AC)){
					String pid1 =  BLCommonUtils.rmModuleType2Pid(RM_AC);
					String pid2 =  BLCommonUtils.rmModuleType2Pid(RM_CUSTOM_AC);
					pids.add(pid1);
					pids.add(pid2);
					return pids;
				}
			}

			String pid = getPid(product);
			pids.add(pid);
			return pids;

		}

		private String getPidFromAsset(String categoryId){
			String defaultConfig = BLFileUtils.readAssetsFile(GatewaySubProductCategoryListActivity.this, "default_config.json");
			if(!TextUtils.isEmpty(defaultConfig)){
				CustomDefaultConfigInfo defaultConfigs = JSON.parseObject(defaultConfig, CustomDefaultConfigInfo.class);
				List<CustomDefaultConfigInfo.ProductsBean> productsBeans = defaultConfigs.getProducts();
				for(CustomDefaultConfigInfo.ProductsBean product : productsBeans){
					if(product.getCategoryid().equals(categoryId)){
						return product.getPid();
					}
				}
			}
			return null;
		}


		private String getPidFromAsset(String index, String categoryId){
			String defaultConfig = BLFileUtils.readAssetsFile(GatewaySubProductCategoryListActivity.this, "default_config.json");
			if(defaultConfig != null){
				CustomDefaultConfigInfo defaultConfigs = JSON.parseObject(defaultConfig, CustomDefaultConfigInfo.class);
				List<CustomDefaultConfigInfo.ProductsBean> productsBeans = defaultConfigs.getProducts();
				if(productsBeans != null && productsBeans.size() > 0){
					for(CustomDefaultConfigInfo.ProductsBean productsBean : productsBeans){
						if(productsBean.getCategoryid().equals(categoryId)){
							if("1".equals(index) && productsBean.getFunctionList().size() == 2){
								//1路开关
								return productsBean.getPid();
							}else if("2".equals(index) && productsBean.getFunctionList().size() == 6){
								//2路开关
								return productsBean.getPid();
							}else if("3".equals(index) && productsBean.getFunctionList().size() == 8){
								//3路开关
								return productsBean.getPid();
							}
						}
					}
				}
			}
			return "";
		}
	}

}
