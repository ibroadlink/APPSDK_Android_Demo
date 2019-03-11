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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitDirResult;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.db.dao.DNAKitDirInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLProductPresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;

import static cn.com.broadlink.blappsdkdemo.common.BLDevSrvConstans.Protocol.RM_CLOUD_IRCODE_PRODUCT;


public class ProductCategoryListActivity extends TitleActivity {
    //常用产品列表保存SD卡文件名称
    private static final String OFTEN_DEVICE_LIST_FILE = "OftenProduct";

    private LinearLayout mOftenLayout;
    private GridView mOftenGridView, mProduceListGridView;
    private TextView mProductTitle;

    private BLProductPresenter mProductManager;
    private List<ProductInfoResult.ProductDninfo> mOftemList = new ArrayList<>();
    private List<DNAKitDirInfo> mAddDeviceList = new ArrayList<>();

    private ClassifyAdapter mClassifyAdapter;
    private ProductAdapter mOftenAdapter;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_layout);

        initData();

        mProductManager = BLProductPresenter.getInstance();

        finView();

        setListener();

        initView();
    }

    private void initData() {
        setTitle( "Product List");
        
        if(BLLocalFamilyManager.getInstance().getCurrentFamilyId()==null){
            BLToastUtils.show("Please login and select a family first!");
            back();
        }
    }

    private void initView() {
        setBackWhiteVisible();
        mClassifyAdapter = new ClassifyAdapter(ProductCategoryListActivity.this, mAddDeviceList);
        mProduceListGridView.setAdapter(mClassifyAdapter);

        mOftenAdapter = new ProductAdapter(ProductCategoryListActivity.this, mOftemList);
        mOftenGridView.setAdapter(mOftenAdapter);

        queryColumnList();
    }

    private void finView() {
        mOftenLayout = (LinearLayout) findViewById(R.id.often_layout);
        mProduceListGridView = (GridView) findViewById(R.id.product_listview);
        mOftenGridView = (GridView) findViewById(R.id.often_listview);
        mProductTitle = (TextView) findViewById(R.id.tv_product_title);
    }

    private void setListener() {

        mProduceListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toProductListActivirt(mAddDeviceList.get(position));
            }
        });

        mOftenGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new QueryModelDetailTask().execute(mOftemList.get(position).getPid());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetDNAKitDirListTask().execute();
    }

    private void toProductListActivirt(DNAKitDirInfo categoryInfo) {
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_VALUE, categoryInfo);
        intent.putExtra(BLConstants.INTENT_NAME, categoryInfo.getName());
        intent.setClass(ProductCategoryListActivity.this, ProductBrandListActivity.class);
        startActivity(intent);
    }

    //获取DNA产品目录
    private class GetDNAKitDirListTask extends AsyncTask<Void, Void, GetDNAKitDirResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         showProgressDialog("Loading...");
        } 
        
        @Override
        protected GetDNAKitDirResult doInBackground(Void... params) {
            return mProductManager.queryProductCategoryList(ProductCategoryListActivity.this, null);
        }

        @Override
        protected void onPostExecute(GetDNAKitDirResult result) {
            super.onPostExecute(result);

            dismissProgressDialog();
            if (result != null && result.getStatus() == BLHttpErrCode.SUCCESS) {
                if (result.getCategorylist() != null) {
                    saveCategoryList(result.getCategorylist());
                }
                if (result.getHotproducts() != null) {
                    //保存常用设备
                    String filePath = BLStorageUtils.PRODUCT_LIST_PATH + OFTEN_DEVICE_LIST_FILE + BLCommonUtils.getLanguage();
                    String content = "";
                    List<ProductInfoResult.ProductDninfo> hotProdcuts = new ArrayList<>();
                    if (result.getHotproducts() != null) {
                        //过滤pid和保存映射列表
                        for (ProductInfoResult.ProductDninfo productDninfo : result.getHotproducts()) {
                            if (TextUtils.isEmpty(productDninfo.getMappid())) {
                                hotProdcuts.add(productDninfo);
                            } else if (productDninfo.getMappid().equals(productDninfo.getPid())) {
                                hotProdcuts.add(productDninfo);
                            }
                        }
                    }
                    content = JSON.toJSONString(hotProdcuts);

                    BLFileUtils.saveStringToFile(content, filePath);
                }

                queryColumnList();
            }
        }
    }

    //获取产品详情
    private class QueryModelDetailTask extends AsyncTask<String, Void, ProductInfoResult> {
        BLProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = BLProgressDialog.createDialog(ProductCategoryListActivity.this, R.string.str_common_loading);
            progressDialog.show();
        }

        @Override
        protected ProductInfoResult doInBackground(String... params) {
            String pid = params[0];
            return mProductManager.queryProducInfo(ProductCategoryListActivity.this, pid);
        }

        @Override
        protected void onPostExecute(ProductInfoResult result) {
            super.onPostExecute(result);
            if (ProductCategoryListActivity.this.isFinishing()) {
                return;
            }
            progressDialog.dismiss();
            if (result !=null && result.getStatus() == BLHttpErrCode.SUCCESS) {
               toConfigActivity(result.getProductinfo());
            }
        }
    }

    public void toConfigActivity(ProductInfoResult.ProductDninfo productinfo) {
        toConfigWiFiDev(productinfo);
    }
    

    public void toConfigWiFiDev(ProductInfoResult.ProductDninfo productinfo) {
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_VALUE, productinfo);
        intent.setClass(mActivity, ProductConfigDetailActivity.class);
        mActivity.startActivity(intent);
    }
    
    //保存分类列表
    private void saveCategoryList(List<DNAKitDirInfo> categoryList) {
        try {
            DNAKitDirInfoDao dao = new DNAKitDirInfoDao(getHelper());
            dao.createList(categoryList, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //查询添加的功能模块下的列表
    private void queryColumnList() {
        try {
            DNAKitDirInfoDao dao = new DNAKitDirInfoDao(getHelper());
            mAddDeviceList.clear();
            mAddDeviceList.addAll(dao.queryList(null));
            if (mAddDeviceList.size() > 0){
                mProductTitle.setVisibility(View.VISIBLE);
            } else {
                mProductTitle.setVisibility(View.INVISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //刷新常用设备
        mOftemList.clear();
        String filePath = BLStorageUtils.PRODUCT_LIST_PATH + OFTEN_DEVICE_LIST_FILE + BLCommonUtils.getLanguage();
        String content = BLFileUtils.getStringByFile(filePath);
        if (!TextUtils.isEmpty(content)) {
           List oftemProductsList =  JSON.parseArray(content, ProductInfoResult.ProductDninfo.class);
           //去除协议为14的设备
            Iterator<ProductInfoResult.ProductDninfo> iteator = oftemProductsList.iterator();
            while(iteator.hasNext()){
                ProductInfoResult.ProductDninfo productDninfo = iteator.next();
                if(RM_CLOUD_IRCODE_PRODUCT.equals(productDninfo.getProtocol())){
                    iteator.remove();
                }
            }
            mOftemList.addAll(oftemProductsList);
        }


        refreshView();
    }

    private void refreshView() {
        mOftenLayout.setVisibility(!mOftemList.isEmpty() ? View.VISIBLE : View.GONE);

        mOftenAdapter.notifyDataSetChanged();
        mClassifyAdapter.notifyDataSetChanged();
    }

    //添加设备的适配器
    private class ClassifyAdapter extends ArrayAdapter<DNAKitDirInfo> {
        private BLImageLoaderUtils mBlImageLoaderUtils;

        public ClassifyAdapter(Context context, List<DNAKitDirInfo> objects) {
            super(context, 0, 0, objects);
            mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.product_classify_list_item_layout, null);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.add_device_name_view);
                viewHolder.deviceIcon = (ImageView) convertView.findViewById(R.id.add_device_icon_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.deviceName.setText(getItem(position).getName());
            mBlImageLoaderUtils.displayImage(BLCommonUtils.dnaKitIconUrl(getItem(position).getLink()), viewHolder.deviceIcon, null);

            return convertView;
        }

        private class ViewHolder {
            TextView deviceName;
            ImageView deviceIcon;
        }
    }

    //添加设备的适配器
    private class ProductAdapter extends ArrayAdapter<ProductInfoResult.ProductDninfo> {
        private BLImageLoaderUtils mBlImageLoaderUtils;

        public ProductAdapter(Context context, List<ProductInfoResult.ProductDninfo> objects) {
            super(context, 0, 0, objects);
            mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.product_list_item_layout, null);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.add_device_name_view);
                viewHolder.deviceIcon = (ImageView) convertView.findViewById(R.id.add_device_icon_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.deviceName.setText(getItem(position).getModel());
            mBlImageLoaderUtils.displayImage(BLCommonUtils.dnaKitIconUrl(getItem(position).getIcon()), viewHolder.deviceIcon, null);

            return convertView;
        }

        private class ViewHolder {
            TextView deviceName;
            ImageView deviceIcon;
        }
    }
}
