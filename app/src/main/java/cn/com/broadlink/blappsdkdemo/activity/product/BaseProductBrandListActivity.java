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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.data.DNAKitProductInfo;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLProductPresenter;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;

/**
 * 产品列表页面
 *
 * @author YeJing
 * @data 2017/11/30
 */

abstract class BaseProductBrandListActivity extends TitleActivity {
	private ListView mDeviceListView;

	private ArrayList<DNAKitProductInfo> mModelList = new ArrayList<>();

	private AddDeviceAdapter mAddDeviceAdapter;

	private BLImageLoaderUtils mBlImageLoaderUtils;

	protected BLProductPresenter mProductManager;

	public LinearLayout mSearchLayout;

	public EditText mEditText;

	public ImageView mClearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_layout);
		setBackWhiteVisible();

		setTitle(getIntent().getStringExtra(BLConstants.INTENT_NAME));

		mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(BaseProductBrandListActivity.this);
		mProductManager = BLProductPresenter.getInstance();

		mAddDeviceAdapter = new AddDeviceAdapter(BaseProductBrandListActivity.this, mModelList);

		findView();

		initView();

		setListener();

		loadProductList();
	}

	//获取产品列表
	public abstract void loadProductList();

	//获取品牌名称
	public String getBrandName(){
		return "";
	}

	//获取网关信息,子类可以重写
	public List<BLDeviceInfo> getGatewayDeviceInfo(){
		return null;
	}

	//取得空间
	private void findView() {
		mDeviceListView = (ListView) findViewById(R.id.listview);
		mSearchLayout = (LinearLayout) findViewById(R.id.search_title_layout);
		mEditText = (EditText)findViewById(R.id.et_put_identify);
		mClearButton = (ImageView)findViewById(R.id.iv_clear);
	}

	public void initView(){
		mDeviceListView.setDivider(null);

		mDeviceListView.setPadding(0, BLCommonUtils.dip2px(BaseProductBrandListActivity.this, 5),
				0, BLCommonUtils.dip2px(BaseProductBrandListActivity.this, 15));

		mDeviceListView.setAdapter(mAddDeviceAdapter);
	}

	//设置监听
	private void setListener() {
		mDeviceListView.setOnItemClickListener(new OnSingleItemClickListener() {
			@Override
			public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
				new QueryModelDetailTask().execute(mModelList.get(position));
			}
		});
	}

	private class ComparatorByDeviceType implements Comparator<DNAKitProductInfo> {

		@Override
		public int compare(DNAKitProductInfo sDeviceInfo, DNAKitProductInfo rDeviceInfo) {
			if (sDeviceInfo.getRank() < rDeviceInfo.getRank()) return -1;
			return 0;
		}
	}

	/**
	 * 子设备实现获取产品列表，并将产品列表设置刷新
	 * @param productList
	 */
	public void refreshProductList(ArrayList<DNAKitProductInfo> productList){
		if(productList == null) return;

		ArrayList<DNAKitProductInfo> cacheProductList = new ArrayList<>();
		//过滤pid和保存映射列表
		for (int i = 0; i < productList.size(); i++) {
			DNAKitProductInfo dnaKitProductInfo = productList.get(i);
			if(TextUtils.isEmpty(dnaKitProductInfo.getMappid())){
				cacheProductList.add(dnaKitProductInfo);
			}else if(dnaKitProductInfo.getMappid().equals(dnaKitProductInfo.getPid())){
				cacheProductList.add(dnaKitProductInfo);
			}
		}

		Collections.sort(cacheProductList, new ComparatorByDeviceType());

		mModelList.clear();
		mModelList.addAll(cacheProductList);
		mAddDeviceAdapter.notifyDataSetChanged();
	}

	//获取产品详情
	private class QueryModelDetailTask extends AsyncTask<DNAKitProductInfo, Void, ProductInfoResult> {
		BLProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = BLProgressDialog.createDialog(BaseProductBrandListActivity.this, R.string.str_common_loading);
			progressDialog.show();
		}

		@Override
		protected ProductInfoResult doInBackground(DNAKitProductInfo... params) {
			DNAKitProductInfo cloudCategoryInfo = params[0];
			return mProductManager.queryProducInfo(BaseProductBrandListActivity.this, cloudCategoryInfo.getPid());
		}

		@Override
		protected void onPostExecute(ProductInfoResult result) {
			super.onPostExecute(result);
			if (BaseProductBrandListActivity.this.isFinishing()) {
				return;
			}
			progressDialog.dismiss();
		    if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS) {
                toConfigWiFiDev(result.getProductinfo());
			}
		}
	}
	
    public void toConfigWiFiDev(ProductInfoResult.ProductDninfo productinfo) {
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_VALUE, productinfo);
        intent.setClass(mActivity, ProductConfigDetailActivity.class);
        mActivity.startActivity(intent);
    }

    //添加设备适配器
	private class AddDeviceAdapter extends ArrayAdapter<DNAKitProductInfo> {

		public AddDeviceAdapter(Context context, List<DNAKitProductInfo> objects) {
			super(context, 0, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.add_modle_info_list_item_layout, null);
				viewHolder.deviceIcon = (ImageView) convertView.findViewById(R.id.device_icon_view);
				viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name_view);
				viewHolder.deviceType = (TextView) convertView.findViewById(R.id.device_name_type);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			//魔法家显示展示名称
			String model =  getItem(position).getModel();
			String name = getItem(position).getName();
			String type = getItem(position).getBrandname();
			String alias = getItem(position).getAlias();

			if(!TextUtils.isEmpty(alias)){
				model = "【" + alias + "】" + model;
			}
			if(type.equals("")){
				type = name;
			}else{
				type = type + "  " + name;
			}

			viewHolder.deviceName.setText(model);
			viewHolder.deviceType.setText(type);

			mBlImageLoaderUtils.displayImage(BLCommonUtils.dnaKitIconUrl(getItem(position).getShortcuticon()), viewHolder.deviceIcon, null);

			return convertView;
		}

		private class ViewHolder {
			ImageView deviceIcon;
			TextView deviceName;
			TextView deviceType;
		}
	}
}